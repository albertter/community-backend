package com.bjut.community.api;

import com.bjut.community.util.Result;
import com.bjut.community.util.ResultGenerator;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhen
 */
@RestController
@RequestMapping(path = "/api")
public class TestAPI {
    @GetMapping("/require_auth")
    @RequiresAuthentication
    public Result requireAuth() {
        return ResultGenerator.genSuccessResult("已认证");
    }

    @GetMapping("/require_role")
    @RequiresRoles("admin")
    public Result requireRole() {
        return ResultGenerator.genSuccessResult("You are visiting require_role");
//        return new ResponseBean(200, "You are visiting require_role", null);
    }

    @GetMapping("/require_permission")
    @RequiresPermissions(logical = Logical.AND, value = {"view", "edit"})
    public Result requirePermission() {
        return ResultGenerator.genSuccessResult("ou are visiting permission require edit,view");
//        return new ResponseBean(200, "You are visiting permission require edit,view", null);
    }

    @RequestMapping(path = "/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result unauthorized() {
        return ResultGenerator.genFailResult("Unauthorized");
//        return new ResponseBean(401, "Unauthorized", null);
    }
}
