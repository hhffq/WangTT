package com.changgou.canal.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author lht
 * @date 2025-08-09 14:16
 */

@Configuration
public class RabbitMqFanoutConfig {


    //上架  定义交换机名称
    public static final String GOODS_UP_EXCHANGE = "goods_up_exchange";

    //下架   定义交换机名称
    public static final String GOODS_DOWN_EXCHANGE = "goods_down_exchange";

    //删除  定义队列名称
    public static final String SEARCH_DELETE_QUEUE = "search_delete_queue";

    //定义队列名称
    public static final String SEARCH_ADD_QUEUE = "search_add_queue";

    //定义队列名称
    public static final String AD_UPDATE_QUEUE = "ad_update_queue";



    // 声明队列
    @Bean(SEARCH_DELETE_QUEUE)
    public Queue SEARCH_DELETE_QUEUE() {
        return new Queue(SEARCH_DELETE_QUEUE);
    }
    // 声明交换机
    @Bean(GOODS_DOWN_EXCHANGE)
    public Exchange GOODS_DOWN_EXCHANGE() {

        return ExchangeBuilder.fanoutExchange(GOODS_DOWN_EXCHANGE).durable(true).build();// 开启持久化
    }

    //下架  队列绑定交换机
    @Bean
    public Binding DOWN_UPDATE_QUEUE_BIND(@Qualifier(SEARCH_DELETE_QUEUE) Queue queue, @Qualifier(GOODS_DOWN_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

    // 声明队列
    @Bean
    public Queue queue() {
        return new Queue(AD_UPDATE_QUEUE);
    }

    // 声明队列
    @Bean(SEARCH_ADD_QUEUE)
    public Queue AD_UPDATE_QUEUE() {
        return new Queue(SEARCH_ADD_QUEUE);
    }



    // 声明交换机
    @Bean(GOODS_UP_EXCHANGE)
    public Exchange GOODS_UP_EXCHANGE() {

        return ExchangeBuilder.fanoutExchange(GOODS_UP_EXCHANGE).durable(true).build();// 开启持久化
    }

    // 队列绑定交换机
    @Bean
    public Binding AD_UPDATE_QUEUE_BIND(@Qualifier(SEARCH_ADD_QUEUE) Queue queue, @Qualifier(GOODS_UP_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

}
