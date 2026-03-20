package com.xuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.dto.ViewPageQueryDTO;
import com.xuan.entity.Views;
import com.xuan.result.PageResult;

import java.util.List;

public interface IViewService extends IService<Views> {
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

    /**
     * 统计总浏览量
     * @return 总浏览量
     */
    Integer countTotal();

    /**
     * 统计今日浏览量
     * @return 今日浏览量
     */
    Integer countToday();
}
