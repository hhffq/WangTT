package com.changgou.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.canal.config.RabbitMqFanoutConfig;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


/**
 * @author lht
 * @date 2025-08-09 14:37
 */
@CanalEventListener
public class SpuListener {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @ListenPoint(schema = "changgou_goods", table = "tb_spu")
    public void sendMessage(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        System.out.println("改变前的数据加入到Map中");
        Map<String, String> beforeMap = new HashMap<>();
        rowData.getBeforeColumnsList().forEach(c -> beforeMap.put(c.getName(), c.getValue()));


        System.out.println("改变后的数据加入到Map中");
        Map<String, String> afterMap = new HashMap<>();
        rowData.getAfterColumnsList().forEach(c -> afterMap.put(c.getName(), c.getValue()));


        // 获取上架 的 is_marketable
        if ("0".equals(beforeMap.get("is_marketable")) && "1".equals(afterMap.get("is_marketable"))) {
            String s = afterMap.get("id");
            System.out.println("s:" + s);
            rabbitTemplate.convertAndSend(RabbitMqFanoutConfig.GOODS_UP_EXCHANGE, "", afterMap.get("id"));
            System.out.println("发送成功！！！" + afterMap.get("id"));
        }

        // 获取下架 的 is_marketable
        if ("1".equals(beforeMap.get("is_marketable")) && "0".equals(afterMap.get("is_marketable"))) {
            String s = afterMap.get("id");
            System.out.println("s:" + s);
            rabbitTemplate.convertAndSend(RabbitMqFanoutConfig.GOODS_DOWN_EXCHANGE, "", afterMap.get("id"));
            System.out.println("发送成功！！！" + afterMap.get("id"));
        }

    }


}
