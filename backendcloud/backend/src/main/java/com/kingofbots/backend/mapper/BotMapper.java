package com.kingofbots.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kingofbots.backend.pojo.Bot;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BotMapper extends BaseMapper<Bot> {
}
