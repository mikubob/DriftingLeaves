package com.xuan.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.dto.ArticleCommentDTO;
import com.xuan.dto.ArticleCommentEditDTO;
import com.xuan.dto.ArticleCommentPageQueryDTO;
import com.xuan.dto.ArticleCommentReplyDTO;
import com.xuan.entity.ArticleComments;
import com.xuan.result.PageResult;
import com.xuan.vo.ArticleCommentVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 文章评论服务
 */
public interface IArticleCommentService extends IService<ArticleComments> {

    /**
     * 按可见根节点分页查询评论，仅装入完整祖先链可见的回复。
     * @param articleId 已发布文章 ID
     * @param page 页码，从 1 开始
     * @param pageSize 每页根节点数，1..50
     * @return 根节点分页及本页可见回复
     * @throws com.xuan.exception.ValidationException 分页范围无效或文章未发布
     */
    PageResult<ArticleCommentVO> getTreePage(Long articleId, int page, int pageSize);

    /**
     * 管理端按时间及审核状态分页查询评论。
     * @param articleCommentPageQueryDTO 查询条件
     * @return 含管理字段的分页结果
     */
    PageResult pageQuery(ArticleCommentPageQueryDTO articleCommentPageQueryDTO);

    /**
     * 管理端读取指定文章的全部评论并安全渲染历史内容。
     * @param articleId 文章 ID
     * @return 按创建时间倒序的评论
     */
    List<ArticleComments> getByArticleId(Long articleId);

    /**
     * 批量审核评论，只处理未审核到已审核的状态变化，成功提交后发送通知。
     * @param ids 评论 ID 列表
     */
    void batchApprove(List<Long> ids);

    /**
     * 批量删除评论；删除根节点时级联整条线程，删除回复时保留并改挂后代。
     * @param ids 评论 ID 列表
     */
    void batchDelete(List<Long> ids);

    /**
     * 管理员回复评论，继承祖先私密限制并在提交成功后发送通知。
     * @param articleCommentReplyDTO 回复内容及目标关系
     * @param request 当前请求，用于地点与设备信息
     */
    void adminReply(ArticleCommentReplyDTO articleCommentReplyDTO, HttpServletRequest request);

    // ===== 博客端方法 =====

    /**
     * 兼容旧数组入口，返回第一页至多十个可见根节点及其可见回复。
     * @param articleId 已发布文章 ID
     * @param visitorId 兼容旧参数，不作为身份凭据
     * @return 当前请求访客可见的第一页评论
     */
    List<ArticleCommentVO> getCommentTree(Long articleId, Long visitorId);

    /**
     * 当前已验证访客提交评论，校验祖先可见性及一次性验证码，内容进入待审状态。
     * @param articleCommentDTO 内容、格式、联系方式及验证码
     * @param request 当前请求，用于地点与设备信息
     * @throws com.xuan.exception.ValidationException 验证码、联系方式或回复关系无效
     */
    void submitComment(ArticleCommentDTO articleCommentDTO, HttpServletRequest request);

    /**
     * 编辑当前访客自己的评论并重新进入待审状态，保存内容格式和编辑标记。
     * @param editDTO 内容 ID、正文及格式；visitorId 不参与授权
     * @throws com.xuan.exception.ValidationException 内容不存在或无权编辑
     */
    void editComment(ArticleCommentEditDTO editDTO);

    /**
     * 由当前已验证访客删除本人评论，管理员回复不可由访客删除。
     * @param id 内容 ID
     * @param visitorId 仅兼容旧参数，实际身份取自 VisitorContext
     * @throws com.xuan.exception.ValidationException 内容不存在或无权删除
     */
    void visitorDeleteComment(Long id, Long visitorId);

    /**
     * 统计总评论数
     * @return 总评论数
     */
    Integer countTotal();

    /**
     * 统计待审核评论数
     * @return 待审核评论数
     */
    Integer countPending();
}
