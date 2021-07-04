package com.bjut.community.api;

import com.bjut.community.entity.Event;
import com.bjut.community.entity.Page;
import com.bjut.community.entity.User;
import com.bjut.community.event.EventProducer;
import com.bjut.community.service.FollowService;
import com.bjut.community.service.UserService;
import com.bjut.community.util.CommunityConstant;
import com.bjut.community.util.Result;
import com.bjut.community.util.ResultGenerator;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class FollowController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    private FollowService followService;
    @Autowired
    private UserService userService;

    //    @Autowired
//    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;

    /**
     * 关注
     *
     * @param entityType 实体类型：帖子 1、评论 2
     * @param entityId   实体ID
     * @return 消息
     */
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    public Result follow(int entityType, int entityId) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        //关注
        followService.follow(user.getId(), entityType, entityId);

        // 触发事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityId(entityId)
                .setEntityType(entityType)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return ResultGenerator.genSuccessResult("关注成功");
    }

    /**
     * 取消关注
     *
     * @param entityType 实体类型：帖子 1、评论 2
     * @param entityId   实体ID
     * @return 消息
     */
    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    public Result unfollow(int entityType, int entityId) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        //关注
        followService.unfollow(user.getId(), entityType, entityId);
        return ResultGenerator.genSuccessResult("取消关注成功");
    }


    /**
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public Result getFollowees(@PathVariable("userId") int userId, @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        User user = userService.findUserById(userId);
        if (user == null) {
            return ResultGenerator.genErrorResult(400, "该用户不存在");
        }
        //关注数
        int followeeCount = (int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER);

        // 评论分页信息
//        page.setLimit(5);
//        page.setPath("/followees/" + userId);
//        page.setRows(followeeCount);

        List<Map<String, Object>> userList = followService.findFollowees(userId, offset, limit);
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("followeeCount", followeeCount);
        map.put("userList", userList);
        return ResultGenerator.genSuccessResult(map);

    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public Result getFollowers(@PathVariable("userId") int userId, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }

        //关注数
        int followerCount = (int) followService.findFollowerCount(ENTITY_TYPE_USER, userId);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows(followerCount);

        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("followerCount", followerCount);
        map.put("userList", userList);
        return ResultGenerator.genSuccessResult(map);
    }

    private boolean hasFollowed(int userId) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (user == null) {
            return false;
        }
        return followService.hasFollowed(user.getId(), ENTITY_TYPE_USER, userId);
    }
}
