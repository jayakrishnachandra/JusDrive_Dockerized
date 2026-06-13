package com.jusdrive.api_gateway.filter;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.jusdrive.api_gateway.util.JwtUtils;

import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthFilter implements GatewayFilter, Ordered {

    @Autowired
    private JwtUtils jwtUtils;

    private static final List<String> EXCLUDED_URLS =  new  ArrayList<>(Arrays.asList(
        
                                            "/auth/customer/login", "/auth/customer/register",
                                                "/auth/owner/login", "/auth/owner/register",
                                                "/auth/customer/forgotPassword","/auth/customer/verifyResetCode",
                                                "/auth/owner/forgotPassword","/auth/owner/verifyResetCode"));
    


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
     {
        ServerHttpRequest request = exchange.getRequest();
    
        

        // Allow them without authentication
        if (EXCLUDED_URLS.stream().anyMatch(url -> request.getURI().getPath().contains(url))) 
        {
            return chain.filter(exchange);
        }

        // Check for Authorization header
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION))
        {
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");

        String userId = jwtUtils.extractUserId(token);

        if (userId == null) {
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
       
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
            .header("X-User-Id", userId)
            .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -1; 
    }

    //order definer the execution order or filters if multiple filters assinged to a serverwebexchange
}
