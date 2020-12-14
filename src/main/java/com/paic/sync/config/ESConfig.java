package com.paic.sync.config;

import com.alibaba.fastjson.JSONObject;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * description: ESConfig
 * date: 2020/11/12 10:34 下午
 * author: gallup
 * version: 1.0
 */
@Slf4j
@Setter
@Component
@ConfigurationProperties("es")
public class ESConfig {
    private String ip;
    private int port;

    @Bean
    public RestHighLevelClient getHighLevelClient(){
        HttpHost httpHost = new HttpHost(ip,port,"http");
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost[]{httpHost}));
        log.info("es启动......");
        return client;
    }

}
