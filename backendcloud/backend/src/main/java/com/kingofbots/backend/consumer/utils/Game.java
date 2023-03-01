package com.kingofbots.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kingofbots.backend.consumer.WebSocketServer;
import com.kingofbots.backend.pojo.Bot;
import com.kingofbots.backend.pojo.Record;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    private final Integer rows;
    private final Integer cols;
    private final Integer innerWallCnt;
    private final int[][] g;
    private final static int[] dx = {-1,0,1,0}, dy = {0,1,0,-1};

    private Player playerA, playerB;
    private ReentrantLock lock = new ReentrantLock();
    private Integer nextStepA = null, nextStepB = null;  // 0 1 2 3表示...
    private String status = "playing";       // finished
    private String loser = "";                // all 平局    A: A输   B: B输
    private static final String addBotUrl = "http://127.0.0.1:3002/bot/add/";

    public Game(Integer rows,
                Integer cols,
                Integer innerWallCnt,
                Integer idA,
                Bot botA,
                Integer idB,
                Bot botB)
    {
        this.rows = rows;
        this.cols = cols;
        this.innerWallCnt = innerWallCnt;
        this.g = new int[rows][cols];
        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if(botA != null){
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if(botB != null){
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        this.playerA = new Player(idA, botIdA, botCodeA, rows-2, 1, new ArrayList<>());
        this.playerB = new Player(idB, botIdB, botCodeB, 1, cols-2, new ArrayList<>());
    }

    public int[][] getG(){return  this.g;}
    public String getMapString()
    {
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < rows; ++i)
            for(int j = 0; j < cols; ++j)
                res.append(g[i][j]);
        return res.toString();
    }

    public boolean check_connective(int sx, int sy, int tx, int ty)
    {
        if(sx == tx && sy ==ty) return true;
        g[sx][sy] = 1;   // 1表示true
        for(int i = 0; i < 4; ++i){
            int x = sx + dx[i];
            int y = sy + dy[i];
            if(x>=0 && x<this.rows && y>=0 && y<this.cols && g[x][y]==0)
                if(check_connective(x,y,tx,ty)) {
                    g[sx][sy] = 0;   // 能连通,也要擦除
                    return true;
                }
        }
        g[sx][sy] = 0;
        return false;
    }

    public boolean draw(){
        for(int i = 0; i < this.rows; ++i)
            for(int j = 0; j < this.cols; ++j)
                g[i][j] = 0;

        for(int r = 0; r < this.rows; r++)
            g[r][0] = g[r][this.cols-1] = 1;
        for(int c = 0; c < this.cols; c++)
            g[0][c] = g[this.rows-1][c] = 1;

        Random random = new Random();
        for(int i = 0; i < this.innerWallCnt/2; ++i){
            for(int j = 0; j < 1000; ++j) //对于每个i,随机1000次一般能找到一个位置创建墙
            {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);
                // 创建过的就不要再搞一次了,地图是对称的
                if(g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1)
                    continue;
                if(r == this.rows-2 && c == 1 || r == 1 && c == this.cols-2)
                    continue;
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }
        return check_connective(this.rows-2, 1, 1, this.cols-2);
    }
    public void createMap(){
        for(int i = 0; i < 1000; ++i) if(draw()) break;
    }
    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try{
            this.nextStepA = nextStepA;
        }finally {                     // 这样写的好处是报了异常也会自动解锁
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try{
            this.nextStepB = nextStepB;
        }finally {
            lock.unlock();
        }
    }

    public Player getPlayerA(){return this.playerA;}
    public Player getPlayerB(){return this.playerB;}
    // 获取局面信息
    private String getInput(Player player)
    {
        Player me, you;
        if(player.getId().equals(playerA.getId())){
            me = playerA;
            you = playerB;
        }else{
            me = playerB;
            you = playerA;
        }
        return getMapString()+"#"+
                me.getSx()+"#"+
                me.getSy()+"#("+
                me.getStepsString()+")#"+               // 这里避免steps空操作加个()
                you.getSx()+"#"+
                you.getSy()+"#("+
                you.getStepsString()+")";               // 最后一个不用#了
    }
    private void sendBotCode(Player player)
    {
        if(player.getBotId().equals(-1)) return;
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",player.getId().toString());  // 用字符串传递
        data.add("bot_code",player.getBotCode());
        data.add("input", getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl,data,String.class);
    }

    public boolean nextStep(){    // 判断时候同时获得了A,B的操作
        try{
            Thread.sleep(200);  // 前端speed=5的话200ms才能走一格,否则来不赢刷新
        }catch (InterruptedException e){
            throw new RuntimeException();
        }
        sendBotCode(playerA);   // 如果是亲自出马的话就不会传数据到BotRunningSystem
        sendBotCode(playerB);   // 向BotRunningSystem发送bot代码以获得nextStep
        for(int i = 0; i < 30; i++){
            try {
                Thread.sleep(200);  // 每200ms轮询查一次
                lock.lock();
                try{
                    if(nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                }finally {
                    lock.unlock();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return false;
    }
                                    // 判断A是否撞到B
    public boolean checkValid(List<Cell> playerA, List<Cell> playerB)
    {
        int len = playerA.size();
        Cell head = playerA.get(len-1);                // 蛇头
        if(this.g[head.x][head.y] == 1) {
            System.out.println("撞墙");
            return false;
        }
        for(int i = 0; i < len - 1; ++i){
            if(head.x == playerA.get(i).x && head.y == playerA.get(i).y){
                System.out.println("撞自己身体");
                return false;
            }
        }
        for(int i = 0; i < playerB.size(); ++i)
        {
            if (head.x == playerB.get(i).x && head.y == playerB.get(i).y){
                System.out.println("撞他人身体");
                return false;
            }
        }
        return true;
    }
    public void judge(){
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();
        boolean validA = checkValid(cellsA,cellsB);
        boolean validB = checkValid(cellsB,cellsA);
        if(!validA || !validB)
        {
            this.status = "finished";
            if(!validA && !validB) loser = "all";
            else if(!validA) loser = "A";
            else loser = "B";

        }
    }
                               // JSON格式
    public void sendAllMessage(String message){
        if(WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if(WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }
    public void sendResult(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event","result");
        jsonObject.put("loser", this.loser);
        saveToDataBase();
        sendAllMessage(jsonObject.toJSONString());
    }
    public void sendMove(){
        JSONObject jsonObject = new JSONObject();
        lock.lock();
        try{
            jsonObject.put("event", "move");
            jsonObject.put("a_direction", nextStepA);  // 后端识别用户键盘操作然后将消息发给前端
            jsonObject.put("b_direction", nextStepB);
            sendAllMessage(jsonObject.toJSONString());
            nextStepA = nextStepB = null;              // 记得清空操作
        }finally {
            lock.unlock();
        }
    }
    public void saveToDataBase()
    {
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);

    }

    @Override
    public void run() {
        for(int i = 0; i < 1000; ++i){   // 走一步看一步,最多走1000步分胜负
            if(nextStep()){
                judge();
                if(this.status.equals("finished")) {
                    sendResult();
                    break;               // 别忘记break！真的是寄！
                }
                else sendMove();
            }else{    // 有人没有输入,游戏结束
                this.status = "finished";
                lock.lock();
                try{
                    if(nextStepA == null && nextStepB == null){
                        loser = "all";
                        System.out.println("loser all");
                    }
                    else if(nextStepA == null) {
                        loser = "A";
                        System.out.println("loserA 超时！");
                    }
                    else {
                        loser = "B";
                        System.out.println("loserB 超时！");
                    }
                }finally {
                    lock.unlock();
                }
                sendResult();
                break;      // 结束之前向前端发消息
            }
        }
    }
}
