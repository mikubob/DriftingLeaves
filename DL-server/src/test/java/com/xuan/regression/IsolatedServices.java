package com.xuan.regression;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.xuan.config.*;
import com.xuan.handle.MyMetaObjectHandler;
import com.xuan.properties.*;
import com.xuan.service.*;
import com.xuan.service.impl.*;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.*;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.net.ServerSocket;
import java.nio.file.*;
import java.sql.*;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import static org.mockito.Mockito.mock;

/** Starts only new child services on loopback with fresh data; never reads Spring application profiles. */
final class IsolatedServices implements AutoCloseable {
    private Process mysql,redis;
    private final Path directory;
    private final int mysqlPort,redisPort;
    AnnotationConfigApplicationContext context;
    static IsolatedServices active;

    IsolatedServices() throws Exception {
        directory=Files.createTempDirectory(Path.of("target").toAbsolutePath(),"isolated-services-");
        mysqlPort=freePort();redisPort=freePort();active=this;
        try {
            Path mysqld=Path.of(System.getProperty("isolated.mysql.executable","D:/CodeEnvironment/MySQL/mysql-8.0.34-winx64/bin/mysqld.exe")).toAbsolutePath();
            Path redisExe=Path.of(System.getProperty("isolated.redis.executable","D:/CodeEnvironment/Redis/redis-server.exe")).toAbsolutePath();
            Path mysqlData=directory.resolve("mysql");Files.createDirectory(mysqlData);
            Process initialize=new ProcessBuilder(mysqld.toString(),"--no-defaults","--initialize-insecure","--datadir="+mysqlData,"--basedir="+mysqld.getParent().getParent())
                    .redirectErrorStream(true).redirectOutput(directory.resolve("mysql-initialize.log").toFile()).start();
            if(!initialize.waitFor(90,TimeUnit.SECONDS) || initialize.exitValue()!=0) {
                initialize.destroyForcibly();throw new IllegalStateException("Isolated MySQL initialization failed: "+directory);
            }
            mysql=new ProcessBuilder(mysqld.toString(),"--no-defaults","--datadir="+mysqlData,"--basedir="+mysqld.getParent().getParent(),
                    "--bind-address=127.0.0.1","--port="+mysqlPort,"--mysqlx=0","--skip-log-bin","--innodb-buffer-pool-size=64M","--performance-schema=OFF")
                    .redirectErrorStream(true).redirectOutput(directory.resolve("mysql.log").toFile()).start();
            Path redisData=directory.resolve("redis");Files.createDirectory(redisData);
            redis=new ProcessBuilder(redisExe.toString(),"--bind","127.0.0.1","--port",String.valueOf(redisPort),"--save","","--appendonly","no","--dir",redisData.toString())
                    .redirectErrorStream(true).redirectOutput(directory.resolve("redis.log").toFile()).start();
            awaitDatabase();
            try(Connection c=DriverManager.getConnection(jdbcUrl(""),"root","")) { c.createStatement().execute("CREATE DATABASE dl_isolated CHARACTER SET utf8mb4"); }
            context=new AnnotationConfigApplicationContext(Config.class);
            try(var c=context.getBean(DataSource.class).getConnection()) {
                ScriptUtils.executeSqlScript(c,new ClassPathResource("sql/schema.sql"));
                ScriptUtils.executeSqlScript(c,new ClassPathResource("sql/migrations/20261026_expand_social_icon.sql"));
                ScriptUtils.executeSqlScript(c,new ClassPathResource("sql/migrations/20261027_backend_integrity.sql"));
            }
            context.getBean(StringRedisTemplate.class).getConnectionFactory().getConnection().ping();
            System.out.println("Isolated services ready: "+directory+", mysql="+mysqlPort+", redis="+redisPort);
        } catch(Exception ex) { close();throw ex; }
    }
    private void awaitDatabase() throws Exception {
        long until=System.nanoTime()+Duration.ofSeconds(60).toNanos();
        while(System.nanoTime()<until) {
            if(!mysql.isAlive() || !redis.isAlive()) throw new IllegalStateException("Isolated child service exited: "+directory);
            try(var c=DriverManager.getConnection(jdbcUrl(""),"root","")) { return; }
            catch(SQLException ex) { Thread.sleep(200); }
        }
        throw new IllegalStateException("Isolated MySQL startup timed out: "+directory);
    }
    private String jdbcUrl(String db) { return "jdbc:mysql://127.0.0.1:"+mysqlPort+"/"+db+"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&connectTimeout=1000"; }
    private static int freePort() throws Exception { try(var socket=new ServerSocket(0,0,java.net.InetAddress.getLoopbackAddress())) { return socket.getLocalPort(); } }
    /** Stops only the child process trees created by this fixture, including the Windows MySQL server child. */
    @Override public void close() {
        try {
            if(context!=null) context.close();
        } finally {
            for(Process process:new Process[]{redis,mysql}) {
                if(process==null) continue;
                var descendants=process.descendants().toList();
                process.destroy();
                descendants.forEach(ProcessHandle::destroyForcibly);
                try {
                    if(!process.waitFor(10,TimeUnit.SECONDS)) process.destroyForcibly().waitFor(10,TimeUnit.SECONDS);
                    for(var child:descendants) child.onExit().get(10,TimeUnit.SECONDS);
                    if(process.isAlive()) throw new IllegalStateException("Isolated child did not stop: "+directory);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();process.destroyForcibly();
                    throw new IllegalStateException("Interrupted while stopping isolated services",ex);
                } catch(java.util.concurrent.ExecutionException | java.util.concurrent.TimeoutException ex) {
                    throw new IllegalStateException("Isolated descendant did not stop: "+directory,ex);
                }
            }
        }
    }

