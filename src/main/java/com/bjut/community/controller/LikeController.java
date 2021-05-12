package com.bjut.community.controller;

import com.bjut.community.entity.Event;
import com.bjut.community.entity.User;
import com.bjut.community.event.EventProducer;
import com.bjut.community.service.DiscussPostService;
import com.bjut.community.service.LikeService;
import com.bjut.community.util.CommunityConstant;
import com.bjut.community.util.CommunityUtil;
import com.bjut.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Controller
public class LikeController implements CommunityConstant {
//    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    private LikeService likeService;

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();
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
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityId(entityId)
                    .setEntityType(entityType)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);

            eventProducer.fireEvent(event);
        }


        return CommunityUtil.getJSONString(0, null, map);

    }
}
