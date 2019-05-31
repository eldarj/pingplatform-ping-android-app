package com.eldarja.ping.domains.login.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AuthRequestDto implements Serializable {
//    @SerializedName(value = "PhoneNumber", alternate = {"phoneNumber", "phonenumber"})
    @SerializedName(value = "phoneNumber")
    public String PhoneNumber;

    public AuthRequestDto(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }
}