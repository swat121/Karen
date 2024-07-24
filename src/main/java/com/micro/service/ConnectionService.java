package com.micro.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class ConnectionService {
    private final RestTemplate restTemplate;
    private final LoadBalancerClient loadBalancerClient;

    @SneakyThrows
    public <T> T getResponseFromMqtt(String mqttHost, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MjE4NTQ0NDEyMDUsImlzcyI6IkVNUVgifQ.ARiWh09YQMCIjPAdYZfglMPf8cK45ascr15eZTp1NpA");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(mqttHost, HttpMethod.GET, entity, responseType).getBody();
    }

    public <T> T requestForBoard(String url, HttpMethod method, @Nullable HttpEntity<MultiValueMap<String, String>> requestEntity, Class<T> responseType) {
        return restTemplate.exchange(url, method, requestEntity, responseType).getBody();
    }

    @SneakyThrows
    public <T> T getResponseFromService(String name, String url, Class<T> responseType) {
        return loadBalancerClient.execute(name, backendInstance -> {
            URI backendUrl = backendInstance.getUri().resolve(url);
            return restTemplate.getForEntity(backendUrl, responseType).getBody();
        });
    }

    @SneakyThrows
    public <T> String postRequestForService(String name, String url, HttpEntity<T> request) {
        return loadBalancerClient.execute(name, backendInstance -> {
            URI backendUrl = backendInstance.getUri().resolve(url);
            return restTemplate.postForEntity(backendUrl, request, String.class).getBody();
        });
    }

    @SneakyThrows
    public <T> void putRequestForService(String name, String url, HttpEntity<T> request) {
        loadBalancerClient.execute(name, backendInstance -> {
            URI backendUrl = backendInstance.getUri().resolve(url);
            restTemplate.put(String.valueOf(backendUrl), request, String.class);
            return backendInstance.getInstanceId();
        });
    }
}
