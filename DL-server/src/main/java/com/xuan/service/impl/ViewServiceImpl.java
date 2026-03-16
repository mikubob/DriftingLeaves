package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.ViewPageQueryDTO;
import com.xuan.entity.Views;
import com.xuan.mapper.ViewMapper;
import com.xuan.result.PageResult;
import com.xuan.service.IViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
