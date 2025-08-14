package com.changgou.system.IpFilter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lht
 * @date 2025-07-24 21:48
 */
@Component
public class UrlFilter implements GlobalFilter, Ordered {
    // 例如当用户访问 改地址时 http://localhost:9101/goods/brand/category/手机
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("经过第2个过滤器IPFilter");
        // 获取访问的请求路径
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("path:" + path);//path:  /brand/category/手机

        // 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 4;
    }
}
