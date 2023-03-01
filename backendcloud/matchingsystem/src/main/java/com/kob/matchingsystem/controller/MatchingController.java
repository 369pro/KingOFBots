package com.kob.matchingsystem.controller;

import com.kob.matchingsystem.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchingController {
    @Autowired
    MatchingService matchingService;
    @PostMapping("/player/add/")  // 路径最后记得加/,webServer端向微服务传过来的数据
    public String addPlayer(@RequestParam MultiValueMap<String,String> data)
    {
        Integer userId = Integer.parseInt(data.getFirst("user_id"));
        Integer rating = Integer.parseInt(data.getFirst("rating"));
        return matchingService.addPlayer(userId, rating);
    }

    @PostMapping("/player/remove/")  //还是post因为要给微服务发送userId
    public String removePlayer(@RequestParam MultiValueMap<String,String> data)
    {
        Integer userId = Integer.parseInt(data.getFirst("user_id"));
        return matchingService.removePlayer(userId);
    }
}
