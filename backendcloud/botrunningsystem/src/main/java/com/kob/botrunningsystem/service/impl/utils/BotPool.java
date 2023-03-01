package com.kob.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();  //条件变量
    private final Queue<Bot> bots = new LinkedList<>();

    public void addBot(Integer userId, String botCode, String input)
    {
        Bot bot = new Bot(userId, botCode, input);
        lock.lock();
        try{
            bots.add(bot);
            condition.signalAll();     // 唤醒消费者进程
        }finally {
            lock.unlock();
        }

    }
    private void consume(Bot bot)
    {
        Consumer consumer = new Consumer();
        consumer.startTimeout(8000, bot);   // 8s Bot无动作自动结束
    }
    @Override
    public void run() {
        while (true)
        {
            lock.lock();
            if(bots.isEmpty()){
                try{
                    condition.await();   // 默认释放锁
                } catch (InterruptedException e) {
                    e.printStackTrace();  // 出现故障了就赶快解锁
                    lock.unlock();
                    break;                // 别忘了break掉
                }
            }else{
                Bot bot = bots.remove();
                lock.unlock();         // 拿出bot就要立刻解锁
                consume(bot);
            }
        }

    }
}
