package com.kingofbots.backend.controller.user.account;

import com.kingofbots.backend.service.userInterface.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    @PostMapping("/user/account/register/")   // 从前端传来的信息
    public Map<String,String> register (@RequestParam Map<String,String> info)
    {
        String username = info.get("username");
        String password = info.get("password");
        String confirmedPassword = info.get("confirmedPassword");
        return registerService.register(username,password,confirmedPassword);
    }
}
