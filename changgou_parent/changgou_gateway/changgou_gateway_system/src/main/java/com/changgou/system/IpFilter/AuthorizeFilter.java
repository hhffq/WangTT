package com.changgou.system.IpFilter;

import com.changgou.system.utils.JwtUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lht
 * @date 2025-07-30 21:02
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered  {


    private static final String AUTHORIZE_TOKEN = "token";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获得请求
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 获得响应
        ServerHttpResponse response = exchange.getResponse();


        if (path.contains("/admin/login")) {
            // 放行
            return chain.filter(exchange);
        }

        //获取请求头
        HttpHeaders headers = request.getHeaders();


        // 从请求头里面获取token
        String token = headers.getFirst(AUTHORIZE_TOKEN);

        if (StringUtils.isEmpty(token)) {
            // 如果请求头里面没有令牌，则返回401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);

            // 返回
            return response.setComplete();
        }
        try {
            JwtUtil.parseJWT(token);
        } catch (Exception e) {
           e.printStackTrace();
            // 说明令牌解析错误
            // 如果请求头里面没有令牌，则返回401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);

            // 返回
            return response.setComplete();
        }
        // 放行
        return chain.filter(exchange);

    }


    @Override
    public int getOrder() {
        return 2;
    }
}
