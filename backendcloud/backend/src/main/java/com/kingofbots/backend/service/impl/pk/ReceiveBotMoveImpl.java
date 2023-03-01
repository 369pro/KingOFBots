package com.kingofbots.backend.service.impl.pk;

import com.kingofbots.backend.consumer.WebSocketServer;
import com.kingofbots.backend.consumer.utils.Game;
import com.kingofbots.backend.consumer.utils.Player;
import com.kingofbots.backend.service.pkInterface.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        System.out.println("received userId " + userId +" "+ direction);
        WebSocketServer webSocketServer = WebSocketServer.users.get(userId);  // 同上防止用户意外断开连接
        if(webSocketServer != null)
        {
            Game game = webSocketServer.game;
            if(game != null){
                Player playerA = game.getPlayerA();
                Player playerB = game.getPlayerB();
                if(playerA.getId().equals(userId)){
                    game.setNextStepA(direction);
                }else if(playerB.getId().equals(userId))
                    game.setNextStepB(direction);
            }
        }
        return "receive bot move success!";
    }
}
