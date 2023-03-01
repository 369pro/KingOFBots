package com.kingofbots.backend.controller.user.bot;

import com.kingofbots.backend.pojo.Bot;
import com.kingofbots.backend.service.userInterface.bot.GetListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetListController {
    @Autowired
    GetListService getListService;
    @GetMapping("/user/bot/getlist/")
    List<Bot> getList(){
        return getListService.getList();
    }
}
