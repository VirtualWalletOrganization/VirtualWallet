package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.models.dtos.MockBankDto;
import com.example.virtualwallet.models.enums.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/api/mock-bank")
public class MockBankController {

    private static int currentResponse = 0;

    @PostMapping
    public ResponseEntity<?> getTransferPermission(@RequestBody MockBankDto mockBankDto,
                                                  @RequestHeader String authorization) {
//        if (!authorization.contains("Bearer")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        int i = currentResponse == 5 ? currentResponse = 0 : currentResponse++;

        switch (currentResponse) {
            case 0:
            case 1:
            case 2:
                return new ResponseEntity<>(Status.COMPLETED, HttpStatus.OK);
            case 3:
                throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT);
            case 4:
                return new ResponseEntity<>(Status.REJECT, HttpStatus.OK);
            default:
                throw new ResponseStatusException(HttpStatus.ACCEPTED);
        }
    }

    @PostMapping("/check-status")
    public ResponseEntity<?> checkPreviousTransactionStatus(@RequestBody MockBankDto mockBankDto,
                                                            @RequestHeader String authorization) {
        return new ResponseEntity<>(Status.COMPLETED, HttpStatus.OK);
    }
}