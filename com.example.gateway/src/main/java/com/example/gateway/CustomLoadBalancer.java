package com.example.gateway;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.util.function.SingletonSupplier;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CustomLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private final String serviceId;
    private final SingletonSupplier<ServiceInstanceListSupplier> serviceInstanceListSingletonSupplier;
    private final Random random = new Random();

    public CustomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        this.serviceInstanceListSingletonSupplier = SingletonSupplier
                .of(() -> serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new));
        this.serviceId = serviceId;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSingletonSupplier.obtain();
        return supplier.get(request)
                .next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances));
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
                                                              List<ServiceInstance> serviceInstances) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            return new EmptyResponse();
        }
        if (!"item".equals(serviceId)) {
            return new DefaultResponse(instances.get(random.nextInt(instances.size())));
        }
        if (instances.size() != 2) {
            return new DefaultResponse(instances.get(random.nextInt(instances.size())));
        }
        List<ServiceInstance> sortedItemServiceInstanceList = instances.stream()
                .sorted(Comparator.comparing(thisServiceInstance -> thisServiceInstance.getUri().toString()))
                .toList();
        return random.nextInt(10) < 7 ?
                new DefaultResponse(sortedItemServiceInstanceList.get(0)) :
                new DefaultResponse(sortedItemServiceInstanceList.get(1));
    }
}

