package com.micro;

import com.micro.config.UrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
@EnableConfigurationProperties(UrlConfig.class)
public class KarenApplication {

	public static void main(String[] args) {
		SpringApplication.run(KarenApplication.class, args);
	}

}
