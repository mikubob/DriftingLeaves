package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.MessageDTO;
import com.xuan.dto.MessageEditDTO;
import com.xuan.dto.MessagePageQueryDTO;
import com.xuan.dto.MessageReplyDTO;
import com.xuan.entity.Messages;
import com.xuan.mapper.MessageMapper;
import com.xuan.result.PageResult;
import com.xuan.service.IMessageService;
import com.xuan.vo.MessageVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 留言服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Messages> implements IMessageService {

    @Override
    public void submitMessage(MessageDTO messageDTO, HttpServletRequest request) {
        //TODO 实现访客提交留言逻辑
    }

    @Override
    public PageResult pageQuery(MessagePageQueryDTO messagePageQueryDTO) {
        //TODO 实现分页条件查询留言逻辑
        return null;
    }

    @Override
    public void batchApprove(List<Long> ids) {
        //TODO 实现批量审核通过留言逻辑
    }

    @Override
    public void batchDelete(List<Long> ids) {
        //TODO 实现批量删除留言逻辑
    }

    @Override
    public void adminReply(MessageReplyDTO messageReplyDTO, HttpServletRequest request) {
        //TODO 实现管理员回复留言逻辑
    }

    @Override
    public List<MessageVO> getMessageTree(Long visitorId) {
        //TODO 实现获取已审核留言列表（树形结构）逻辑
        return null;
    }

    @Override
    public void editMessage(MessageEditDTO editDTO) {
        //TODO 实现访客编辑留言逻辑
    }

    @Override
    public void visitorDeleteMessage(Long id, Long visitorId) {
        //TODO 实现访客删除留言逻辑
    }
}
