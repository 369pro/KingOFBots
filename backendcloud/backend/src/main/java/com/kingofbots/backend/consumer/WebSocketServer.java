package com.kingofbots.backend.consumer;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingofbots.backend.consumer.utils.Game;
import com.kingofbots.backend.consumer.utils.JwtAuthentication;
import com.kingofbots.backend.mapper.BotMapper;
import com.kingofbots.backend.mapper.RecordMapper;
import com.kingofbots.backend.mapper.UserMapper;
import com.kingofbots.backend.pojo.Bot;
import com.kingofbots.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {             // userId-->链接
    public static final ConcurrentHashMap<Integer,WebSocketServer> users = new ConcurrentHashMap<>();
    private static UserMapper userMapper;  //与Mysql注入方式有区别
    public static RestTemplate restTemplate;
    public static RecordMapper recordMapper;  // 这里为什么要放到这个函数里面*
    private static BotMapper botMapper;
    private User user;
    private Integer startGameBotId = -1;      // -1表示亲自出马
    private Session session = null;    // 用于维护链接信息
    public Game game = null;          // 每名玩家的链接再维护一个地图信息
    // 调用微服务的addPlayer函数
    private static final String addPlayerUrl = "http://127.0.0.1:3001/player/add/";

    private static final String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

    @Autowired                         // 通过set函数注入
    public void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){WebSocketServer.recordMapper = recordMapper;}
    @Autowired
    public void setBotMapper(BotMapper botMapper){WebSocketServer.botMapper = botMapper;}
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session;
        //  token--> userid
        Integer userId = JwtAuthentication.getUserId(token);;
        this.user = userMapper.selectById(userId); // 用户一打开匹配页面就要把用户信息记录下来
        if(this.user != null){
            users.put(userId, this);
        }else{
            this.session.close();
        }
        // 建立连接
        System.out.println("connected!");
    }

    @OnClose
    public void onClose() {
        if(user != null)
        {
            users.remove(this.user.getId());
        }
        // 关闭链接
        System.out.println("closed!");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        System.out.println("received message");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching".equals(event)){
            this.startGameBotId = data.getInteger("bot_id");  // 前端botId传到这
            System.out.println("startGameBotId = "+startGameBotId);
            startMatching();
        }else if("stop-matching".equals(event)){
            stopMatching();
        } else if ("move".equals(event)) {
            move(data.getInteger("direction"));
        }
    }

    // 后端向前端发信息
    public void sendMessage(String message)
    {
        synchronized (this.session)
        {
            try{
                this.session.getBasicRemote().sendText(message);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void move(int direction){ // 匹配结束之后game就知道自己手上有哪两名玩家了
        if(user.getId().equals(game.getPlayerA().getId())) {
            if(game.getPlayerA().getBotId().equals(-1))   // 屏蔽bot的输入
                game.setNextStepA(direction);
        }
        else if(user.getId().equals(game.getPlayerB().getId())){
            if(game.getPlayerB().getBotId().equals(-1))
                game.setNextStepB(direction);
        }
    }
    public static void startGame(Integer aId, Integer bId)   // 微服务完成匹配后根据(webServer链接来调用此函数)
    {
        System.out.println("start game...");
        User userA = userMapper.selectById(aId);
        User userB = userMapper.selectById(bId);
        Bot botA = null, botB = null;
        for(Integer userId : users.keySet())
        {
            if(userId.equals(userA.getId())){
                Integer botAId = users.get(userId).startGameBotId;  // 找出对应连接的botId
                botA = botMapper.selectById(botAId);
            }
            if(userId.equals(userB.getId())){
                Integer botBId = users.get(userId).startGameBotId;
                botB = botMapper.selectById(botBId);
            }
        }

        Game game = new Game(
                13,
                14,
                20,
                userA.getId(),
                botA,
                userB.getId(),
                botB
        );
        game.createMap();
        if(users.get(userA.getId()) != null)                // 用户关闭此链接即为null
            users.get(userA.getId()).game = game;           // 分别赋值给两名玩家
        if(users.get(userB.getId()) != null)
            users.get(userB.getId()).game = game;
        game.start();

        JSONObject respGame = new JSONObject();          // 存储地图相关的信息
        respGame.put("a_id",game.getPlayerA().getId());
        respGame.put("a_sx",game.getPlayerA().getSx());
        respGame.put("a_sy",game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());

        // 服务端给用户1的答复
        JSONObject respUser1 = new JSONObject();
        respUser1.put("event","start-matching");
        respUser1.put("opponent_username",userB.getUsername());
        respUser1.put("opponent_photo",userB.getPhoto());
        respUser1.put("game",respGame);    // 向前端发送game的相关信息
        // 向user1发送 respUser1
        if(users.get(userA.getId()) != null)
            users.get(userA.getId()).sendMessage(respUser1.toJSONString());

        JSONObject respUser2 = new JSONObject();
        respUser2.put("event","start-matching");
        respUser2.put("opponent_username",userA.getUsername());
        respUser2.put("opponent_photo",userA.getPhoto());
        respUser2.put("game",respGame);
        // 向user2发送 respUser2
        if(users.get(userB.getId()) != null)
            users.get(userB.getId()).sendMessage(respUser2.toJSONString());
    }
    public void startMatching()
    {
        System.out.println("start matching...");
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",user.getId().toString());
        data.add("rating",user.getRating().toString());
        restTemplate.postForObject(addPlayerUrl, data, String.class);  //这里方法这么写不理解
    }
    public void stopMatching()
    {
        System.out.println("stop matching");
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id", user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class); // 调用微服务的函数
    }
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
