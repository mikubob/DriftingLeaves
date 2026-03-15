package com.xuan.controller.home;


import com.xuan.result.Result;
import com.xuan.service.ISocialMediaService;
import com.xuan.vo.SocialMediaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  首页端社交媒体接口
 */
@RestController("homeSocialMediaController")
@RequestMapping("/home/socialMedia")
public class SocialMediaController {

    @Autowired
    private ISocialMediaService socialMediaService;

    /**
     * 获取可见社交媒体信息
     */
    @GetMapping
    public Result<List<SocialMediaVO>> getSocialVisibleMedia() {
        List<SocialMediaVO> socialMediaVOList = socialMediaService.getVisibleSocialMedia();
        return Result.success(socialMediaVOList);
    }
}
