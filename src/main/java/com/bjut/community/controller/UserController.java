package com.bjut.community.controller;


import com.bjut.community.entity.User;
import com.bjut.community.service.FollowService;
import com.bjut.community.service.LikeService;
import com.bjut.community.service.UserService;
import com.bjut.community.util.CommunityConstant;
import com.bjut.community.util.CommunityUtil;
import com.mysql.cj.util.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;


@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domainPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    //    @Autowired
//    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;

    //    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    //    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "没有选择图片");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        assert fileName != null;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isNullOrEmpty(suffix)) {
            model.addAttribute("error", "文件格式不正确");
            return "/site/setting";
        }

        fileName = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage());
            throw new RuntimeException(e);
        }
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        String headerUrl = domainPath + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public String getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        fileName = uploadPath + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/" + suffix);

        FileInputStream fis;
        OutputStream os;
        try {
            fis = new FileInputStream(fileName);
            os = response.getOutputStream();

            byte[] buffer = new byte[1024];
            int b;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }

        } catch (IOException e) {
            logger.error("打开头像失败" + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/index";

    }

    //    @LoginRequired
    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updatePassword(Model model, String oldPassword, String newPassword) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (userService.updatePassword(user, oldPassword, newPassword) == 0) {
            model.addAttribute("error", "原密码错误");
            return "site/setting";
        }
        return "redirect:/logout";
    }

    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String updatePassword(@PathVariable("userId") int userId, Model model) {
        User loginUser = (User) SecurityUtils.getSubject().getPrincipal();
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        //点赞数
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        //关注数
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝数
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        //当前用户是否关注该用户
        boolean hasFollowed = false;
        if (loginUser != null) {
            hasFollowed = followService.hasFollowed(loginUser.getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }
}
