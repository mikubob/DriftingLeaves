package com.xuan.controller.blog;

import com.xuan.annotation.RateLimit;
import com.xuan.dto.MessageDTO;
import com.xuan.dto.MessageEditDTO;
import com.xuan.result.Result;
import com.xuan.service.IMessageService;
import com.xuan.vo.MessageVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客端留言接口
 */
@RestController("blogMessageController")
@RequestMapping("/blog/message")
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final IMessageService messageService;

    /**
     * 按可见根节点分页返回留言及其可见回复。
     * @param page 页码，从 1 开始
     * @param pageSize 每页根节点数，1..50
     * @return 当前请求访客可见的留言分页
     */
    @GetMapping("/page")
    public Result<com.xuan.result.PageResult<MessageVO>> getTreePage(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(messageService.getTreePage(page, pageSize));
    }

    /**
     * 访客提交留言
     * @param messageDTO
     * @param request
     * @return
     */
    @PostMapping
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "留言过于频繁，请稍后再试")
    public Result<String> submitMessage(@Valid @RequestBody MessageDTO messageDTO, HttpServletRequest request) {
        log.info("访客提交内容");
        messageService.submitMessage(messageDTO, request);
        return Result.success();
    }

    /**
     * 获取留言列表（树形结构，含当前访客的未审核留言）
     */
    @GetMapping
    public Result<List<MessageVO>> getMessageTree(@RequestParam(required = false) Long visitorId) {
        log.info("博客端获取留言树: visitorId={}", visitorId);
        List<MessageVO> messageTree = messageService.getMessageTree(visitorId);
        return Result.success(messageTree);
    }

    /**
     * 访客编辑留言
     */
    @PutMapping("/edit")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public Result<String> editMessage(@Valid @RequestBody MessageEditDTO editDTO) {
        log.info("访客编辑留言: {}", editDTO);
        messageService.editMessage(editDTO);
        return Result.success();
    }

    /**
     * 访客删除留言
     */
    @DeleteMapping("/{id}")
    @RateLimit(type = RateLimit.Type.IP, tokens = 5, burstCapacity = 8,
              timeWindow = 60, message = "操作过于频繁，请稍后再试")
    public Result<String> deleteMessage(@PathVariable Long id, @RequestParam(required = false) Long visitorId) {
        log.info("访客删除留言: id={}, visitorId={}", id, visitorId);
        messageService.visitorDeleteMessage(id, visitorId);
        return Result.success();
    }
}
