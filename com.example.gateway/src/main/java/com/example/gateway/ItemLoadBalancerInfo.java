package com.example.gateway;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.util.Comparator;
import java.util.List;

public class ItemLoadBalancerInfo {

    @Bean
    @Scope("prototype")
    public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
            DiscoveryClient discoveryClient,
            Environment environment,
            ConfigurableApplicationContext context
    ) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        List<ServiceInstance> sortedServiceInstanceList = discoveryClient.getInstances(name).stream()
                .sorted(Comparator.comparing(thisServiceInstance -> thisServiceInstance.getUri().toString()))
                .toList();
        return ServiceInstanceListSupplier.builder()
                .withDiscoveryClient()
                .withCaching()
                .withWeighted(instance -> {
                    if (!"item".equals(name)) {
                        return 1;
                    }
                    if (sortedServiceInstanceList.size() != 2) {
                        return 1;
                    }
                    if (instance.getUri().toString().equals(sortedServiceInstanceList.get(0).getUri().toString())) {
                        return 7;
                    }
                    if (instance.getUri().toString().equals(sortedServiceInstanceList.get(1).getUri().toString())) {
                        return 3;
                    }
                    return 1;
                })
                .build(context);
    }

//    @Bean
//    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
//            Environment environment,
//            LoadBalancerClientFactory loadBalancerClientFactory) {
//        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
//        return new CustomLoadBalancer(
//                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
//                name
//        );
//    }


//    @Bean
//    public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
//            DiscoveryClient discoveryClient,
//            ConfigurableApplicationContext context
//    ) {
//        // DiscoveryClient에서 item 서비스의 인스턴스 목록을 가져온 후 포트 번호를 기준으로 정렬
//        List<ServiceInstance> sortedServiceInstanceList = discoveryClient.getInstances("item").stream()
//                .sorted(Comparator.comparing(ServiceInstance::getPort))
//                .toList();
//        // withWeighted 메서드를 사용하여 7 : 3 가중치를 부여
//        return ServiceInstanceListSupplier.builder()
//                .withDiscoveryClient()
//                .withHints()
//                .withWeighted(instance -> {
//                    if (instance.getPort() == sortedServiceInstanceList.get(0).getPort()) {
//                        return 7;
//                    }
//                    if (instance.getPort() == sortedServiceInstanceList.get(1).getPort()) {
//                        return 3;
//                    }
//                    return 1;
//                })
//                .withCaching()
//                .build(context);
//    }


//    @Bean
//    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
//            LoadBalancerClientFactory loadBalancerClientFactory) {
//        return new CustomLoadBalancer(
//                loadBalancerClientFactory.getLazyProvider("item", ServiceInstanceListSupplier.class),
//                "item"
//        );
//    }

}
