package com.changgou.search.listener;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.search.config.RabbitMqFanoutConfig;
import com.changgou.search.service.ESManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author lht
 * @date 2025-08-09 18:04
 */

@Component
public class GoodsUpListener {
    @Autowired
    private ESManagerService esManagerService;
    @Autowired
    private SkuFeign skuFeign;
    @RabbitListener(queues = RabbitMqFanoutConfig.SEARCH_ADD_QUEUE)
    public void receviceSEARCHADDQUEUEMessage(String spuId) {
        try {
            // 处理消息的业务逻辑
            System.out.println("查询skuList 并导入到索引库中:" + spuId);

            //查询skuList 并导入到索引库中
            esManagerService.importDataToESBySpuId(spuId);
        } catch (Exception e) {
            // 记录异常或进行其他错误处理
            e.printStackTrace();
        }


    }

}
