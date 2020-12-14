package com.paic.sync.handler;

import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description: DeleteHandler
 * date: 2020/11/11 8:04 下午
 * author: gallup
 * version: 1.0
 */
@Slf4j
@Component
public class DeleteHandler extends AbstractHandler {

    public DeleteHandler() {
        eventType = EventType.DELETE;
    }

    @Autowired
    public void setNextHandler(UpdateHandler updateHandler) {
        this.nextHandler = updateHandler;
    }

    @Override
    public void handleRowChange(RowChange rowChange, String tableName,String idName) {
        rowChange.getRowDatasList().forEach(rowData -> {
            rowData.getBeforeColumnsList().forEach(column -> {
                if (idName.equals(column.getName())) {
                    //清除 redis 缓存
                    log.info("清除 Redis 缓存 key={} 成功!\r\n", "blog:" + column.getValue());
                    redisUtil.del(tableName + ":" + column.getValue());
                }
            });
        });
    }
}
