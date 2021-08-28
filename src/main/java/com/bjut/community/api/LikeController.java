package com.bjut.community.api;

import com.bjut.community.entity.Event;
import com.bjut.community.entity.User;
import com.bjut.community.event.EventProducer;
import com.bjut.community.service.DiscussPostService;
import com.bjut.community.service.LikeService;
import com.bjut.community.util.CommunityConstant;
import com.bjut.community.util.Result;
import com.bjut.community.util.ResultGenerator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
public class LikeController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    private LikeService likeService;

    @Autowired
    private DiscussPostService DiscussPostService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @RequiresAuthentication
    public Result like(int entityType, int entityId, int entityUserId, int postId) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        //点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        //获取数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //获取状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 触发事件
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityId(entityId)
                    .setEntityType(entityType)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);

            eventProducer.fireEvent(event);
        }


        return ResultGenerator.genSuccessResult("关注成功", map);

    }
}
