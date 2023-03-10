package com.leo.infra.feign;

import com.leo.infra.feign.client.Default;
import feign.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Value("${dev.proxy.host:}")
    private String proxyHost;
    
    @Value("${dev.proxy.port:}")
    private Integer proxyPort;
    
    @Bean
    public Client feignClient(LoadBalancerClient cachingFactory, LoadBalancerClientFactory clientFactory) {
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
        return new FeignBlockingLoadBalancerClient(new Client.Proxied(null, null, proxy), cachingFactory, clientFactory);
    }
}
