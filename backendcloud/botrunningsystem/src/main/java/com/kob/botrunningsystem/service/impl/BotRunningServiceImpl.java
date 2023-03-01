package com.kob.botrunningsystem.service.impl;

import com.kob.botrunningsystem.service.BotRunningService;
import com.kob.botrunningsystem.service.impl.utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {
    public static final BotPool botPool = new BotPool();  // 只有一个池子,故用static
    @Override
    public String addBot(Integer userId, String botCode, String input) {
        System.out.println("add bot "+ userId);
        botPool.addBot(userId, botCode, input);
        return "add bot success!";
    }
}
