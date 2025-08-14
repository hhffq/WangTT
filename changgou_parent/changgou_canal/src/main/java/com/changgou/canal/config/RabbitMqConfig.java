package com.changgou.canal.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * @author lht
 * @date 2025-08-08 22:10
 */
//@Configuration
public class RabbitMqConfig {

    // ctrl + shift + u 变为大小写
    public static final String AD_UPDATE_QUEUE_POSITION = "ad_update_queue_position";


    @Bean
    public Queue queue() {
        return new Queue(AD_UPDATE_QUEUE_POSITION);
    }
}
