package com.kingofbots.backend.controller.pk;

import com.kingofbots.backend.service.pkInterface.StartGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartGameController {
    @Autowired
    private StartGameService startGameService;
    @PostMapping("/pk/start/game/")
    public String startGame(@RequestParam MultiValueMap<String,String> data)
    {
        Integer aId = Integer.parseInt(data.getFirst("a_id"));
        Integer bId = Integer.parseInt(data.getFirst("b_id"));
        return startGameService.startGame(aId, bId);
    }
}
