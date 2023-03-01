package com.kingofbots.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingofbots.backend.mapper.BotMapper;
import com.kingofbots.backend.pojo.Bot;
import com.kingofbots.backend.pojo.User;
import com.kingofbots.backend.service.impl.utils.UserDetailsImpl;
import com.kingofbots.backend.service.userInterface.bot.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class RemoveServiceImpl implements RemoveService {
    @Autowired
    BotMapper botMapper;
    @Override
    public Map<String, String> remove(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser =(UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        Map<String,String> map = new HashMap<>();
        int bot_id = Integer.parseInt(data.get("bot_id"));
        System.out.printf("bot.id = %d\n",bot_id );
        Bot bot = botMapper.selectById(bot_id);
        if(bot == null){
            map.put("error_message","bot为空");
            return map;
        }
        if(!Objects.equals(bot.getUserId(),user.getId()))
        {
            map.put("error_message","用户无权修改其他用户的bot");
            return map;
        }

        QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",bot_id);
        botMapper.delete(queryWrapper);              // ***这种写错了调试起来很麻烦的
        map.put("error_message","success");
        System.out.println("remove success");
        return map;
    }
}
