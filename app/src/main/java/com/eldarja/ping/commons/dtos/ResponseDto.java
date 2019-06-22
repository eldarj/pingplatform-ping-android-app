package com.eldarja.ping.commons.dtos;

import com.eldarja.ping.domains.login.dtos.AccountDto;

import java.io.Serializable;

// GEneric response dto
public class ResponseDto implements Serializable {
    private AccountDto dto; // TODO: Make generic

    private String Message;

    private String MessageCode;
}
