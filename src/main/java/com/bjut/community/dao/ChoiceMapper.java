package com.bjut.community.dao;

import com.bjut.community.entity.Choice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: 褚真
 * @date: 2021/8/8
 * @time: 15:21
 * @description:
 */
@Mapper
public interface ChoiceMapper {
    void save(List<Choice> list);
}
