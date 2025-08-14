package com.changgou.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.canal.config.RabbitMqConfig;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lht
 * @date 2025-08-07 23:57
 */

//@CanalEventListener //声明当前的类是canal的监听类
public class BusinessListener {
    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * @param eventType 当前操作数据库的类型
     * @param rowData   当前操作数据库的数据
     */
    @ListenPoint(schema = "changgou_business", table = "tb_ad")
    public void adUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        System.out.println("广告表数据发生发表");
//        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
//        beforeColumnsList.forEach((c) -> System.out.println("改变前的数据:" + c.getName() + "::" + c.getValue()));
//
//
//        System.out.println("----------------------------------------------------------------------");
//        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
//        afterColumnsList.forEach((c) -> System.out.println("改变后的数据" + c.getName() + "::" + c.getValue()));

        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("hah:" + column.getName() + "::" + column.getValue());
            if ("position".equals(column.getName())) {
                System.out.println("发送最新的数据到mq中：" + column.getValue());
                rabbitTemplate.convertAndSend("", RabbitMqConfig.AD_UPDATE_QUEUE_POSITION, column.getValue());
            }
        }
    }
}
