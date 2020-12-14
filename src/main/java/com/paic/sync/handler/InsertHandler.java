package com.paic.sync.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * description: InsertHandler
 * date: 2020/11/11 8:05 下午
 * author: gallup
 * version: 1.0
 */
@Slf4j
@Component
public class InsertHandler extends AbstractHandler {
    public InsertHandler() {
        this.eventType = EventType.INSERT;
    }

    @Autowired
    public void setNextHandler(DeleteHandler deleteHandler) {
        this.nextHandler = deleteHandler;
    }

    @Override
    public void handleRowChange(RowChange rowChange, String tableName,String idName) {
        rowChange.getRowDatasList().forEach(rowData -> {
            //每一行的每列数据  字段名->值
            List<Column> afterColumnsList = rowData.getAfterColumnsList();
            Map<String, String> map = super.columnsToMap(afterColumnsList);
            String id = map.get(idName);
            //添加字段对应特征
            map.put("vec", "1111111");
            String jsonStr = JSONObject.toJSONString(map);
            log.info("新增的数据：{}\r\n", jsonStr);
            redisUtil.setDefault(tableName + ":" + id, jsonStr);
        });
    }

}
