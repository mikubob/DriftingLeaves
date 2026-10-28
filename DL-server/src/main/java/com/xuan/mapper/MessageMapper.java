package com.xuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuan.entity.Messages;
import org.apache.ibatis.annotations.Mapper;

/**
 * 留言持久层，提供线程状态变更所需的行锁查询。
 */
@Mapper
public interface MessageMapper extends BaseMapper<Messages> {
    /**
     * 以行锁读取留言，调用方须处于数据库事务内。
     * @param id 留言 ID
     * @return 锁定的记录，不存在时为 null
     */
    @org.apache.ibatis.annotations.Select("SELECT * FROM messages WHERE id = #{id} FOR UPDATE")
    Messages lockById(Long id);

}
