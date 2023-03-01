package com.kob.matchingsystem.service.impl;

import com.kob.matchingsystem.service.MatchingService;
import com.kob.matchingsystem.service.impl.utils.MatchingPool;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    // 全局只有一个线程
    public static final MatchingPool matchingPool = new MatchingPool();
    @Override
    public String addPlayer(Integer userId, Integer rating) {
        System.out.println("add player " + userId + " " + rating + " success");
        matchingPool.addPlayer(userId,rating);
        return "add player " + userId + " " + rating + "success";
    }

    @Override
    public String removePlayer(Integer userId) {
        System.out.println("remove player " + userId + " success");
        matchingPool.removePlayer(userId);
        return "remove player " + userId + " success";
    }
}
