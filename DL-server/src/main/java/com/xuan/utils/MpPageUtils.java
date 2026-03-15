package com.xuan.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.result.PageResult;

import java.util.function.Function;

/**
 * MyBatis-Plus 分页工具类
 * 提供便捷的分页查询方法
 */
public class MpPageUtils {

    /**
     * 执行分页查询
     * @param page 页码
     * @param pageSize 每页数量
     * @param wrapper 查询条件
     * @param service 服务类
     * @return 分页结果
     */
    public static <T> PageResult<T> pageQuery(int page, int pageSize, QueryWrapper<T> wrapper, IService<T> service) {
        Page<T> mpPage = new Page<>(page, pageSize);
        IPage<T> resultPage = service.page(mpPage, wrapper);
        return PageResult.fromIPage(resultPage);
    }

    /**
     * 执行分页查询（使用 Page 对象）
     * @param mpPage MP 分页对象
     * @param wrapper 查询条件
     * @param service 服务类
     * @return 分页结果
     */
    public static <T> PageResult<T> pageQuery(Page<T> mpPage, QueryWrapper<T> wrapper, IService<T> service) {
        IPage<T> resultPage = service.page(mpPage, wrapper);
        return PageResult.fromIPage(resultPage);
    }

    /**
     * 构建分页对象
     * @param page 页码
     * @param pageSize 每页数量
     * @return MP 分页对象
     */
    public static <T> Page<T> buildPage(int page, int pageSize) {
        return new Page<>(page, pageSize);
    }

    /**
     * 构建分页对象（带默认值）
     * @param page 页码（如果小于 1 则使用 1）
     * @param pageSize 每页数量（如果小于 1 则使用 10，大于 500 则使用 500）
     * @return MP 分页对象
     */
    public static <T> Page<T> buildPageWithDefault(int page, int pageSize) {
        page = Math.max(1, page);
        pageSize = Math.max(1, Math.min(500, pageSize));
        return new Page<>(page, pageSize);
    }

    /**
     * 转换 IPage 到 PageResult
     * @param iPage MP 分页结果
     * @return 自定义分页结果
     */
    public static <T> PageResult<T> toPageResult(IPage<T> iPage) {
        return PageResult.fromIPage(iPage);
    }
}