    @Configuration
    @org.springframework.cache.annotation.EnableCaching
    @org.springframework.scheduling.annotation.EnableAsync
    @EnableAspectJAutoProxy
    @EnableTransactionManagement(proxyTargetClass=true)
    @MapperScan("com.xuan.mapper")
    @Import({RedisConfiguration.class,MybatisPlusConfig.class,ArticleCommentServiceImpl.class,MessageServiceImpl.class,
            ArticleLikeServiceImpl.class,VisitorServiceImpl.class,ArticleServiceImpl.class,ArticleTagServiceImpl.class,
            RssSubscriptionServiceImpl.class,com.xuan.task.CounterSyncCoordinator.class,CounterBatchWriterImpl.class,com.xuan.mapper.ArticleCountReader.class,
            TokenServiceImpl.class,VerifyCodeServiceImpl.class,CaptchaServiceImpl.class,VisitorTokenServiceImpl.class,
            com.xuan.interceptor.VisitorTokenInterceptor.class,SkillServiceImpl.class,MusicServiceImpl.class,
            SocialMediaServiceImpl.class,ExperienceServiceImpl.class,FriendLinkServiceImpl.class,SystemConfigServiceImpl.class,
            PersonalInfoServiceImpl.class,ArticleCategoryServiceImpl.class,OperationLogServiceImpl.class,
            AdminServiceImpl.class,EncryptPasswordServiceImpl.class,SaveLogAsyncServiceImpl.class,com.xuan.aspect.OperationLogAspect.class})
    static class Config {
        @Bean java.util.concurrent.ExecutorService taskExecutor() { return java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor(); }
        @Bean IsolatedBackendTest.AuditProbe auditProbe() { return new IsolatedBackendTest.AuditProbe(); }
        @Bean DataSource dataSource() { return new DriverManagerDataSource(active.jdbcUrl("dl_isolated"),"root",""); }
        @Bean PlatformTransactionManager transactionManager(DataSource ds) { return new DataSourceTransactionManager(ds); }
        @Bean RedisConnectionFactory redisConnectionFactory() {
            LettuceConnectionFactory factory=new LettuceConnectionFactory("127.0.0.1",active.redisPort);
            return factory;
        }
        @Bean StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) { return new StringRedisTemplate(factory); }
        @Bean SqlSessionFactory sqlSessionFactory(DataSource ds,com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor interceptor) throws Exception {
            MybatisSqlSessionFactoryBean factory=new MybatisSqlSessionFactoryBean();factory.setDataSource(ds);
            MybatisConfiguration configuration=new MybatisConfiguration();configuration.setMapUnderscoreToCamelCase(true);
            factory.setConfiguration(configuration);factory.setPlugins(interceptor);
            GlobalConfig global=new GlobalConfig();global.setMetaObjectHandler(new MyMetaObjectHandler());factory.setGlobalConfig(global);
            factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
            return factory.getObject();
        }
        @Bean JwtProperties jwtProperties() { JwtProperties jwt=new JwtProperties();jwt.setSecretKey("isolated-test-key-which-is-longer-than-thirty-two-bytes");jwt.setTtl(600_000L);return jwt; }
        @Bean WebsiteProperties websiteProperties() { WebsiteProperties site=new WebsiteProperties();site.setTitle("Isolated Blog");site.setBlog("https://blog.example.test");return site; }
        @Bean VisitorProperties visitorProperties() { VisitorProperties p=new VisitorProperties();p.setVerifyCode("123456");return p; }
        @Bean EmailService emailService() { return mock(EmailService.class); }
        @Bean AsyncEmailService asyncEmailService() { return mock(AsyncEmailService.class); }
        @Bean UserAgentService userAgentService() { return new UserAgentServiceImpl(); }
        @Bean BlockService blockService() { return mock(BlockService.class); }
        @Bean AsyncVisitorService asyncVisitorService() { return mock(AsyncVisitorService.class); }
        @Bean LoginLockService loginLockService() { return mock(LoginLockService.class); }
    }
}
