package com.eldarja.ping.login.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

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
