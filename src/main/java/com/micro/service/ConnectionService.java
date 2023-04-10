package com.micro.service;

import com.micro.dto.Client;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class ConnectionService {
    private final RestTemplate restTemplate;
    private final LoadBalancerClient loadBalancerClient;
    private static final Logger LOG = LogManager.getRootLogger();

    public <T> T getResponseFromMicro(String name, String url, Class<T> responseType) {
        LOG.info("======================== Connection service: GET " + name + url + responseType + " ========================");
        return restTemplate.getForEntity("http://" + name + url, responseType).getBody();
    }

    public String postRequestForMicro(String name, String url, HttpEntity<MultiValueMap<String, String>> request) {
        LOG.info("======================== Connection service: POST " + name + url + request + " ========================");
        return restTemplate.postForEntity("http://" + name + url, request, String.class).getBody();
    }

    @SneakyThrows
    public <T> T getResponseFromService(String name, String url, Class<T> responseType) {
        LOG.info("======================== Connection service: GET " + name + url + responseType + " ========================");
        return loadBalancerClient.execute(name, backendInstance -> {
            URI backendUrl = backendInstance.getUri().resolve(url);
            return restTemplate.getForEntity(backendUrl, responseType).getBody();
        });
    }

    @SneakyThrows
    public String postRequestForService(String name, String url, HttpEntity<Client> request) {
        LOG.info("======================== Connection service: POST " + name + url + request + " ========================");
        return loadBalancerClient.execute(name, backendInstance -> {
            URI backendUrl = backendInstance.getUri().resolve(url);
            String response = restTemplate.postForEntity(backendUrl, request, String.class).getBody();
            return response + " " + backendInstance.getInstanceId();
        });
    }
}