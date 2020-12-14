package com.paic.sync.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * description: UpdateHandler
 * date: 2020/11/11 8:06 下午
 * author: gallup
 * version: 1.0
 */
@Slf4j
@Component
public class UpdateHandler extends AbstractHandler {

    public UpdateHandler() {
        this.eventType = EventType.UPDATE;
    }


    @Override
    public void handleRowChange(RowChange rowChange, String tableName,String idName) {
        rowChange.getRowDatasList().forEach(rowData -> {
            //每一行的每列数据  字段名->值
            List<Column> beforeColumnsList = rowData.getBeforeColumnsList();
            Map<String, String> beforeMap = super.columnsToMap(beforeColumnsList);
            log.info("更新前数据：{}", JSONObject.toJSONString(beforeMap));

            List<Column> afterColumnsList = rowData.getAfterColumnsList();
            Map<String, String> afterMap = super.columnsToMap(afterColumnsList);
            String id = afterMap.get(idName);
            String afterJsonStr = JSONObject.toJSONString(afterMap);
            log.info("更新后数据：{}\r\n", afterJsonStr);
            /**
             *  高并发下，为保证数据一致性，当数据库更新后，不建议去更新缓存，
             *  而是建议直接删除缓存，由查询时再设置到缓存。
             *  这里为了演示，对缓存作更新操作，具体看业务需求。
             */
            redisUtil.setDefault(tableName + ":" + id, afterJsonStr);

        });
    }
}
