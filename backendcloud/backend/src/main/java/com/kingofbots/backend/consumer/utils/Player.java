package com.kingofbots.backend.consumer.utils;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private Integer id;
    private Integer botId;
    private String botCode;
    private Integer sx;
    private Integer sy;
    private ArrayList<Integer> steps;   // 记录每一步的方向,利用此可以还原出蛇

    private boolean checkTailIncreasing(int step){
        if(step <= 6) return true;
        return step % 3 == 0;
    }
    public List<Cell> getCells()
    {
        List<Cell> res = new ArrayList<>();
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        res.add(new Cell(x, y));
        for(int d : steps){
            x = x + dx[d];
            y = y + dy[d];
            res.add(new Cell(x, y));
        }
        for(int i = 1; i < steps.size(); ++i)
        {
            if(!checkTailIncreasing(i)) res.remove(0);
        }
        return res;
    }

    public String getStepsString()
    {
        StringBuilder res = new StringBuilder();
        for(int step : steps) res.append(step);
        return res.toString();
    }
}
