package com.changgou.search.listener;

import com.changgou.search.config.RabbitMqFanoutConfig;
import com.changgou.search.dao.ESManagerMapper;
import com.changgou.search.service.ESManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lht
 * @date 2025-08-09 22:52
 */

@Component
public class GoodsDownListener {
    @Autowired
    private ESManagerService esManagerService;

    @RabbitListener(queues = RabbitMqFanoutConfig.SEARCH_DELETE_QUEUE)
    public void receviceSEARCHDELETEQUEUEMessage(String spuId) {
        System.out.println("删除索引库监听类,接收到的spuId:" + spuId);

        //查询skuList 并导入到索引库中
        esManagerService.deleteBySpuId(spuId);
    }
}
