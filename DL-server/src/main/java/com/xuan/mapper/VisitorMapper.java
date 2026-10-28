package com.xuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuan.dto.DailyViewCountDTO;
import com.xuan.dto.ProvinceCountDTO;
import com.xuan.entity.Visitors;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 访客持久层，提供访问统计、原子增量及访客行锁查询。
 */
@Mapper
public interface VisitorMapper extends BaseMapper<Visitors> {
    /**
     * 以行锁读取访客，调用方须处于数据库事务内。
     * @param id 访客 ID
     * @return 锁定的记录，不存在时为 null
     */
    @org.apache.ibatis.annotations.Select("SELECT * FROM visitors WHERE id = #{id} FOR UPDATE")
    Visitors lockById(Long id);


    /**
     * 统计今日新增访客数
     * @return 今日新增访客数
     */
    @Select("select count(*) from visitors where date(create_time) = curdate()")
    Integer countToday();

    List<DailyViewCountDTO> getDailyNewVisitorStats(@Param("begin") LocalDate begin, @Param("end") LocalDate end);

    List<ProvinceCountDTO> getProvinceDistribution();
}
