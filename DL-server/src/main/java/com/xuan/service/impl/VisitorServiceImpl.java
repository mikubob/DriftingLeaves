package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.VisitorPageQueryDTO;
import com.xuan.dto.VisitorRecordDTO;
import com.xuan.entity.Visitors;
import com.xuan.mapper.VisitorMapper;
import com.xuan.result.PageResult;
import com.xuan.service.IVisitorService;
import com.xuan.vo.VisitorRecordVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 访客服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitors> implements IVisitorService {

    @Override
    public VisitorRecordVO recordVisitorViewInfo(VisitorRecordDTO visitorRecordDTO, HttpServletRequest httpRequest) {
        // TODO: 实现记录访客访问信息逻辑
        return null;
    }

    @Override
    public PageResult pageQuery(VisitorPageQueryDTO visitorPageQueryDTO) {
        // TODO: 实现分页查询访客列表逻辑
        return null;
    }

    @Override
    public void batchBlock(List<Long> ids) {
        // TODO: 实现批量封禁访客逻辑
    }

    @Override
    public void batchUnblock(List<Long> ids) {
        // TODO: 实现批量解封访客逻辑
    }
}
