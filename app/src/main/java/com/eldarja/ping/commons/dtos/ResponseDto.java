package com.eldarja.ping.commons.dtos;

import com.eldarja.ping.domains.login.dtos.AccountDto;

import java.io.Serializable;

// Generic response dto
public class ResponseDto implements Serializable {
    private AccountDto dto; // TODO: Make generic

    private String message;

    private String messageCode;

    public String getMessage() {
        return message;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
