package com.paic.sync.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paic.sync.enums.GoodsFiled;
import com.paic.sync.service.IndexService;
import com.paic.sync.utils.ESUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.xcontent.XContentType;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * description: IndexServiceImpl
 * date: 2020/11/16 3:54 下午
 * author: gallup
 * version: 1.0
 */
@Slf4j
public class IndexServiceImpl implements IndexService {
    private static final Integer SIZE = 1000;

    @Autowired
    private RestHighLevelClient client;

    @Override
    public boolean initIndex(String indexName,String filePath) {
        File file = new File(filePath);
        if(file.isDirectory()){
            for(File f: file.listFiles()){
                String fname = f.getName();
                BulkRequest bulkRequest = new BulkRequest();

                FileReader fr = null;
                BufferedReader br = null;
                try {
                    fr = new FileReader(f);
                    br = new BufferedReader(fr);
                    String line = br.readLine();
                    while( line != null){
                        IndexRequest request = new IndexRequest(indexName);
                        JSONObject jo = JSON.parseObject(line);
                        request.source(line, XContentType.JSON);
                        request.id(jo.getString(GoodsFiled.SPUID
                        ));
                        bulkRequest.add(request);
                        if(bulkRequest.numberOfActions() > SIZE){
                            try{
                                BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                                if(response.hasFailures()){
                                    log.warn(response.buildFailureMessage());
                                }
                            }catch (IOException e){
                                log.error("es写入数据失败",e);
                            }
                            bulkRequest = new BulkRequest();
                        }
                        line = br.readLine();
                    }
                    if(bulkRequest.numberOfActions() >0){
                        try{
                            BulkResponse response = client.bulk(bulkRequest,RequestOptions.DEFAULT);
                            if(response.hasFailures()){
                                log.warn(response.buildFailureMessage());
                            }
                        }catch (Exception e){
                            log.error("es写入数据失败",e);
                        }
                    }
                }catch (Exception e){
                    log.error("读取文件失败:"+fname,e);
                }finally {
                    if (br != null){
                        try{
                            br.close();
                        }catch (IOException e){
                            log.error("br关闭失败",e);
                        }
                    }

                    if (fr != null){
                        try{
                            fr.close();
                        }catch (IOException e){
                            log.error("",e);
                        }
                    }

                }
            }
        }

        return false;
    }
//    public static void main(String []args){
//        IndexServiceImpl indexService = new IndexServiceImpl() ;
//        indexService.initIndex("spu","/Users/gallup/Downloads/归档/");
//    }

}
