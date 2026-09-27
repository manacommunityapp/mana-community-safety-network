package com.manacommunity.safety;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
    "com.manacommunity.safety",
    "com.manacommunity.common"
})
@EntityScan(basePackages = {
    "com.manacommunity.safety.domain.entities",
    "com.manacommunity.common"
})
@EnableJpaRepositories(basePackages = {
    "com.manacommunity.safety.repository",
    "com.manacommunity.common"
})
@EnableAsync
@EnableScheduling
public class SafetyNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafetyNetworkApplication.class, args);
    }
}
