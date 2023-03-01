package com.kingofbots.backend.service.userInterface.account;

import java.util.Map;

public interface LoginService {  // 统一返回Map类型
    Map<String,String> getToken(String username, String password);
}
