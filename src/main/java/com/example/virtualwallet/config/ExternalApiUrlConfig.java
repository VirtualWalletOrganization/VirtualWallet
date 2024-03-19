package com.example.virtualwallet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:application.properties")
public class ExternalApiUrlConfig {

    private final String mockBankUrl;
    private final String mockBankStatusCheckUrl;
    private final String bankMainAcc;
    private final String bankUserId;

    public ExternalApiUrlConfig(Environment environment) {
        this.mockBankUrl = environment.getProperty("intermediary-bank-service.url");
        this.mockBankStatusCheckUrl = environment.getProperty("intermediary-bank-service.status-check-url");
        this.bankMainAcc = environment.getProperty("bank-main-acc");
        this.bankUserId = environment.getProperty("bank-user-acc-id");
    }

    public String getMockBankUrl() {
        return mockBankUrl;
    }

    public String getMockBankStatusCheckUrl() {
        return mockBankStatusCheckUrl;
    }

    public String getBankMainAcc() {
        return bankMainAcc;
    }

    public String getBankUserId() {
        return bankUserId;
    }
}