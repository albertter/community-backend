package com.bjut.community.api;

import com.alibaba.fastjson.JSONObject;
import com.bjut.community.entity.Message;
import com.bjut.community.entity.Page;
import com.bjut.community.entity.User;
import com.bjut.community.service.MessageService;
import com.bjut.community.service.impl.UserServiceImpl;
import com.bjut.community.util.CommunityConstant;
import com.bjut.community.util.Result;
import com.bjut.community.util.ResultGenerator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@RestController
@RequiresAuthentication
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserServiceImpl userService;

    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public Result getLetterList(Page page) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/letter/list/");
        page.setRows(messageService.findConversationsCount(user.getId()));

        //会话列表
        List<Message> conversationsList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationsList != null) {
            for (Message message : conversationsList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("unreadCount", messageService.findLettersUnreadCount(user.getId(), message.getConversationId()));
                map.put("letterCount", messageService.findLettersCount(message.getConversationId()));

                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        //查询未读消息数量
        int letterUnreadCount = messageService.findLettersUnreadCount(user.getId(), null);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);

        Map<String, Object> res = new HashMap<>();
        res.put("conversations", conversations);
        res.put("letterUnreadCount", letterUnreadCount);
        res.put("noticeUnreadCount", noticeUnreadCount);

        return ResultGenerator.genSuccessResult(res);
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public Result getLetterDetail(@PathVariable("conversationId") String conversationId, Page page) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/letter/list/");
        page.setRows(messageService.findConversationsCount(user.getId()));

        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        Map<String, Object> res = new HashMap<>();
        res.put("letters", letters);
        res.put("target", getLetterTarget(conversationId));

        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return ResultGenerator.genSuccessResult(res);
    }

    public User getLetterTarget(String conversationId) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (user.getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    public List<Integer> getLetterIds(List<Message> letterList) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message letter : letterList) {
                if (user.getId() == letter.getToId() && letter.getStatus() == 0) {
                    ids.add(letter.getId());
                }
            }
        }
        return ids;
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    public Result sendLetter(String toName, String content) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        User target = userService.findUserByUsername(toName);
        if (target == null) {
            return ResultGenerator.genFailResult("目标用户不存在");
        }
        Message message = new Message();
        message.setFromId(user.getId());
        message.setToId(target.getId());
        message.setCreateTime(new Date());
        message.setContent(content);
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }

        messageService.addMessage(message);
        return ResultGenerator.genSuccessResult();
    }

    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public Result getNoticeList() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Map<String, Object> res = new HashMap<>();
        // 查询评论通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVO = new HashMap<>();
        messageVO.put("message", message);
        if (message != null) {

            String content = HtmlUtils.htmlUnescape(message.getContent());

            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", count);
            messageVO.put("unread", unreadCount);

        }
        res.put("commentNotice", messageVO);

        // 查询点赞通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        messageVO = new HashMap<>();
        messageVO.put("message", message);
        if (message != null) {

            String content = HtmlUtils.htmlUnescape(message.getContent());

            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVO.put("count", count);
            messageVO.put("unread", unreadCount);

        }
//        model.addAttribute("likeNotice", messageVO);
        res.put("likeNotice", messageVO);

        // 查询关注通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVO = new HashMap<>();
        messageVO.put("message", message);
        if (message != null) {

            String content = HtmlUtils.htmlUnescape(message.getContent());

            HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("entityType", data.get("entityType"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count", count);
            messageVO.put("unread", unreadCount);

        }
        res.put("followNotice", messageVO);
//        model.addAttribute("followNotice", messageVO);  // null

        int letterUnreadCount = messageService.findLettersUnreadCount(user.getId(), null);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
//        model.addAttribute("letterUnreadCount", letterUnreadCount);
//        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        res.put("letterUnreadCount", letterUnreadCount);
        res.put("noticeUnreadCount", noticeUnreadCount);


        return ResultGenerator.genSuccessResult(res);
    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public Result geNoticerDetail(@PathVariable("topic") String topic, Page page) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();

        // 分页信息
        page.setLimit(5);
        page.setPath("/notice/list/");
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVOList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                map.put("notice", notice);
                String content = HtmlUtils.htmlUnescape(notice.getContent());

                HashMap<String, Object> data = JSONObject.parseObject(content, HashMap.class);

                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityId", data.get("entityId"));
                map.put("entityType", data.get("entityType"));
                map.put("postId", data.get("postId"));

                map.put("fromUser", userService.findUserById(notice.getFromId()));
                noticeVOList.add(map);

            }
        }

        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return ResultGenerator.genSuccessResult(noticeVOList);

    }
}
