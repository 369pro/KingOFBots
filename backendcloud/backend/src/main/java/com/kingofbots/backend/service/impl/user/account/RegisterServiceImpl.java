package com.kingofbots.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingofbots.backend.mapper.UserMapper;
import com.kingofbots.backend.pojo.User;
import com.kingofbots.backend.service.userInterface.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String,String> res = new HashMap<>();
        if(username == null) {
            res.put("error_message", "用户名不能为空");
            return res;
        }
        if(password == null || confirmedPassword == null) {
            res.put("error_message", "密码不能为空");
            return res;
        }

        username = username.trim();
        if(username.length() == 0){
            res.put("error_message", "用户名不能为空");
            return res;
        }
        if(password.length() == 0 || confirmedPassword.length() == 0){
            res.put("error_message", "密码不能为空");
            return res;
        }
        if(!password.equals(confirmedPassword)){
            res.put("error_message","两次密码不一致");
            return res;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        if(users.size() != 0) {
            res.put("error_message","用户名不能重复");
            return res;
        }

        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/145913_lg_2d8b6c35ae.jpg";
        User user = new User(null, username, encodedPassword, photo, 1500);
        userMapper.insert(user);
        res.put("error_message","success");
        return res;
    }
}
