package com.xuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.ViewPageQueryDTO;
import com.xuan.entity.Views;
import com.xuan.mapper.ViewMapper;
import com.xuan.result.PageResult;
import com.xuan.service.IViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 浏览记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViewServiceImpl extends ServiceImpl<ViewMapper, Views> implements IViewService {

    @Override
    public PageResult pageQuery(ViewPageQueryDTO viewPageQueryDTO) {
        // TODO: 实现分页查询浏览记录逻辑
        return null;
    }

    @Override
    public void batchDelete(List<Long> ids) {
        // TODO: 实现批量删除浏览记录逻辑
    }

    /**
     * 统计浏览记录总数
     * @return 浏览记录总数
     */
    @Override
    public Integer countTotal() {
        return Math.toIntExact(count());
    }

    /**
     * 统计今日浏览量
     * @return 今日浏览量
     */
    @Override
    public Integer countToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        
        LambdaQueryWrapper<Views> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Views::getViewTime, startOfDay, endOfDay);
        
        return Math.toIntExact(count(wrapper));
    }
}
