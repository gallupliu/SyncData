package com.paic.sync.utils;

//import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * description: Demo
 * date: 2020/11/18 3:01 下午
 * author: gallup
 * version: 1.0
 */
@Slf4j
public class Demo {
//    @Autowired
//    private static  ESUtil esUtil;

    /**
     * 调用 ES 获取 IK 分词后结果
     *
     * @param searchContent
     * @return
     */
//
//    private List getIkAnalyzeSearchTerms(String searchContent) {
//
//// 调用 IK 分词分词
//
//        AnalyzeRequestBuilder ikRequest = new AnalyzeRequestBuilder(elasticsearchTemplate.getClient(),
//
//                AnalyzeAction.INSTANCE, "indexName", searchContent);
//
//        ikRequest.setTokenizer("ik");
//
//        List ikTokenList = ikRequest.execute().actionGet().getTokens();
//
//// 循环赋值
//
//        List searchTermList = new ArrayList<>();
//
//        ikTokenList.forEach(ikToken -> { searchTermList.add(ikToken.getTerm()); });
//
//        return searchTermList;
//
//    }



//    public void main(String[] args) {
//        RestHighLevelClient getHighLevelClient = esUtil.getHighLevelClient();
////        Object blogList = demo.queryAll();
//
//    }

}
