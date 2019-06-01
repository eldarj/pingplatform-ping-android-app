package com.eldarja.ping.domains.login.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AuthRequestDto implements Serializable {
    //@SerializedName(value = "PhoneNumber", alternate = {"phoneNumber", "phonenumber"})
    @SerializedName(value = "phoneNumber")
    private String PhoneNumber;

    @SerializedName(value = "firstname")
    private String Firstname;

    @SerializedName(value = "lastname")
    private String Lastname;

    @SerializedName(value = "callingCode")
    private int CallingCode;

    public AuthRequestDto(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public AuthRequestDto(String phoneNumber, int callingCode, String firstname, String lastname) {
        this.PhoneNumber = phoneNumber;
        this.CallingCode = callingCode;
        this.Firstname = firstname;
        this.Lastname = lastname;
    }
}
