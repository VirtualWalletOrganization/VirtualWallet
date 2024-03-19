package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.dtos.MockBankDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class ExternalQuery {

    public static ResponseEntity<String> createExternalTransferRequestQuery(MockBankDto mockBankDto, String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.set("Authorization", "Bearer: JWT");
        HttpEntity<MockBankDto> entity = new HttpEntity<>(mockBankDto, headers);

        ResponseEntity<String> response;
        response = restTemplate.postForEntity(url, entity, String.class);
        return response;
    }
}