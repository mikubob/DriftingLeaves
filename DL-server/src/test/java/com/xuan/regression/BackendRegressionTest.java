package com.xuan.regression;

import com.xuan.context.BaseContext;
import com.xuan.dto.*;
import com.xuan.exception.*;
import com.xuan.properties.*;
import com.xuan.service.*;
import com.xuan.service.impl.*;
import com.xuan.utils.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.*;
import org.springframework.test.util.ReflectionTestUtils;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.time.Duration;
import java.util.*;
import javax.imageio.ImageIO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BackendRegressionTest {
    @Test void unsubscribePreservesDatabaseFailuresInsteadOfCallingThemInvalidTokens() {
        JwtProperties jwt=new JwtProperties();jwt.setSecretKey("isolated-test-key-which-is-longer-than-thirty-two-bytes");
        var service=spy(new RssSubscriptionServiceImpl(mock(org.springframework.data.redis.core.StringRedisTemplate.class),
                mock(EmailService.class),new WebsiteProperties(),jwt,mock(com.xuan.mapper.VisitorMapper.class)));
        var sub=com.xuan.entity.RssSubscriptions.builder().id(1L).email("one@example.test").build();
        String token=service.unsubscribeToken(sub);
        var failure=new org.springframework.dao.DataAccessResourceFailureException("injected database outage");
        doThrow(failure).when(service).getById(1L);
        assertSame(failure,assertThrows(org.springframework.dao.DataAccessResourceFailureException.class,()->service.unsubscribe(token)));
        assertThrows(RssSubscriptionException.class,()->service.unsubscribe("broken"));
    }

    @Test void passwordsAcceptedAfterChangeAreAcceptedAtLogin() {
        try(var validator = jakarta.validation.Validation.buildDefaultValidatorFactory()) {
            for(String password: List.of("abc_123","Newpass1!","含空格 valid", "x".repeat(32))) {
                assertTrue(validator.getValidator().validate(AdminLoginDTO.builder().username("admin").password(password).code("123456").build()).isEmpty());
                assertTrue(validator.getValidator().validate(AdminChangePasswordDTO.builder().oldPassword("oldpass").newPassword(password).confirmNewPassword(password).build()).isEmpty());
            }
            assertFalse(validator.getValidator().validate(AdminLoginDTO.builder().username("admin").password("short").code("123456").build()).isEmpty());
        }
    }
    @Test void invalidReportDatesFailBeforeQueriesAndSameDayWorks() {
        IViewService views=mock(IViewService.class);
        IVisitorService visitors=mock(IVisitorService.class);
        ReportServiceImpl reports=new ReportServiceImpl(views,visitors,mock(IArticleCategoryService.class),mock(IArticleService.class),mock(IArticleTagService.class),mock(IArticleCommentService.class),mock(IMessageService.class));
        LocalDate today=LocalDate.of(2026,10,27);
        assertThrows(ValidationException.class,()->reports.getViewStatistics(today,today.minusDays(1)));
        assertThrows(ValidationException.class,()->reports.getVisitorStatistics(null,today));
        assertThrows(ValidationException.class,()->reports.getViewStatistics(today,today.plusYears(3)));
        verifyNoInteractions(views,visitors);
        when(views.getDailyViewStats(today,today)).thenReturn(List.of());
        assertEquals("2026-10-27",reports.getViewStatistics(today,today).getDateList());
    }
    @Test void plainTextMarkdownContactsAndMailHaveOneSafeContract() {
        String payload="<img src=x onerror='bad()'> & \"hello\"\nnext";
        assertTrue(DiscussionUtil.html(payload,0).contains("&lt;img"));
        assertTrue(DiscussionUtil.html(payload,0).contains("<br>"));
        assertFalse(DiscussionUtil.html(payload,1).contains("onerror"));
        assertEquals("123456789012@qq.com",DiscussionUtil.email("123456789012"));
        WebsiteProperties site=new WebsiteProperties();site.setTitle("<script>bad()</script>");
        String mail=EmailTemplateUtil.buildReplyNotificationEmailContent(payload,payload,payload,payload,"comment",site);
        assertFalse(mail.contains("<img src=x")); assertFalse(mail.contains("<script>"));
        String extensions=MarkdownUtil.toHtml("| a | b |\n|---|---|\n|1|2|\n\nhttps://example.com\n\n- [x] done");
        assertTrue(extensions.contains("<table>"));assertTrue(extensions.contains("href=\"https://example.com\""));assertTrue(extensions.contains("disabled"));
    }
    @Test void uploadKeepsUnconvertedExtensionsAndRejectsMissingNames() throws Exception {
        ImageProperties image=new ImageProperties();image.setEnabled(false);image.setOutPutFormat("webp");
        AliOssUtil oss=mock(AliOssUtil.class);when(oss.getFileCategory("png")).thenReturn("image");
        when(oss.upload(any(),eq("png"),any())).thenReturn("https://test/image.png");
        CommonServiceImpl service=new CommonServiceImpl(image,oss,new ImageCompressUtil(image));
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();ImageIO.write(new BufferedImage(2,2,BufferedImage.TYPE_INT_ARGB),"png",bytes);
        assertEquals("https://test/image.png",service.uploadFile(new MockMultipartFile("file","A.PNG","image/png",bytes.toByteArray())));
        verify(oss).upload(aryEq(bytes.toByteArray()),eq("png"),endsWith(".png"));
        assertThrows(UploadFileErrorException.class,()->service.uploadFile(null));
        assertThrows(UploadFileErrorException.class,()->service.uploadFile(new MockMultipartFile("file",null,null,new byte[]{1})));
    }
    @Test void signedVisitorTokensCannotBeSubstitutedWithAdminOrNumericIds() {
        JwtProperties jwt=new JwtProperties();jwt.setSecretKey("isolated-test-key-which-is-longer-than-thirty-two-bytes");
        VisitorTokenService visitors=new VisitorTokenServiceImpl(jwt);
        assertEquals(19L,visitors.verify(visitors.issue(19L)));
        assertThrows(TokenException.class,()->visitors.verify("19"));
        assertThrows(TokenException.class,()->visitors.verify(JwtUtil.createJWT(jwt.getSecretKey(),10000,Map.of("adminId",19))));
        assertNotEquals(visitors.issue(19L),visitors.issue(19L));
        MockHttpServletRequest request=new MockHttpServletRequest();request.setCookies(new jakarta.servlet.http.Cookie(jwt.getCookieName(),"%broken"));
        assertNull(JwtUtil.cookieToken(request,jwt.getCookieName()));
    }
    @Test void networkInitialSampleUsesZeroBaselineAndLaterRatesUseElapsedTime() {
        assertEquals(Long.valueOf(500L),ReflectionTestUtils.<Long>invokeMethod(com.xuan.service.impl.monitor.ServerMonitorUtil.class,"calculateRate",1000L,2000L,1000L,3000L));
        assertEquals(Long.valueOf(0L),ReflectionTestUtils.<Long>invokeMethod(com.xuan.service.impl.monitor.ServerMonitorUtil.class,"calculateRate",2000L,1000L,1000L,2000L));
    }

    @Test void ossFailuresNeverReturnUrlsAndAllClientsClose() {
        com.aliyun.oss.OSS client=mock(com.aliyun.oss.OSS.class);
        AliOssUtil oss=new AliOssUtil("test.invalid","test-id","test-secret","bucket");
        try(var construction=org.mockito.Mockito.mockConstruction(com.aliyun.oss.OSSClientBuilder.class,
                (builder,context)->when(builder.build(anyString(),anyString(),anyString())).thenReturn(client))) {
            doThrow(new com.aliyun.oss.OSSException("rejected")).when(client).putObject(anyString(),anyString(),any(InputStream.class));
            assertThrows(UploadFileErrorException.class,()->oss.upload(new byte[]{1},"png","a.png"));
            verify(client).shutdown();reset(client);
            doThrow(new com.aliyun.oss.ClientException("network failed")).when(client).putObject(anyString(),anyString(),any(InputStream.class));
            assertThrows(UploadFileErrorException.class,()->oss.upload(new byte[]{1},"png","a.png"));verify(client).shutdown();reset(client);
            assertEquals("https://bucket.test.invalid/image/a.png",oss.upload(new byte[]{1},"png","a.png"));verify(client).shutdown();
        }
    }
    @Test void pngAndWebpConversionsKeepTransparencyAndUnsupportedFormatsStayUnconverted() throws Exception {
        BufferedImage input=new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
        Random random=new Random(1);for(int y=0;y<100;y++)for(int x=0;x<100;x++)input.setRGB(x,y,0x40000000|random.nextInt(0xffffff));
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();ImageIO.write(input,"png",bytes);
        var file=new MockMultipartFile("file","transparent.PNG","image/png",bytes.toByteArray());
        ImageProperties image=new ImageProperties();image.setEnabled(true);image.setMaxSizeKb(1L);image.setQuality(0.8);
        for(String format:List.of("png","webp")) {
            image.setOutPutFormat(format);ImageCompressUtil compress=new ImageCompressUtil(image);assertTrue(compress.shouldCompress(file));
            byte[] converted=compress.compress(file);BufferedImage decoded=ImageIO.read(new ByteArrayInputStream(converted));
            assertNotNull(decoded);assertTrue(decoded.getColorModel().hasAlpha());assertTrue((decoded.getRGB(0,0)>>>24)<255);
            if(format.equals("webp"))assertEquals("WEBP",new String(converted,8,4,java.nio.charset.StandardCharsets.US_ASCII));
        }
        for(String extension:List.of("svg","ico")) assertFalse(new ImageCompressUtil(image).shouldCompress(new MockMultipartFile("file","a."+extension,null,bytes.toByteArray())));
        image.setMaxSizeKb(1000L);assertFalse(new ImageCompressUtil(image).shouldCompress(file));
    }
    @Test void rssUrlsRemainValidXmlForSpecialSlugsAndSiteAttributes() throws Exception {
        IArticleService articles=mock(IArticleService.class);IPersonalInfoService personal=mock(IPersonalInfoService.class);
        WebsiteProperties site=new WebsiteProperties();site.setBlog("https://example.test/?a=1&b=2");
        when(articles.getPublishedPage(1,20)).thenReturn(com.xuan.result.PageResult.of(List.of(com.xuan.vo.BlogArticleVO.builder().id(1L).slug("a&b\"中文").title("title & quote").build()),1,1,20));
        String xml=new RssFeedServiceImpl(articles,personal,site).generateRssFeed();
        var document=javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        assertEquals(1,document.getElementsByTagName("item").getLength());
        assertTrue(document.getElementsByTagName("guid").item(0).getTextContent().contains("a&b%22"));
    }
    @Test void localBucketsHaveCapacityAndIdleLifetimeAndRetainActiveLimits() {
        var config=new com.xuan.config.RateLimitConfiguration();
        for(int i=0;i<8;i++)assertTrue(config.tryConsume("active",8,5,Duration.ofSeconds(60)));
        assertFalse(config.tryConsume("active",8,5,Duration.ofSeconds(60)));
        for(int i=0;i<12000;i++)config.resolveBucket("key"+i,8,5,Duration.ofSeconds(60));
        config.getBucketCache().cleanUp();assertTrue(config.getBucketCache().estimatedSize()<=10000);
        assertEquals(Duration.ofHours(1),config.getBucketCache().policy().expireAfterAccess().orElseThrow().getExpiresAfter());
    }
    @Test void actualNetworkFirstFramesUseBaselineEvenWithLargeHistoricalCounters() {
        var network=mock(oshi.hardware.NetworkIF.class);when(network.getName()).thenReturn("isolated-nic");when(network.getBytesRecv()).thenReturn(900_000_000L);when(network.getBytesSent()).thenReturn(700_000_000L);
        Map<?,?> snapshots=(Map<?,?>)ReflectionTestUtils.getField(com.xuan.service.impl.monitor.ServerMonitorUtil.class,"NETWORK_SNAPSHOTS");snapshots.clear();
        try(var oshi=mockStatic(cn.hutool.system.oshi.OshiUtil.class,CALLS_REAL_METHODS)) {
            oshi.when(cn.hutool.system.oshi.OshiUtil::getNetworkIFs).thenReturn(List.of(network));
            assertEquals(0L,com.xuan.service.impl.monitor.ServerMonitorUtil.collectNetworkSnapshot("all").inBytesPerSec());
            assertEquals(0L,com.xuan.service.impl.monitor.ServerMonitorUtil.collectNetworkSnapshot("isolated-nic").outBytesPerSec());
        } finally {snapshots.clear();}
    }
    @Test void userAgentParsingPreservesExistingBrowserAndPlatformPriority() {
        var agent=new UserAgentServiceImpl();
        var chrome=agent.parse("Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");
        assertEquals("Android",chrome.os());assertEquals("Chrome",chrome.browser());
        assertEquals("WeChat",agent.parse("Mozilla/5.0 Chrome/120.0 MicroMessenger/8.0").browser());
        assertEquals("Unknown",agent.parse(null).os());
    }

    @org.springframework.web.bind.annotation.RestController
    static class ErrorProbe {
        @org.springframework.web.bind.annotation.GetMapping("/limited") public void limited() { throw new RateLimitException("wait"); }
        @org.springframework.web.bind.annotation.GetMapping("/blocked") public void blocked() { throw new BlockedException("blocked"); }
        @org.springframework.web.bind.annotation.GetMapping("/expired") public void expired() { throw new TokenException("expired"); }
    }
    @Test void httpErrorsDistinguishTemporaryLimitsBansAndExpiredCredentials() throws Exception {
        var mvc=org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(new ErrorProbe()).setControllerAdvice(new com.xuan.handle.GlobalExceptionHandler()).build();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/limited"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isTooManyRequests())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Retry-After","60"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.code").value(0));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/blocked")).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isForbidden());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/expired")).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnauthorized());
    }
    @Test void controllerLogsNeverContainPasswordOrMailCodeAndCreationReturnsAnId() throws Exception {
        var admins=mock(IAdminService.class);var jwt=new JwtProperties();jwt.setTtl(600000L);
        when(admins.login(any(),any())).thenReturn(com.xuan.vo.AdminLoginVO.builder().id(1L).build());
        ch.qos.logback.classic.Logger logger=(ch.qos.logback.classic.Logger)org.slf4j.LoggerFactory.getLogger(com.xuan.controller.admin.AdminController.class);
        var events=new ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>();events.start();logger.addAppender(events);
        try {
            var mvc=org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(new com.xuan.controller.admin.AdminController(admins,jwt)).build();
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/admin/login").contentType("application/json")
                    .content("{\"username\":\"admin\",\"password\":\"SENTINELpass!\",\"code\":\"987321\"}"));
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/admin/admin/changePassword").contentType("application/json")
                    .content("{\"oldPassword\":\"SENTINELold\",\"newPassword\":\"SENTINELnew\",\"confirmNewPassword\":\"SENTINELnew\"}"));
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/admin/admin/changeEmail").contentType("application/json")
                    .content("{\"email\":\"new@example.test\",\"code\":\"SENTINELcode\"}"));
            assertTrue(events.list.stream().noneMatch(e->e.getFormattedMessage().contains("SENTINEL")||e.getFormattedMessage().contains("987321")));
        } finally { logger.detachAppender(events);events.stop(); }
        IArticleService articles=mock(IArticleService.class);when(articles.createArticle(any())).thenReturn(84L);
        var mvc=org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(new com.xuan.controller.admin.ArticleController(articles)).build();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/article").contentType("application/json")
                .content("{\"title\":\"title\",\"slug\":\"slug\",\"contentMarkdown\":\"body\",\"categoryId\":1,\"isPublished\":0}"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data").value(84));
    }
    private static byte[] aryEq(byte[] value) { return org.mockito.AdditionalMatchers.aryEq(value); }
}
