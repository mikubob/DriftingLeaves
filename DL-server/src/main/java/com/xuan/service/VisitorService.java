package com.xuan.service;

import cn.hutool.db.PageResult;
import com.xuan.dto.VisitorPageQueryDTO;
import com.xuan.dto.VisitorRecordDTO;
import com.xuan.vo.VisitorRecordVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface VisitorService {
    /**
     * 记录访客访问信息
     * @param visitorRecordDTO
     * @param httpRequest
     * @return
     */
    VisitorRecordVO recordVisitorViewInfo(VisitorRecordDTO visitorRecordDTO, HttpServletRequest httpRequest);

    /**
     * 分页查询访客列表
     * @param visitorPageQueryDTO
     * @return
     */
    PageResult pageQuery(VisitorPageQueryDTO visitorPageQueryDTO);

    /**
     * 批量封禁访客
     * @param ids
     */
    void batchBlock(List<Long> ids);

    /**
     * 批量解封访客
     * @param ids
     */
    void batchUnblock(List<Long> ids);
}
