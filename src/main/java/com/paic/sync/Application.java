package com.paic.sync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * description: Application
 * date: 2020/11/11 8:07 下午
 * author: gallup
 * version: 1.0
 */
@EnableScheduling     //开启定时任务支持
@SpringBootApplication
@EnableTransactionManagement //开启事务支持
//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//排除自动配置
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}


