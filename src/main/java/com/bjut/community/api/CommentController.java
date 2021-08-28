package com.bjut.community.api;

import com.bjut.community.entity.Comment;
import com.bjut.community.entity.DiscussPost;
import com.bjut.community.entity.Event;
import com.bjut.community.entity.User;
import com.bjut.community.event.EventProducer;
import com.bjut.community.service.CommentService;
import com.bjut.community.service.impl.DiscussPostServiceImpl;
import com.bjut.community.service.impl.UserServiceImpl;
import com.bjut.community.util.CommunityConstant;
import com.bjut.community.util.Result;
import com.bjut.community.util.ResultGenerator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author zhen
 */
@RestController
@RequestMapping(path = "/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private DiscussPostServiceImpl discussPostServiceImpl;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private EventProducer eventProducer;

    /**
     * 向帖子添加评论
     *
     * @param discussPostId 帖子id
     * @param comment       评论
     * @return 评论成功
     */
    @RequestMapping(path = "/{discussPostId}/add", method = RequestMethod.POST)
    @RequiresAuthentication
    public Result addComment(@PathVariable("discussPostId") int discussPostId, @RequestBody Comment comment) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(user.getId())
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityType())
                .setData("postId", discussPostId);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostServiceImpl.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());

        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentsById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        return ResultGenerator.genSuccessResult("发帖成功");
    }
}
