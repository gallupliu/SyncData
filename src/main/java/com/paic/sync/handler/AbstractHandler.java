package com.paic.sync.handler;

import com.paic.sync.utils.ESUtil;
import com.paic.sync.utils.RedisUtil;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import com.alibaba.fastjson.JSONObject;
/**
 * description: AbstractHandler
 * date: 2020/11/11 8:01 下午
 * author: gallup
 * version: 1.0
 */
@Slf4j
public abstract class AbstractHandler {
    static String selectsql = null;
    static ResultSet retsult = null;

    @Value("${db.itemTableName}")
    private  String itemTableName;

    @Value("${db.itemId}")
    private  String itemId;

    /**
     * 下一个执行者
     */
    protected AbstractHandler nextHandler;

    /**
     * 事件类型 UPDATE、DELETE、INSERT
     */
    protected EventType eventType;

    @Autowired
    protected RedisUtil redisUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    private ESUtil esUtil = new ESUtil();
    @Autowired
    private ESUtil esUtil;

    /**
     * 启动时全量更新数据
     */
    @PostConstruct
    public void init(){

        List<Map<String, Object>> maps = queryAll(itemTableName);
        for(Map<String,Object> map:maps){
            String id = map.get(itemId).toString();
            if(redisUtil.get(itemTableName+":"+id)==null){
                Map<String, String> strMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    strMap.put(entry.getKey(),entry.getValue().toString());
                }
                //添加字段对应特征
                strMap.put("vec", "1111111");
                esUtil.getIkAnalyzeSearchTerms("","相互调用出错");
                String jsonStr = JSONObject.toJSONString(strMap);
                log.info("更新缓存数据：{}\r\n", jsonStr);
                redisUtil.setDefault(itemTableName+":"+id, jsonStr);

            }

        }

    }

    /**
     * 获取示例数据库 blog 的全部信息
     * @return 返回 json 数据
     * */
    public List<Map<String, Object>> queryAll(String tableName) {
        String sql = "select * from "+tableName;
//       执行sql语句
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        return maps;
    }

    /**
     * 传递处理的事件
     */
    public void handleMessage(Entry entry) {
        if (this.eventType == entry.getHeader().getEventType()) {
            //发生写入操作的库名
            String database = entry.getHeader().getSchemaName();
            //发生写入操作的表名
            String table = entry.getHeader().getTableName();
            //根据表名字处理
            log.info("监听到数据库：{}，表：{} 的 {} 事件", database, table, eventType.toString());
            //如果 rowChange 不为空,则执行 handleRowChange()
//            Optional.ofNullable(this.getRowChange(entry))
//                    .ifPresent(this::handleRowChange);
            if (this.getRowChange(entry) != null) {
                this.handleRowChange(this.getRowChange(entry), itemTableName,itemId);
            }
        } else {
            if (nextHandler != null) {
                nextHandler.handleMessage(entry);
            }
        }
    }

    /**
     * 处理数据库 UPDATE、DELETE、INSERT 的数据
     */
    public abstract void handleRowChange(RowChange rowChange, String tableName,String idName);

    /**
     * 获得数据库 UPDATE、DELETE、INSERT 的数据
     */
    private RowChange getRowChange(Entry entry) {
        RowChange rowChange = null;
        try {
            rowChange = RowChange.parseFrom(entry.getStoreValue());
        } catch (InvalidProtocolBufferException e) {
            log.error("根据CanalEntry获取RowChange异常:", e);
        }
        return rowChange;
    }

    /**
     * columns 转 map
     */
    protected Map<String, String> columnsToMap(List<Column> columns) {
        return columns.stream().collect(Collectors.toMap(Column::getName, Column::getValue));
    }

}
