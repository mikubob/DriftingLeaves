package com.xuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.dto.FriendLinkDTO;
import com.xuan.entity.FriendLinks;
import com.xuan.mapper.FriendLinkMapper;
import com.xuan.service.IFriendLinkService;
import com.xuan.vo.FriendLinkVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 友情链接服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLinks> implements IFriendLinkService {

    @Override
    public List<FriendLinks> getAllFriendLink() {
        // TODO: 实现管理端获取所有友情链接逻辑
        return null;
    }

    @Override
    public void addFriendLink(FriendLinkDTO friendLinkDTO) {
        // TODO: 实现管理端添加友情链接逻辑
    }

    @Override
    public void batchDelete(List<Long> ids) {
        // TODO: 实现批量删除友情链接逻辑
    }

    @Override
    public void updateFriendLink(FriendLinkDTO friendLinkDTO) {
        // TODO: 实现管理端修改友情链接逻辑
    }

    @Override
    public List<FriendLinkVO> getVisibleFriendLink() {
        // TODO: 实现博客端获取可见友情链接逻辑
        return null;
    }
}
