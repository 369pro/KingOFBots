package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
@Component    // 将类注入bean
public class MatchingPool extends Thread{    // 全局只有一个线程

    private static List<Player> players = new ArrayList<>();
    private static RestTemplate restTemplate;
    private final ReentrantLock lock = new ReentrantLock();
    private static final String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating)
    {
        lock.lock();
        try{
            Player player = new Player(userId,rating, 0);
            players.add(player);
        }finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId)
    {
        lock.lock();
        try{
            List<Player> newPlayers = new ArrayList<>();
            for(Player player : players){
                if(!player.getUserId().equals(userId))
                    newPlayers.add(player);
            }
            players = newPlayers;
        }finally {
            lock.unlock();
        }
    }
    // 所有玩家等待时间+1
    private void increaseWaitingTime()
    {
        for(Player player : players)
            player.setWaitingTime(player.getWaitingTime()+1);
    }
    // 检查两名玩家是否符合匹配要求
    private boolean checkMatched(Player playerA, Player playerB)
    {
        Integer ratingDelta = Math.abs(playerA.getRating()- playerB.getRating());
        Integer matchingCondition = Math.min(playerA.getWaitingTime(),playerB.getWaitingTime())*10;
        return matchingCondition >= ratingDelta;
    }
    // 此线程内部访问players需要加锁(在run方法里面加锁)
    private void matchPlayers()
    {
        System.out.println("matching players .." + players.toString());
        int len = players.size();
        boolean[] used = new boolean[len];
        List<Player> newPlayers = new ArrayList<>();
        for(int i = 0; i < len; ++i)
        {
            if(used[i]) continue;   // 小的i可能带走大的j,所以这里要判断一下
            for(int j = i + 1; j < len; ++j)
            {
                if(used[j]) continue;
                Player playerA = players.get(i), playerB = players.get(j);
                if(checkMatched(playerA, playerB)) {
                    used[i] = used[j] = true;
                    sendMatchResult(playerA, playerB);
                    break;          // 找到了两个可以匹配的就break掉
                }
            }
        }
        for(int i = 0; i < len; ++i) if(!used[i]) newPlayers.add(players.get(i));
        players = newPlayers;
    }
    private void sendMatchResult(Player playerA, Player playerB)
    {
        System.out.println("send result " + playerA + " " + playerB);
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("a_id",playerA.getUserId().toString());
        data.add("b_id", playerB.getUserId().toString());
        // 返回匹配结果同时通知webServer端开始游戏
        restTemplate.postForObject(startGameUrl, data, String.class);
    }
    @Override
    public void run() {
        while(true)
        {
            try {
                Thread.sleep(1000);
                increaseWaitingTime();
                lock.lock();
                try{
                    matchPlayers();
                }finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
