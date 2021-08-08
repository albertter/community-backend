package com.bjut.community.service.impl;

import com.bjut.community.dao.ChoiceMapper;
import com.bjut.community.entity.Choice;
import com.bjut.community.service.ChoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: 褚真
 * @date: 2021/8/8
 * @time: 15:26
 * @description:
 */
@Service
public class ChoiceServiceImpl implements ChoiceService {
    @Autowired
    private ChoiceMapper choiceMapper;

    @Override
    public void save(List<Choice> list) {
        choiceMapper.save(list);
    }
}
