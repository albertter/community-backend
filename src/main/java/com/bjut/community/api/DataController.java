package com.bjut.community.api;

import com.bjut.community.service.DataService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author zhen
 */
@RestController
public class DataController {
    @Autowired
    private DataService dataService;

    @RequestMapping(path = "/data/uv", method = RequestMethod.POST)
//    @RequiresRoles("admin")
    public Long getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        return dataService.calculateUV(start, end);
    }

    @RequestMapping(path = "/data/dau", method = RequestMethod.POST)
//    @RequiresRoles("admin")
    public Long getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        return dataService.calculateDAU(start, end);
    }
}
