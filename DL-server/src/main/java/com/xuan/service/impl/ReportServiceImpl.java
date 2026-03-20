package com.xuan.service.impl;

import com.xuan.dto.DailyViewCountDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 获取浏览量统计
     * @param begin 开始时间
     * @param end   结束时间
     * @return 浏览量统计
     */
    @Override
    public ViewReportVO getViewStatistics(LocalDate begin, LocalDate end) {
        //1. 获取指定日期范围内的日期列表
        List<LocalDate> dateList = getDateList(begin, end);
        //2. 获取指定日期范围内的浏览量
        List<DailyViewCountDTO> dailyStats = viewService.getDailyViewStats(begin,end);
        //3. 转换为Map, key: 日期, value: 浏览量
        Map<LocalDate, Integer> dailyViewMap = dailyStats.stream()
                .collect(Collectors.toMap(DailyViewCountDTO::getDate, DailyViewCountDTO::getCount));
        //4. 将日期列表和浏览量列表转换为字符串
        List<Integer> viewCountList = dateList.stream()
                .map(date -> dailyViewMap.getOrDefault(date, 0))
                .toList();
        //5. 返回结果
        return ViewReportVO.builder()
                .dateList(dateList.stream().map(LocalDate::toString).collect(Collectors.joining(",")))
                .viewCountList(viewCountList.stream().map(String::valueOf).collect(Collectors.joining(",")))
                .build();
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

    /**
     * 获取指定日期范围内的日期列表
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }
}
