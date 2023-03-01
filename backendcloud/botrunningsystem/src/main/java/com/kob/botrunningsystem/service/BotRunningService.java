package com.kob.botrunningsystem.service;

public interface BotRunningService {
    //   返回nextstep的时候根据userid返回          传入当前局面信息
    String addBot(Integer userId, String botCode, String input);
}
