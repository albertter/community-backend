package com.bjut.community.controller;

import com.bjut.community.entity.Comment;
import com.bjut.community.entity.DiscussPost;
import com.bjut.community.entity.Event;
import com.bjut.community.event.EventProducer;
import com.bjut.community.service.CommentService;
import com.bjut.community.service.DiscussPostService;
import com.bjut.community.service.UserService;
import com.bjut.community.util.CommunityConstant;
import com.bjut.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(path = "/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Comment comment) {

        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityType())
                .setData("postId", discussPostId);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());

        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentsById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        eventProducer.fireEvent(event);


        return "redirect:/discuss/detail/" + discussPostId;

    }
}
