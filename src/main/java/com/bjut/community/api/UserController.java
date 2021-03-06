package com.bjut.community.api;


import com.bjut.community.entity.User;
import com.bjut.community.service.FollowService;
import com.bjut.community.service.impl.LikeServiceImpl;
import com.bjut.community.service.impl.UserServiceImpl;
import com.bjut.community.util.CommunityConstant;
import com.bjut.community.util.CommunityUtil;
import com.bjut.community.util.Result;
import com.bjut.community.util.ResultGenerator;
import com.mysql.cj.util.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;


@RestController
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
    private UserServiceImpl userService;

    //    @Autowired
//    private HostHolder hostHolder;
    @Autowired
    private LikeServiceImpl likeServiceImpl;
    @Autowired
    private FollowService followService;


    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    @RequiresAuthentication
    public Result uploadHeader(MultipartFile headerImage) {
        if (headerImage == null) {
            return ResultGenerator.genErrorResult(403, "??????????????????");
        }
        String fileName = headerImage.getOriginalFilename();
        assert fileName != null;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isNullOrEmpty(suffix)) {
            return ResultGenerator.genErrorResult(403, "?????????????????????");
        }

        fileName = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("??????????????????" + e.getMessage());
            throw new RuntimeException(e);
        }
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        String headerUrl = domainPath + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return ResultGenerator.genSuccessResult("??????????????????");
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
            logger.error("??????????????????" + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/index";

    }

    @RequiresAuthentication
    @RequestMapping(path = "/update", method = RequestMethod.PUT)
    public Result updatePassword(String oldPassword, String newPassword) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (userService.updatePassword(user, oldPassword, newPassword) == 0) {
            return ResultGenerator.genErrorResult(400, "?????????????????????");
        }
        return ResultGenerator.genSuccessResult("??????????????????");
    }

    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public Result getProfile(@PathVariable("userId") int userId) {
        User loginUser = (User) SecurityUtils.getSubject().getPrincipal();
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("??????????????????");
        }
        //?????????
        int likeCount = likeServiceImpl.findUserLikeCount(userId);
        //?????????
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        //?????????
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        //?????????????????????????????????
        boolean hasFollowed = false;
        if (loginUser != null) {
            hasFollowed = followService.hasFollowed(loginUser.getId(), ENTITY_TYPE_USER, userId);
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("likeCount", likeCount);
        hashMap.put("followeeCount", followeeCount);
        hashMap.put("followerCount", followerCount);
        hashMap.put("hasFollowed", hasFollowed);
        return ResultGenerator.genSuccessResult(hashMap);
    }
}
