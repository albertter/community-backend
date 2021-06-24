package com.bjut.community.controller;

import com.bjut.community.entity.User;
import com.bjut.community.service.UserService;
import com.bjut.community.util.CommunityConstant;
import com.bjut.community.util.CommunityUtil;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.wf.captcha.utils.CaptchaUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;


    @Value("server.servlet.context-path")
    private String contextPath;

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(Model model, String username, String password, String code,
                        boolean rememberme, HttpServletRequest request, HttpServletResponse response) {


        Map<String, Object> map = new HashMap<>();
        try {
            if (CaptchaUtil.ver(code, request)) {
                //获取主体对象
                Subject subject = SecurityUtils.getSubject();
                User user = userService.findUserByUsername(username);
                password = CommunityUtil.md5(password + user.getSalt());
                UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberme);

                subject.login(token);
                return "redirect:/index";
            } else {
                CaptchaUtil.clear(request);
                model.addAttribute("kaptchaMsg", "验证码不正确");
                return "/site/login";
            }
        } catch (UnknownAccountException e) {
            e.printStackTrace();
            map.put("usernameMsg", "用户名错误!");
            System.out.println("用户名错误!");
        } catch (IncorrectCredentialsException e) {
            e.printStackTrace();
            map.put("usernameMsg", "密码错误!");
            System.out.println("密码错误!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        model.addAttribute("usernameMsg", map.get("usernameMsg"));
        model.addAttribute("passwordMsg", map.get("passwordMsg"));
        return "/site/login";

    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();//退出用户
        return "redirect:/index";
    }

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功");
            model.addAttribute("target", "/community/index");
            return "site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));

            return "/site/register";
        }

    }

    @RequestMapping(path = "/activation/{userID}/{code}", method = RequestMethod.GET)
    public String register(Model model, @PathVariable("userID") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功");
            model.addAttribute("target", "/community/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "激活成功，重复激活");
            model.addAttribute("target", "/community/login");
        } else {
            model.addAttribute("msg", "激活失败");
            model.addAttribute("target", "/community/index");
        }
        return "site/operate-result";
    }

//    @RequestMapping(value = "/kaptcha", method = RequestMethod.GET)
//    public void getKaptcha(HttpSession session, HttpServletResponse response) {
//        //生成验证码
//        String code = kaptchaProduser.createText();
//        BufferedImage img = kaptchaProduser.createImage(code);
//        //把验证码存入session
//        session.setAttribute("kaptcha", code);
//        //
//        response.setContentType("image/png");
//        try {
//            OutputStream os = response.getOutputStream();
//            ImageIO.write(img, "png", os);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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
