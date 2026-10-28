package com.xuan.regression;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuan.annotation.OperationLog;
import com.xuan.constant.*;
import com.xuan.context.*;
import com.xuan.controller.blog.*;
import com.xuan.dto.*;
import com.xuan.entity.*;
import com.xuan.enumeration.OperationType;
import com.xuan.exception.*;
import com.xuan.handle.GlobalExceptionHandler;
import com.xuan.interceptor.VisitorTokenInterceptor;
import com.xuan.mapper.*;
import com.xuan.properties.*;
import com.xuan.service.*;
import com.xuan.task.CounterSyncCoordinator;
import com.xuan.service.impl.*;
import com.xuan.utils.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.*;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import javax.sql.DataSource;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnabledIfSystemProperty(named="isolatedServices",matches="true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IsolatedBackendTest {
    private IsolatedServices services;
    private JdbcTemplate db;
    private StringRedisTemplate redis;
    private final ObjectMapper json=new ObjectMapper().findAndRegisterModules();
    private <T>T bean(Class<T> type) { return services.context.getBean(type); }

    @BeforeAll void start() throws Exception {
        services=new IsolatedServices();db=new JdbcTemplate(bean(DataSource.class));redis=bean(StringRedisTemplate.class);
    }
    @AfterAll void stop() { if(services!=null)services.close(); }
    @BeforeEach void reset() {
        VisitorContext.clear();BaseContext.removeCurrentId();BaseContext.removeCurrentRole();
        // This DataSource is constructed solely from the newly spawned service's random port.
        for(String table:db.queryForList("SHOW TABLES",String.class)) db.execute("TRUNCATE TABLE `"+table+"`");
        try(var c=redis.getConnectionFactory().getConnection()) { c.serverCommands().flushDb(); }
        org.mockito.Mockito.reset(bean(EmailService.class),bean(AsyncEmailService.class));
        db.update("INSERT INTO visitors(id,fingerprint,ip,total_views,is_blocked) VALUES(1,'visitor-a','127.0.0.1',1,0),(2,'visitor-b','127.0.0.1',1,0)");
        db.update("INSERT INTO article_categories(id,name,slug,sort) VALUES(1,'category','category',0)");
        db.update("INSERT INTO articles(id,title,slug,category_id,is_published,view_count,like_count,comment_count,content_markdown,content_html) VALUES(1,'article','article',1,1,100,10,0,'body','body'),(2,'article2','article2',1,1,0,0,0,'body','body')");
    }
    private void own(long id) { VisitorContext.setId(id); }
    private MockHttpServletRequest request() { MockHttpServletRequest r=new MockHttpServletRequest();r.setRemoteAddr("127.0.0.1");return r; }
    private String answer(com.xuan.vo.CaptchaVO captcha) {
        String[] parts=captcha.getQuestion().split(" ");return String.valueOf(Integer.parseInt(parts[0])+Integer.parseInt(parts[2]));
    }
    private long comment(Long root,Long parent,long owner,int approved,int secret,String content) {
        ArticleComments c=ArticleComments.builder().articleId(1L).rootId(root).parentId(parent).visitorId(owner).nickname("author")
                .content(content).contentHtml("<img onerror=bad()>").emailOrQq("private@example.test").isMarkdown(0).isSecret(secret).isApproved(approved).isAdminReply(0).build();
        bean(ArticleCommentMapper.class).insert(c);if(approved==1)db.update("UPDATE articles SET comment_count=comment_count+1 WHERE id=1");return c.getId();
    }
    private MockMvc blogMvc() {
        return MockMvcBuilders.standaloneSetup(new ArticleCommentController(bean(IArticleCommentService.class)),new MessageController(bean(IMessageService.class)),new RssSubscriptionController(bean(IRssSubscriptionService.class)))
                .addInterceptors(bean(VisitorTokenInterceptor.class)).setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(json)).build();
    }

    @Test void numericVisitorIdCannotReadPendingSecretOrModifyOtherAuthors() throws Exception {
        long id=comment(null,null,1,0,1,"private pending");
        String b=bean(VisitorTokenService.class).issue(2L);
        MockMvc mvc=blogMvc();
        mvc.perform(get("/blog/articleComment/article/1/page").param("visitorId","1").header("X-Visitor-Token",b))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.total").value(0));
        mvc.perform(delete("/blog/articleComment/"+id).param("visitorId","1").header("X-Visitor-Token",b)).andExpect(status().isBadRequest());
        mvc.perform(delete("/blog/articleComment/"+id).param("visitorId","1")).andExpect(status().isUnauthorized());
        mvc.perform(get("/blog/articleComment/article/1/page").header("X-Visitor-Token","1")).andExpect(status().isUnauthorized());
        assertNotNull(bean(ArticleCommentMapper.class).selectById(id));
        own(1);var page=bean(IArticleCommentService.class).getTreePage(1L,1,10);
        String response=json.writeValueAsString(page);
        assertTrue(response.contains("private pending"));assertTrue(response.contains("\"isOwn\":true"));
        assertFalse(response.contains("emailOrQq"));assertFalse(response.contains("private@example.test"));assertTrue(response.contains("avatarUrl"));
    }

    @Test void privateAncestorsRestrictLegacyDescendantsAndAdminReplies() {
        long secretRoot=comment(null,null,1,1,1,"secret-root");
        comment(secretRoot,secretRoot,2,1,0,"legacy-child");
        long publicRoot=comment(null,null,1,1,0,"public-root");
        long privateChild=comment(publicRoot,publicRoot,1,1,1,"secret-child");
        comment(publicRoot,privateChild,2,1,0,"legacy-grandchild");
        own(2);String publicJson=JSON.toJSONString(bean(IArticleCommentService.class).getTreePage(1L,1,10));
        assertFalse(publicJson.contains("secret-root"));assertFalse(publicJson.contains("legacy-child"));assertFalse(publicJson.contains("legacy-grandchild"));
        bean(IArticleCommentService.class).adminReply(ArticleCommentReplyDTO.builder().articleId(1L).parentId(secretRoot).content("admin-private").isMarkdown(0).build(),null);
        own(1);String ownerJson=JSON.toJSONString(bean(IArticleCommentService.class).getTreePage(1L,1,10));assertTrue(ownerJson.contains("admin-private"));
        own(2);assertFalse(JSON.toJSONString(bean(IArticleCommentService.class).getTreePage(1L,1,10)).contains("admin-private"));
    }

    @Test void editReapprovalAndConcurrentApprovalsCountEachStateTransitionOnce() throws Exception {
        long id=comment(null,null,1,1,0,"old");own(1);
        var edit=ArticleCommentEditDTO.builder().id(id).visitorId(2L).content("<img onerror=bad()>\nnext").isMarkdown(0).build();
        bean(IArticleCommentService.class).editComment(edit);bean(IArticleCommentService.class).editComment(edit);
        ArticleComments stored=bean(ArticleCommentMapper.class).selectById(id);
        assertEquals(0,stored.getIsApproved());assertEquals(1,stored.getIsEdited());assertEquals(0,stored.getIsMarkdown());assertTrue(stored.getContentHtml().contains("&lt;img"));
        assertEquals(0,db.queryForObject("SELECT comment_count FROM articles WHERE id=1",Integer.class));
        concurrent(12,()->bean(IArticleCommentService.class).batchApprove(List.of(id)));
        assertEquals(1,db.queryForObject("SELECT comment_count FROM articles WHERE id=1",Integer.class));
        long other=comment(null,null,2,0,0,"second");
        concurrent(2,()->bean(IArticleCommentService.class).batchApprove(List.of(id,other)));
        assertEquals(2,db.queryForObject("SELECT comment_count FROM articles WHERE id=1",Integer.class));
        bean(IArticleCommentService.class).batchDelete(List.of(id,other));
        assertEquals(0,db.queryForObject("SELECT comment_count FROM articles WHERE id=1",Integer.class));
    }

    @Test void parentRelationshipsAndSubmittedOwnershipComeFromServer() {
        own(2);long parent=comment(null,null,1,1,0,"parent");
        var captcha=bean(CaptchaService.class).generate();
        ArticleCommentDTO dto=ArticleCommentDTO.builder().articleId(2L).parentId(parent).visitorId(1L).nickname("B").content("reply").captchaId(captcha.getCaptchaId()).captchaAnswer(answer(captcha)).build();
        assertThrows(ValidationException.class,()->bean(IArticleCommentService.class).submitComment(dto,request()));
        dto.setArticleId(1L);dto.setRootId(parent+99);assertThrows(ValidationException.class,()->bean(IArticleCommentService.class).submitComment(dto,request()));
        dto.setRootId(null);dto.setParentNickname("spoof");bean(IArticleCommentService.class).submitComment(dto,request());
        ArticleComments saved=bean(ArticleCommentMapper.class).selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ArticleComments>().eq("nickname","B")).getFirst();
        assertEquals(2L,saved.getVisitorId());assertEquals(parent,saved.getRootId());assertEquals("author",saved.getParentNickname());assertEquals(0,saved.getIsApproved());
        verifyNoInteractions(bean(AsyncEmailService.class));
    }

    @Test void rootPaginationKeepsRepliesAndMessagePrivacyAndReview() {
        for(int i=0;i<23;i++) {long root=comment(null,null,1,1,0,"root-"+i);comment(root,root,1,1,0,"reply-"+i);}
        own(2);var first=bean(IArticleCommentService.class).getTreePage(1L,1,10);var last=bean(IArticleCommentService.class).getTreePage(1L,3,10);
        assertEquals(23,first.getTotal());assertEquals(10,first.getRecords().size());assertEquals(3,last.getRecords().size());
        assertTrue(first.getRecords().stream().allMatch(v->v.getChildren().size()==1));
        assertEquals(10,bean(IArticleCommentService.class).getCommentTree(1L,1L).size());
        Messages m=Messages.builder().visitorId(1L).nickname("A").content("<b>secret</b>").contentHtml("legacy").emailOrQq("private@test.example").isApproved(1).isSecret(1).isMarkdown(0).isAdminReply(0).build();
        bean(MessageMapper.class).insert(m);assertEquals(0,bean(IMessageService.class).getTreePage(1,10).getTotal());
        own(1);bean(IMessageService.class).editMessage(MessageEditDTO.builder().id(m.getId()).visitorId(2L).content("new").isMarkdown(1).build());
        Messages saved=bean(MessageMapper.class).selectById(m.getId());assertEquals(0,saved.getIsApproved());assertEquals(1,saved.getIsEdited());assertEquals(1,saved.getIsMarkdown());
        assertFalse(JSON.toJSONString(bean(IMessageService.class).getTreePage(1,10)).contains("emailOrQq"));
    }

    @Test void identicalDeviceInputsCreateDifferentVisitorsAndConcurrentViewsNeverOverwriteGeo() throws Exception {
        IVisitorService visitor=bean(IVisitorService.class);var dto=VisitorRecordDTO.builder().pagePath("/").platform("Win32").language("zh-CN").build();
        var a=visitor.recordVisitorViewInfo(dto,request());var b=visitor.recordVisitorViewInfo(dto,request());assertNotEquals(a.getVisitorId(),b.getVisitorId());
        db.update("UPDATE visitors SET country='kept', province='kept-province' WHERE id=?",a.getVisitorId());
        concurrent(12,()-> { own(a.getVisitorId()); visitor.recordVisitorViewInfo(dto,request());VisitorContext.clear(); });
        Visitors saved=bean(VisitorMapper.class).selectById(a.getVisitorId());assertEquals(13L,saved.getTotalViews());assertEquals("kept",saved.getCountry());
        own(a.getVisitorId());assertEquals(a.getVisitorId(),visitor.recordVisitorViewInfo(dto,request()).getVisitorId());
    }

    @Test void realRedisLikesAreAtomicAndUseVerifiedVisitor() throws Exception {
        concurrent(20,()-> { own(1);bean(IArticleLikeService.class).likeArticle(1L,2L);VisitorContext.clear(); });
        assertEquals("1",redis.opsForHash().get(RedisConstant.ARTICLE_LIKE_COUNT,"1"));assertEquals(1L,redis.opsForZSet().size(RedisConstant.ARTICLE_LIKE_USER_SET+1));
        own(2);assertFalse(bean(IArticleLikeService.class).hasLiked(1L,1L));
        concurrent(20,()-> { own(1);bean(IArticleLikeService.class).unlikeArticle(1L,2L);VisitorContext.clear(); });
        assertEquals("0",redis.opsForHash().get(RedisConstant.ARTICLE_LIKE_COUNT,"1"));
    }

    @ParameterizedTest @ValueSource(strings={"view","like"})
    void committedBatchSurvivesRedisAckFailureWithoutDoubleCounting(String metric) {
        String key=metric.equals("view")?RedisConstant.ARTICLE_VIEW_COUNT:RedisConstant.ARTICLE_LIKE_COUNT;
        long initial=metric.equals("view")?100:10;String column=metric+"_count";redis.opsForHash().put(key,"1","5");
        AtomicBoolean fail=new AtomicBoolean(true);
        StringRedisTemplate flaky=new StringRedisTemplate(redis.getConnectionFactory()) {
            @Override public <T>T execute(RedisScript<T> script,List<String> keys,Object...args) {
                if(script.getScriptAsString().contains("HINCRBY") && fail.getAndSet(false)) throw new IllegalStateException("injected ack timeout");
                return super.execute(script,keys,args);
            }
        };
        CounterSyncCoordinator counters=new CounterSyncCoordinator(flaky,bean(CounterBatchWriter.class));
        counters.sync(metric,key,"test-lock");assertEquals(initial+5,db.queryForObject("SELECT "+column+" FROM articles WHERE id=1",Long.class));
        redis.opsForHash().increment(key,"1",3);counters.sync(metric,key,"test-lock");
        assertEquals(initial+5,db.queryForObject("SELECT "+column+" FROM articles WHERE id=1",Long.class));assertEquals("3",redis.opsForHash().get(key,"1"));
        counters.sync(metric,key,"test-lock");assertEquals(initial+8,db.queryForObject("SELECT "+column+" FROM articles WHERE id=1",Long.class));assertEquals("0",redis.opsForHash().get(key,"1"));
        assertEquals(2,db.queryForObject("SELECT COUNT(*) FROM counter_sync_batches",Integer.class));
    }

    @Test void expiredLeaseOverlapReusesSameBatchAndOldOwnerCannotDeleteNewLock() {
        String key=RedisConstant.ARTICLE_VIEW_COUNT;redis.opsForHash().put(key,"1","5");CounterBatchWriter real=bean(CounterBatchWriter.class);
        CounterBatchWriter overlap=mock(CounterBatchWriter.class);
        doAnswer(i->{ redis.delete("overlap-lock");redis.opsForHash().increment(key,"1",3);
            bean(CounterSyncCoordinator.class).sync("view",key,"overlap-lock");
            real.apply(i.getArgument(0),i.getArgument(1),i.getArgument(2),i.getArgument(3));return null; }).when(overlap).apply(anyString(),anyLong(),anyString(),anyLong());
        new CounterSyncCoordinator(redis,overlap).sync("view",key,"overlap-lock");
        assertEquals(105,db.queryForObject("SELECT view_count FROM articles WHERE id=1",Integer.class));assertEquals("3",redis.opsForHash().get(key,"1"));assertEquals(1,db.queryForObject("SELECT COUNT(*) FROM counter_sync_batches",Integer.class));
        CounterBatchWriter takeover=mock(CounterBatchWriter.class);
        doAnswer(i->{redis.opsForValue().set("owner-lock","new-owner");real.apply(i.getArgument(0),i.getArgument(1),i.getArgument(2),i.getArgument(3));return null;}).when(takeover).apply(anyString(),anyLong(),anyString(),anyLong());
        new CounterSyncCoordinator(redis,takeover).sync("view",key,"owner-lock");assertEquals("new-owner",redis.opsForValue().get("owner-lock"));
    }

    @Test void failedDatabaseTransactionRollsBackReceiptAndLeavesRedisRetryable() {
        redis.opsForHash().put(RedisConstant.ARTICLE_VIEW_COUNT,"1","5");
        db.execute("CREATE TRIGGER reject_count BEFORE UPDATE ON articles FOR EACH ROW SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='injected failure'");
        try {
            bean(CounterSyncCoordinator.class).sync("view",RedisConstant.ARTICLE_VIEW_COUNT,"failure-lock");
            assertEquals(0,db.queryForObject("SELECT COUNT(*) FROM counter_sync_batches",Integer.class));assertEquals("5",redis.opsForHash().get(RedisConstant.ARTICLE_VIEW_COUNT,"1"));
        } finally { db.execute("DROP TRIGGER reject_count"); }
        bean(CounterSyncCoordinator.class).sync("view",RedisConstant.ARTICLE_VIEW_COUNT,"failure-lock");assertEquals(105,db.queryForObject("SELECT view_count FROM articles WHERE id=1",Integer.class));
    }

    @Test void mailCodesAndCaptchaAreOneUseAndCooldownSurvivesSuccessfulConsumption() throws Exception {
        VerifyCodeService codes=bean(VerifyCodeService.class);List<String> reservations=Collections.synchronizedList(new ArrayList<>());
        concurrent(16,()-> {String ticket=codes.reserveCode(1L,"432198");if(ticket!=null)reservations.add(ticket);});assertEquals(1,reservations.size());
        var accepted=new java.util.concurrent.atomic.AtomicInteger();concurrent(16,()-> {if(codes.verifyCode(1L,"432198"))accepted.incrementAndGet();});assertEquals(1,accepted.get());assertTrue(codes.getRemainingCooldown(1L)>0);
        for(int i=0;i<5;i++)assertFalse(codes.verifyCode(9L,"000000"));assertTrue(codes.isLocked(9L));assertTrue(redis.getExpire(RedisConstant.KEY_ATTEMPT_COUNT_PREFIX+9)>0);
        assertNull(codes.reserveCode(9L,"000000"));
        String old=codes.reserveCode(2L,"123456");redis.delete(RedisConstant.KEY_RATE_LIMIT_PREFIX+2);String newer=codes.reserveCode(2L,"123456");
        codes.cancelCode(2L,old);assertTrue(codes.verifyCode(2L,"123456"));assertNotEquals(old,newer);
        own(1);var captcha=bean(CaptchaService.class).generate();String answer=answer(captcha);accepted.set(0);
        concurrent(16,()-> {own(1);try{bean(CaptchaService.class).verify(captcha.getCaptchaId(),answer);accepted.incrementAndGet();}catch(ValidationException ignored){}finally{VisitorContext.clear();}});assertEquals(1,accepted.get());
        own(1);var target=bean(CaptchaService.class).generate();own(2);assertThrows(ValidationException.class,()->bean(CaptchaService.class).verify(target.getCaptchaId(),answer(target)));
        own(1);assertThrows(ValidationException.class,()->bean(CaptchaService.class).verify(target.getCaptchaId(),answer(target)+"extra"));
    }

    @Test void expiredSessionsDoNotOccupyDeviceSlotsAndSameSecondLoginsAreIndependent() {
        String key=RedisConstant.TOKEN_PREFIX+"v2:"+1;for(int i=0;i<10;i++)redis.opsForZSet().add(key,"expired-"+i,System.currentTimeMillis()-1);
        TokenService tokens=bean(TokenService.class);String a=tokens.createAndStoreToken(1L,1);String b=tokens.createAndStoreToken(1L,1);assertNotEquals(a,b);assertEquals(2L,redis.opsForZSet().size(key));
        tokens.logout(1L,a);assertFalse(tokens.isValidToken(1L,a));assertTrue(tokens.isValidToken(1L,b));
        tokens.logoutAll(1L);assertFalse(tokens.isValidToken(1L,b));
    }

    @Test void rssRequiresMailboxConfirmationUpdatesOneRowAndUnsubscribesWithBoundToken() {
        IRssSubscriptionService rss=bean(IRssSubscriptionService.class);own(1);rss.addSubscription(RssSubscriptionDTO.builder().email("one@example.test").nickname("first").visitorId(2L).build());
        assertFalse(rss.getSubscriptionStatus(2L).isSubscribed());
        var url=org.mockito.ArgumentCaptor.forClass(String.class);verify(bean(EmailService.class)).sendSubscriptionConfirmation(eq("one@example.test"),eq("first"),url.capture());String ticket=url.getValue().split("rssConfirm=")[1];
        own(2);assertThrows(RssSubscriptionException.class,()->rss.confirm(ticket));own(1);rss.confirm(ticket);assertThrows(RssSubscriptionException.class,()->rss.confirm(ticket));
        var status=rss.getSubscriptionStatus(2L);assertTrue(status.isSubscribed());assertEquals("one@example.test",status.getEmail());
        rss.addSubscription(RssSubscriptionDTO.builder().email("one@example.test").nickname("edited").build());assertEquals("edited",rss.getSubscriptionStatus(1L).getNickname());
        assertEquals(1,db.queryForObject("SELECT COUNT(*) FROM rss_subscriptions",Integer.class));
        assertThrows(RssSubscriptionException.class,()->rss.unsubscribe("one@example.test"));rss.unsubscribe(status.getUnsubscribeToken());rss.unsubscribe(status.getUnsubscribeToken());assertFalse(rss.getSubscriptionStatus(1L).isSubscribed());
        own(2);assertFalse(rss.getSubscriptionStatus(1L).isSubscribed());
        db.update("INSERT INTO rss_subscriptions(visitor_id,nickname,email,is_active,confirmed) VALUES(2,'legacy','legacy@example.test',1,0)");assertTrue(rss.getAllActiveSubscriptions().isEmpty());
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,()->db.update("INSERT INTO rss_subscriptions(visitor_id,nickname,email,is_active,confirmed) VALUES(NULL,'duplicate','one@example.test',1,1)"));
    }

    @Test void articleCreationReturnsIdUpdatesNeverOverwriteCountersAndDeletionRemovesDependencies() {
        IArticleService articles=bean(IArticleService.class);ArticleDTO dto=ArticleDTO.builder().id(777L).title("new").slug("new").categoryId(1L).contentMarkdown("body").isPublished(0).build();
        long id=articles.createArticle(dto);assertNotEquals(777,id);dto.setId(id);dto.setTitle("edit");
        db.update("UPDATE articles SET view_count=52,like_count=7,comment_count=3 WHERE id=?",id);articles.updateArticle(dto);
        Articles stored=articles.getById(id);assertEquals(52L,stored.getViewCount());assertEquals(7L,stored.getLikeCount());assertEquals(3L,stored.getCommentCount());
        assertThrows(ValidationException.class,()->articles.updateArticle(ArticleDTO.builder().categoryId(1L).build()));
        comment(null,null,1,1,0,"delete");own(1);bean(IArticleLikeService.class).likeArticle(1L,1L);
        articles.batchDelete(List.of(1L));assertEquals(0,db.queryForObject("SELECT COUNT(*) FROM article_comments WHERE article_id=1",Integer.class));assertFalse(redis.hasKey(RedisConstant.ARTICLE_LIKE_USER_SET+1));
    }



    @Test void nestedRepliesKeepChronologicalOrderAfterVisibilityResolution() {
        long root=comment(null,null,1,1,0,"root");long first=comment(root,root,1,1,0,"first");
        long nested=comment(root,first,1,1,0,"nested");long second=comment(root,root,1,1,0,"second");
        db.update("UPDATE article_comments SET create_time=FROM_UNIXTIME(1000000+id)");
        own(1);assertEquals(List.of(first,nested,second),bean(IArticleCommentService.class).getTreePage(1L,1,10).getRecords().getFirst().getChildren().stream().map(com.xuan.vo.ArticleCommentVO::getId).toList());
    }

    @Test void repliesRequireEveryIntermediateAncestorToBeVisible() {
        long root=comment(null,null,1,1,0,"root");
        long secret=comment(root,root,1,1,1,"secret ancestor");
        long parent=comment(root,secret,2,1,0,"legacy public descendant");
        own(2);
        var captcha=bean(CaptchaService.class).generate();
        ArticleCommentDTO reply=ArticleCommentDTO.builder().articleId(1L).parentId(parent).nickname("B")
                .content("reply").captchaId(captcha.getCaptchaId()).captchaAnswer(answer(captcha)).build();
        assertThrows(ValidationException.class,()->bean(IArticleCommentService.class).submitComment(reply,request()));
        assertEquals(3,db.queryForObject("SELECT COUNT(*) FROM article_comments",Integer.class));
        own(1);
        captcha=bean(CaptchaService.class).generate();reply.setCaptchaId(captcha.getCaptchaId());reply.setCaptchaAnswer(answer(captcha));
        bean(IArticleCommentService.class).submitComment(reply,request());
        assertEquals(1,db.queryForObject("SELECT is_secret FROM article_comments ORDER BY id DESC LIMIT 1",Integer.class));

        db.update("INSERT INTO messages(id,root_id,parent_id,visitor_id,nickname,content,content_html,is_approved,is_secret,is_admin_reply,is_markdown) VALUES "
                +"(100,NULL,NULL,1,'A','root','root',1,0,0,0),(101,100,100,1,'A','secret','secret',1,1,0,0),(102,100,101,2,'B','legacy','legacy',1,0,0,0)");
        own(2);captcha=bean(CaptchaService.class).generate();
        MessageDTO message=MessageDTO.builder().parentId(102L).nickname("B").content("reply")
                .captchaId(captcha.getCaptchaId()).captchaAnswer(answer(captcha)).build();
        assertThrows(ValidationException.class,()->bean(IMessageService.class).submitMessage(message,request()));
        assertEquals(3,db.queryForObject("SELECT COUNT(*) FROM messages",Integer.class));
        db.update("UPDATE messages SET parent_id=102 WHERE id=102");
        assertThrows(ValidationException.class,()->bean(IMessageService.class).submitMessage(message,request()));
    }

    @Test void staleLegacyUnsubscribeCannotCancelAConcurrentConfirmation() {
        db.update("INSERT INTO rss_subscriptions(id,visitor_id,nickname,email,is_active,confirmed) VALUES(1,1,'legacy','legacy@example.test',1,1)");
        RssSubscriptions stale=bean(RssSubscriptionMapper.class).selectById(1L);
        var service=spy(new RssSubscriptionServiceImpl(redis,bean(EmailService.class),bean(WebsiteProperties.class),
                bean(JwtProperties.class),bean(VisitorMapper.class)));
        org.springframework.test.util.ReflectionTestUtils.setField(service,"baseMapper",bean(RssSubscriptionMapper.class));
        String oldToken=service.unsubscribeToken(stale);
        doAnswer(invocation->{
            db.update("UPDATE rss_subscriptions SET confirmation_id='new-confirmation' WHERE id=1");
            return stale;
        }).when(service).getById(1L);
        service.unsubscribe(oldToken);
        assertEquals(1,db.queryForObject("SELECT is_active FROM rss_subscriptions WHERE id=1",Integer.class));
    }

    @Test void concurrentFirstPublicationDispatchesOneNotification() throws Exception {
        db.update("UPDATE articles SET is_published=0,publish_time=NULL WHERE id=1");
        db.update("INSERT INTO rss_subscriptions(visitor_id,nickname,email,is_active,confirmed) VALUES(1,'A','confirmed@example.test',1,1)");
        concurrent(12,()->bean(IArticleService.class).publishOrCancel(1L,1));
        verify(bean(AsyncEmailService.class),times(1)).sendNewArticleNotificationAsync(
                eq("confirmed@example.test"),eq("A"),eq("article"),any(),anyString(),anyString());
        assertNotNull(bean(ArticleMapper.class).selectById(1L).getPublishTime());
    }

    @Test void commitActionsWaitForCommitAndDoNotRunOnRollback() {
        var calls=new java.util.concurrent.atomic.AtomicInteger();
        CommitActions.afterCommit(calls::incrementAndGet);assertEquals(1,calls.get());
        var tx=new org.springframework.transaction.support.TransactionTemplate(bean(org.springframework.transaction.PlatformTransactionManager.class));
        tx.executeWithoutResult(status->{CommitActions.afterCommit(calls::incrementAndGet);assertEquals(1,calls.get());});
        assertEquals(2,calls.get());
        tx.executeWithoutResult(status->{CommitActions.afterCommit(calls::incrementAndGet);status.setRollbackOnly();});
        assertEquals(2,calls.get());
    }

    @Test void deletingNonRootKeepsDescendantsAttachedAndPrivate() {
        long root=comment(null,null,1,1,0,"root");long child=comment(root,root,1,1,1,"private child");long reply=comment(root,child,2,1,0,"legacy reply");
        own(1);bean(IArticleCommentService.class).visitorDeleteComment(child,2L);
        ArticleComments saved=bean(ArticleCommentMapper.class).selectById(reply);assertEquals(root,saved.getParentId());assertEquals(1,saved.getIsSecret());
        VisitorContext.clear();assertEquals(0,bean(IArticleCommentService.class).getTreePage(1L,1,10).getRecords().getFirst().getChildren().size());
        assertEquals(2,db.queryForObject("SELECT comment_count FROM articles WHERE id=1",Integer.class));
    }
    @Test void rssChangingMailboxKeepsOneSubscriptionAndOldUnsubscribeTokenCannotAffectNewMailbox() {
        var rss=bean(IRssSubscriptionService.class);own(1);
        rss.addSubscription(RssSubscriptionDTO.builder().email("first@example.test").nickname("first").build());
        var url=org.mockito.ArgumentCaptor.forClass(String.class);verify(bean(EmailService.class)).sendSubscriptionConfirmation(eq("first@example.test"),any(),url.capture());rss.confirm(url.getValue().split("rssConfirm=")[1]);
        String old=rss.getSubscriptionStatus(1L).getUnsubscribeToken();
        rss.addSubscription(RssSubscriptionDTO.builder().email("second@example.test").nickname("second").build());
        assertEquals("first@example.test",rss.getSubscriptionStatus(1L).getEmail());
        verify(bean(EmailService.class)).sendSubscriptionConfirmation(eq("second@example.test"),any(),url.capture());rss.confirm(url.getValue().split("rssConfirm=")[1]);
        assertEquals(1,db.queryForObject("SELECT COUNT(*) FROM rss_subscriptions",Integer.class));assertEquals("second@example.test",rss.getSubscriptionStatus(1L).getEmail());
        assertThrows(RssSubscriptionException.class,()->rss.unsubscribe(old));assertTrue(rss.getSubscriptionStatus(1L).isSubscribed());
    }

    public static class AuditProbe {
        @OperationLog(value=OperationType.UPDATE,target="article",targetId="#id")
        public void record(Long id,Object payload) {}
    }

    @Test void realAsyncAuditRetainsEachAdministratorAndStoresValidRedactedJson() throws Exception {
        AuditProbe probe=bean(AuditProbe.class);
        Map<String,Object> nested=Map.of("dto",AdminChangePasswordDTO.builder().oldPassword("SENTINELold").newPassword("SENTINELnew").confirmNewPassword("SENTINELconfirm").build(),
                "code","SENTINELcode","token","SENTINELtoken","body","long".repeat(5000));
        BaseContext.setCurrentId(11L);probe.record(1L,nested);BaseContext.removeCurrentId();
        BaseContext.setCurrentId(22L);probe.record(2L,nested);BaseContext.removeCurrentId();
        long until=System.nanoTime()+TimeUnit.SECONDS.toNanos(10);
        while(db.queryForObject("SELECT COUNT(*) FROM operation_logs",Integer.class)<2 && System.nanoTime()<until)Thread.sleep(25);
        assertEquals(List.of(11L,22L),db.queryForList("SELECT admin_id FROM operation_logs ORDER BY admin_id",Long.class));
        for(String data:db.queryForList("SELECT CAST(operate_data AS CHAR) FROM operation_logs",String.class)) {
            assertNotNull(JSON.parseObject(data));assertFalse(data.contains("SENTINEL"));assertTrue(data.length()<5000);
        }
    }

    @Test void commitAwareMetadataCachesRefreshButRollbackDoesNotPublishChanges() {
        IArticleCategoryService categories=bean(IArticleCategoryService.class);
        assertEquals(2,categories.getVisibleCategories().getFirst().getArticleCount());
        bean(IArticleService.class).publishOrCancel(1L,0);
        assertEquals(1,categories.getVisibleCategories().getFirst().getArticleCount());
        var transaction=new org.springframework.transaction.support.TransactionTemplate(bean(org.springframework.transaction.PlatformTransactionManager.class));
        transaction.executeWithoutResult(status->{bean(IArticleService.class).publishOrCancel(2L,0);status.setRollbackOnly();});
        assertEquals(1,categories.getVisibleCategories().getFirst().getArticleCount());
        bean(IArticleCategoryService.class).updateCategory(ArticleCategoryDTO.builder().id(1L).name("renamed").slug("category").build());
        bean(IArticleService.class).publishOrCancel(1L,1);
        assertEquals("renamed",bean(IArticleService.class).getBySlug("article").getCategoryName());
        redis.opsForHash().put(RedisConstant.ARTICLE_VIEW_COUNT,"1","5");
        assertEquals(105L,bean(IArticleService.class).getBySlug("article").getViewCount());
        bean(CounterSyncCoordinator.class).sync("view",RedisConstant.ARTICLE_VIEW_COUNT,"cache-sync-lock");
        assertEquals(105L,bean(IArticleService.class).getBySlug("article").getViewCount());
    }

    @Test void updateTargetsAreValidatedAndCreateDoesNotAcceptCallerPrimaryKey() {
        assertThrows(ValidationException.class,()->bean(ISkillService.class).updateSkill(SkillDTO.builder().name("skill").build()));
        assertThrows(ValidationException.class,()->bean(ISkillService.class).updateSkill(SkillDTO.builder().id(999L).name("skill").build()));
        SkillDTO skill=SkillDTO.builder().id(999L).name("skill").build();bean(ISkillService.class).addSkill(skill);
        assertNotEquals(999L,bean(ISkillService.class).getAllSkill().getFirst().getId());
        assertThrows(ValidationException.class,()->bean(IMusicService.class).updateMusic(MusicDTO.builder().id(999L).build()));
        assertThrows(ValidationException.class,()->bean(ISystemConfigService.class).updateConfig(SystemConfigDTO.builder().id(999L).build()));
        assertThrows(ValidationException.class,()->bean(IArticleTagService.class).updateTag(ArticleTagDTO.builder().id(999L).build()));
        assertThrows(ValidationException.class,()->bean(IPersonalInfoService.class).updatePersonalInfo(PersonalInfoDTO.builder().id(999L).nickname("test").build()));
    }

    @Test void changedPasswordCanReallyLogInAndChangeEmailCodeUsesCurrentIdentity() {
        String salt="isolated-salt";
        String hash=bean(EncryptPasswordService.class).hashPassword("Oldpass1!",salt);
        db.update("INSERT INTO admin(id,username,password,salt,nickname,email,role) VALUES(1,'admin',?,?,'admin','admin@example.test',1)",hash,salt);
        BaseContext.setCurrentId(1L);BaseContext.setCurrentRole(1);
        bean(IAdminService.class).changePassword(AdminChangePasswordDTO.builder().oldPassword("Oldpass1!").newPassword("Newpass1!").confirmNewPassword("Newpass1!").build());
        bean(IAdminService.class).sendChangeEmailCode();
        var code=org.mockito.ArgumentCaptor.forClass(String.class);verify(bean(EmailService.class)).sendVerifyCode(eq("admin@example.test"),code.capture());
        var result=bean(IAdminService.class).login(AdminLoginDTO.builder().username("admin").password("Newpass1!").code(code.getValue()).build(),"127.0.0.1");
        assertEquals(1L,result.getId());assertTrue(bean(TokenService.class).isValidToken(1L,result.getToken()));
    }

    private void concurrent(int count,Runnable action) throws Exception {
        try(var executor=Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures=new ArrayList<>();CountDownLatch ready=new CountDownLatch(count),start=new CountDownLatch(1);
            for(int i=0;i<count;i++) futures.add(executor.submit(()->{ready.countDown();try{start.await();action.run();}catch(InterruptedException ex){Thread.currentThread().interrupt();throw new RuntimeException(ex);}}));
            assertTrue(ready.await(10,TimeUnit.SECONDS));start.countDown();for(Future<?> future:futures)future.get(30,TimeUnit.SECONDS);
        }
    }
}
