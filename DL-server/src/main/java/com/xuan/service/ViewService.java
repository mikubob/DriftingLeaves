package com.xuan.service;

import cn.hutool.db.PageResult;
import com.xuan.dto.ViewPageQueryDTO;

import java.util.List;

public interface ViewService {
    /**
     * 分页查询浏览记录
     * @param viewPageQueryDTO
     * @return
     */
    PageResult pageQuery(ViewPageQueryDTO viewPageQueryDTO);

    /**
     * 批量删除浏览记录
     * @param ids
     */
    void batchDelete(List<Long> ids);
}
