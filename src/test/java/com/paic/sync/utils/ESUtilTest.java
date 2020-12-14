package com.paic.sync.utils;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description: ESUtilTest
 * date: 2020/12/11 3:47 下午
 * author: gallup
 * version: 1.0
 */
@Component
public class ESUtilTest {
//    @Autowired
//    private RestHighLevelClient highLevelClient;
    @Autowired
    private  ESUtil esUtil;
    @Test
    public void test(){
        System.out.println("hello");
    }
}
