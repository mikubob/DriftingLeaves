package com.xuan.service.impl;

import com.xuan.service.IArticleCategoryService;
import com.xuan.service.IArticleService;
import com.xuan.service.IArticleTagService;
import com.xuan.service.IReportService;
import com.xuan.service.IViewService;
import com.xuan.service.IVisitorService;
import com.xuan.vo.AdminOverviewVO;
import com.xuan.vo.ArticleViewTop10VO;
import com.xuan.vo.BlogReportVO;
import com.xuan.vo.ProvinceVisitorVO;
import com.xuan.vo.ViewReportVO;
import com.xuan.vo.VisitorReportVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 报表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements IReportService {

    private final IViewService viewService;
    private final IVisitorService visitorService;
    private final IArticleCategoryService articleCategoryService;
    private final IArticleService articleService;
    private final IArticleTagService articleTagService;

    /**
     * 获取博客报表
     * @return 博客报表
     */
    @Override
    @Cacheable(value = "blogReport", key = "'stats'")
    public BlogReportVO getBlogReport() {
        return BlogReportVO.builder()
                .viewTotalCount(viewService.countTotal())
                .viewTodayCount(viewService.countToday())
                .visitorTotalCount(visitorService.countTotal())
                .categoryTotalCount(articleCategoryService.countTotal())
                .articleTotalCount(articleService.countPublished())
                .tagTotalCount(articleTagService.countTotal())
                .build();
    }

    @Override
    public ViewReportVO getViewStatistics(LocalDate begin, LocalDate end) {
        // TODO: 实现浏览量统计逻辑
        return null;
    }

    @Override
    public VisitorReportVO getVisitorStatistics(LocalDate begin, LocalDate end) {
        // TODO: 实现访客统计逻辑
        return null;
    }

    @Override
    public ProvinceVisitorVO getProvinceDistribution() {
        // TODO: 实现访客省份分布统计逻辑
        return null;
    }

    @Override
    public ArticleViewTop10VO getArticleViewTop10() {
        // TODO: 实现文章访问量排行前十逻辑
        return null;
    }

    @Override
    public AdminOverviewVO getAdminOverview() {
        // TODO: 实现获取管理端总览数据逻辑
        return null;
    }
}
