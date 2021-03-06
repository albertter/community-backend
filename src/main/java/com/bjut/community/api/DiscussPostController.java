package com.bjut.community.api;

import com.bjut.community.entity.Comment;
import com.bjut.community.entity.DiscussPost;
import com.bjut.community.entity.Event;
import com.bjut.community.entity.User;
import com.bjut.community.event.EventProducer;
import com.bjut.community.jwt.JWTUtil;
import com.bjut.community.service.CommentService;
import com.bjut.community.service.DiscussPostService;
import com.bjut.community.service.LikeService;
import com.bjut.community.service.UserService;
import com.bjut.community.util.CommunityConstant;
import com.bjut.community.util.RedisKeyUtil;
import com.bjut.community.util.Result;
import com.bjut.community.util.ResultGenerator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstant {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DiscussPostService discussPostService;
    //    @Autowired
//    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public Result addDiscussPost(@RequestParam("title") String title, @RequestParam("content") String content) {
        String username = JWTUtil.getUsername((String) SecurityUtils.getSubject().getPrincipal());
        User user = userService.findUserByUsername(username);
        System.out.println("user:" + user);
        if (user == null) {
            return ResultGenerator.genFailResult("?????????");
        }
        DiscussPost post = new DiscussPost();
        post.setTitle(title);
        post.setUserId(user.getId());
        post.setContent(content);
        post.setCreateTime(new Date());
        System.out.println(post);
        discussPostService.addDiscussPost(post);

        // ??????????????????
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        //??????????????????
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, post.getId());

        return ResultGenerator.genSuccessResult("????????????");
    }

    /**
     * ??????????????????
     *
     * @param discussPostId ??????id
     * @param offset          ?????????
     * @param limit           ??????
     * @return ??????
     */
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public Result getDiscussPost(@PathVariable("discussPostId") int discussPostId,
                                 @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                                 @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        User loginUser = (User) SecurityUtils.getSubject().getPrincipal();
        // ??????
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        logger.info(discussPost.getUserId() + "");
        // ??????
        User user = userService.findUserById(discussPost.getUserId());

        //????????????
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);

        //????????????
        int likeStatus = loginUser == null ? 0 : likeService.findEntityLikeStatus(loginUser.getId(), ENTITY_TYPE_POST, discussPostId);

        // ??????????????????
//        page.setLimit(5);
//        page.setPath("/discuss/detail/" + discussPostId);
//        page.setRows(discussPost.getCommentCount());

        // ??????: ??????????????????
        // ??????: ??????????????????
        // ????????????
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, discussPost.getId(), offset, limit);
        // ??????VO??????
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // ??????VO
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                //????????????
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                //????????????
                likeStatus = loginUser == null ? 0 : likeService.findEntityLikeStatus(loginUser.getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);
                //????????????
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // ??????VO??????
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // ????????????
                        User target = null;
                        if (reply.getTargetId() != 0) {
                            target = userService.findUserById(reply.getTargetId());
                        }
                        replyVo.put("target", target);
                        //????????????
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        //????????????
                        likeStatus = loginUser == null ? 0 : likeService.findEntityLikeStatus(loginUser.getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                // ????????????
                int replyCount = commentService.findCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        Map<String, Object> res = new HashMap<>();
        res.put("commentVoList", commentVoList);
        res.put("likeCount", likeCount);
        res.put("likeStatus", likeStatus);
        res.put("user", user);

        return ResultGenerator.genSuccessResult(res);

    }

    /**
     * ??????????????????
     *
     * @param offset ???????????????
     * @param limit  ????????????
     * @return ????????????
     */
    @RequestMapping(path = "/posts", method = RequestMethod.GET)
    @Cacheable(value = "discuss", condition = "#offset<5", keyGenerator = "simpleKeyGenerator")
    public Result getDiscussPostList(@RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                                     @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                     @RequestParam(value = "orderMod", required = false, defaultValue = "0") int orderMod) {
        logger.info("getDiscussPostList " + offset + " " + limit + " " + orderMod);
        return ResultGenerator.genSuccessResult(discussPostService.findDiscussPosts(0, offset, limit, orderMod));
    }


    /**
     * ??????
     *
     * @param id ??????id
     * @return ????????????
     */
    @RequestMapping(path = "/top", method = RequestMethod.PUT)
    @RequiresRoles("admin")
    public Result setTop(int id) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        discussPostService.updateType(id, 1);

        // ??????????????????
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return ResultGenerator.genSuccessResult();
    }

    /**
     * ??????
     *
     * @param id ??????id
     * @return ????????????
     */
    @RequestMapping(path = "/wonderful", method = RequestMethod.PUT)
    @RequiresRoles("admin")
    public Result setWonderful(int id) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        discussPostService.updateStatus(id, 1);

        // ??????????????????
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        // ??????????????????
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, id);

        return ResultGenerator.genSuccessResult();
    }

    /**
     * ??????
     *
     * @param id ??????id
     * @return ????????????
     */
    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    @RequiresRoles("admin")
    public Result setDelete(int id) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        discussPostService.updateStatus(id, 2);

        // ??????????????????
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return ResultGenerator.genSuccessResult();
    }

}
