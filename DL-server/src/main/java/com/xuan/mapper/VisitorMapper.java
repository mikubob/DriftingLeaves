package com.xuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuan.dto.DailyViewCountDTO;
import com.xuan.dto.ProvinceCountDTO;
import com.xuan.entity.Visitors;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface VisitorMapper extends BaseMapper<Visitors> {

    List<DailyViewCountDTO> getDailyNewVisitorStats(@Param("begin") LocalDate begin, @Param("end") LocalDate end);

    List<ProvinceCountDTO> getProvinceDistribution();
}
