package com.kingofbots.backend.controller.user.bot;

import com.kingofbots.backend.service.userInterface.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UpdateController {
    @Autowired
    UpdateService updateService;
    @PostMapping("/user/bot/update/")
    Map<String,String> update(@RequestParam Map<String,String> data)   // @RequestParm
    {
        return updateService.update(data);
    }
}
