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
            return ResultGenerator.genFailResult("未登录");
        }
        DiscussPost post = new DiscussPost();
        post.setTitle(title);
        post.setUserId(user.getId());
        post.setContent(content);
        post.setCreateTime(new Date());
        System.out.println(post);
        discussPostService.addDiscussPost(post);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        //计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, post.getId());

        return ResultGenerator.genSuccessResult("发布成功");
    }

    /**
     * 查询帖子详情
     *
     * @param discussPostId 帖子id
     * @param offset          起始页
     * @param limit           数量
     * @return 结果
     */
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public Result getDiscussPost(@PathVariable("discussPostId") int discussPostId,
                                 @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                                 @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        User loginUser = (User) SecurityUtils.getSubject().getPrincipal();
        // 帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        logger.info(discussPost.getUserId() + "");
        // 作者
        User user = userService.findUserById(discussPost.getUserId());

        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);

        //是否点赞
        int likeStatus = loginUser == null ? 0 : likeService.findEntityLikeStatus(loginUser.getId(), ENTITY_TYPE_POST, discussPostId);

        // 评论分页信息
//        page.setLimit(5);
//        page.setPath("/discuss/detail/" + discussPostId);
//        page.setRows(discussPost.getCommentCount());

        // 评论: 给帖子的评论
        // 回复: 给评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, discussPost.getId(), offset, limit);
        // 评论VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                //点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                //是否点赞
                likeStatus = loginUser == null ? 0 : likeService.findEntityLikeStatus(loginUser.getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);
                //评论回复
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标
                        User target = null;
                        if (reply.getTargetId() != 0) {
                            target = userService.findUserById(reply.getTargetId());
                        }
                        replyVo.put("target", target);
                        //点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        //是否点赞
                        likeStatus = loginUser == null ? 0 : likeService.findEntityLikeStatus(loginUser.getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                // 回复数量
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
     * 获取帖子列表
     *
     * @param offset 页码起始页
     * @param limit  列表总数
     * @return 帖子列表
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
     * 置顶
     *
     * @param id 帖子id
     * @return 成功结果
     */
    @RequestMapping(path = "/top", method = RequestMethod.PUT)
    @RequiresRoles("admin")
    public Result setTop(int id) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        discussPostService.updateType(id, 1);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return ResultGenerator.genSuccessResult();
    }

    /**
     * 加精
     *
     * @param id 帖子id
     * @return 成功结果
     */
    @RequestMapping(path = "/wonderful", method = RequestMethod.PUT)
    @RequiresRoles("admin")
    public Result setWonderful(int id) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        discussPostService.updateStatus(id, 1);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        // 计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, id);

        return ResultGenerator.genSuccessResult();
    }

    /**
     * 删除
     *
     * @param id 帖子id
     * @return 成功结果
     */
    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    @RequiresRoles("admin")
    public Result setDelete(int id) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        discussPostService.updateStatus(id, 2);

        // 触发删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return ResultGenerator.genSuccessResult();
    }

}
