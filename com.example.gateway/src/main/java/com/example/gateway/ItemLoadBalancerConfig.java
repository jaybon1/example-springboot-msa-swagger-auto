package com.example.gateway;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@LoadBalancerClients(value = {
        @LoadBalancerClient(name = "item", configuration = ItemLoadBalancerInfo.class),
//        @LoadBalancerClient(name = "user", configuration = ItemLoadBalancerInfo.class)
})
public class ItemLoadBalancerConfig {
}
