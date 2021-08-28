package com.bjut.community;

import com.alibaba.excel.EasyExcel;
import com.bjut.community.dao.ChoiceMapper;
import com.bjut.community.entity.Choice;
import com.bjut.community.excel.ChoiceListener;
import com.bjut.community.excel.NoModelDataListener;
import com.bjut.community.service.ChoiceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: 褚真
 * @date: 2021/8/8
 * @time: 13:26
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ExcelTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelTest.class);
    @Autowired
    private ChoiceService choiceService;
    @Autowired
    private ChoiceMapper choiceMapper;

    @Test
    public void noModelRead() {
        String fileName = "C:\\Users\\chuzhen\\Desktop\\excel.xlsx";

        // 这里 只要，然后读取第一个sheet 同步读取会自动finish
        EasyExcel.read(fileName, new NoModelDataListener()).sheet().doRead();
    }

    /**
     * 最简单的读
     * <p>1. 创建excel对应的实体对象 参照{@link Choice}
     * <p>2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link ChoiceListener}
     * <p>3. 直接读即可
     */
    @Test
    public void simpleRead() {
        // 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
        // 写法1：
        String fileName = "C:\\Users\\chuzhen\\Desktop\\excel.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, Choice.class, new ChoiceListener(choiceService)).sheet().doRead();
    }
}
