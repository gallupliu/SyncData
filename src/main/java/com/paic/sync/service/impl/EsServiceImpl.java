package com.paic.sync.service.impl;


import com.paic.sync.service.EsService;
import com.paic.sync.utils.ESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.paic.sync.config.ESConfig;
/**
 * description: EsServiceImpl
 * date: 2020/11/12 10:59 下午
 * author: gallup
 * version: 1.0
 */
public class EsServiceImpl implements EsService {
    @Autowired
    private ESUtil esUtil;

    @RequestMapping("es")
    public String es(@RequestParam("index") String index, @RequestParam("str") String str){
        esUtil.keywordSearch(index,str,0,10);
        return "success";
    }
}
