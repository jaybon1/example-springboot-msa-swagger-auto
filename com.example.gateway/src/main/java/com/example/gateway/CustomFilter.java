package com.example.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class CustomFilter extends AbstractGatewayFilterFactory<Object> {

    @Autowired
    private DiscoveryClient discoveryClient;

    private final Random random = new Random();

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            // 기존 로드밸런싱 된 라우팅 정보
            Route originalRoute = (Route) exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            // item 서비스에 대한 라우팅 정보가 아니면 기존 라우팅 정보를 그대로 사용
            if (originalRoute == null || !"item".equals(originalRoute.getId())) {
                return chain.filter(exchange);
            }
            // item 서비스에 대한 라우팅 정보가 있으면 item 서비스의 인스턴스 목록을 조회
            List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances("item");
            // item 서비스의 인스턴스가 2개가 아니면 기존 라우팅 정보를 그대로 사용
            if (serviceInstanceList.size() != 2) {
                return chain.filter(exchange);
            }
            // item 서비스의 인스턴스 목록을 URI 문자열로 변환하여 오름차순으로 정렬
            List<String> serviceUriStringList = serviceInstanceList.stream()
                    .map(thisServiceInstance -> thisServiceInstance.getUri().toString())
                    .sorted()
                    .toList();
            // 랜덤 객체를 사용해서 라우팅을 7 : 3 비율로 나누어 선택
            String selectedServiceUriString = serviceUriStringList.get(random.nextInt(10) < 7 ? 0 : 1);
            // 선택된 서비스 인스턴스로 새 라우트 객체 생성
            Route route = Route.async()
                    .id(originalRoute.getId())
                    .uri(selectedServiceUriString)
                    .order(originalRoute.getOrder())
                    .asyncPredicate(originalRoute.getPredicate())
                    .filters(originalRoute.getFilters())
                    .build();
            // 새 라우트 객체를 exchange 객체에 저장
            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR, route);
            return chain.filter(exchange);
        };
    }

}
