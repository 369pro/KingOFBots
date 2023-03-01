package com.kingofbots.backend.controller.user.account;

import com.kingofbots.backend.service.userInterface.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;
    @PostMapping("/user/account/token/")         // 前端提交的数据就是一个字典
    public Map<String,String> getToken(@RequestParam Map<String,String> info)
    {
        String username = info.get("username");
        String password = info.get("password");
        System.out.println(username+" "+password);
        return loginService.getToken(username,password);
    }
}
