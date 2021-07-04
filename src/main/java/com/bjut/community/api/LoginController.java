package com.bjut.community.api;

import com.bjut.community.entity.User;
import com.bjut.community.jwt.JWTUtil;
import com.bjut.community.service.impl.UserServiceImpl;
import com.bjut.community.util.*;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class LoginController implements CommunityConstant {
    @Autowired
    private UserServiceImpl userService;


    @Value("server.servlet.context-path")
    private String contextPath;


    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public Result login(@RequestBody User loginUser) {

        String username = loginUser.getUsername();
        User user = userService.findUserByUsername(username);
        if(user==null){
            return ResultGenerator.genSuccessResult("登录失败，用户名不存在。");
        }

        String password = loginUser.getPassword();
        password = CommunityUtil.md5(password + user.getSalt());


        if (user.getPassword().equals(password)) {
            return ResultGenerator.genSuccessResult("登录成功", JWTUtil.sign(username, password));
        } else {
            return ResultGenerator.genSuccessResult("登录失败，密码错误。");
        }

    }

//    @RequestMapping(path = "/logout", method = RequestMethod.GET)
//    public Result logout(@CookieValue("ticket") String ticket) {
//        Subject subject = SecurityUtils.getSubject();
//        subject.logout();//退出用户
//        return ResultGenerator.genSuccessResult("注销成功");
//    }


    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public Result register(User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            return ResultGenerator.genSuccessResult("注册成功");
        } else {
            return ResultGenerator.genFailResult("注销失败", map);
        }

    }

    @RequestMapping(path = "/activation/{userID}/{code}", method = RequestMethod.GET)
    public Result register(@PathVariable("userID") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            return ResultGenerator.genSuccessResult("激活成功");
        } else if (result == ACTIVATION_REPEAT) {
            return ResultGenerator.genSuccessResult("激活成功，重复激活");
        } else {
            return ResultGenerator.genFailResult("激活失败");
        }
    }

    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(100, 40, 4);
        // 设置字体
//        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);

        // 验证码存入session
        request.getSession().setAttribute("captcha", specCaptcha.text().toLowerCase());

        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }

}
