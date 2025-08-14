package com.changgou.system.IpFilter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * @author lht
 * @date 2025-07-24 21:45
 */

@Component
public class IpFilter implements GlobalFilter, Ordered {


    // 例如当用户访问 改地址时 http://localhost:9101/goods/brand/category/手机
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        System.out.println("经过第1个过滤器IPFilter");
        ServerHttpRequest request = exchange.getRequest();
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        System.out.println("ip" + remoteAddress.getHostName());//ip 0:0:0:0:0:0:0:1

        // 获取访问的远程主机名称为
        String hostName = exchange.getRequest().getRemoteAddress().getHostName();

        // 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 4;
    }
}
