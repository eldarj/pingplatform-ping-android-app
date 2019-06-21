package com.eldarja.ping.domains.login.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AuthRequestDto implements Serializable {
    //@SerializedName(value = "phoneNumber", alternate = {"phoneNumber", "phonenumber"})
    @SerializedName(value = "phoneNumber")
    private String phoneNumber;

    @SerializedName(value = "firstname")
    private String firstname;

    @SerializedName(value = "lastname")
    private String lastname;

    @SerializedName(value = "callingCountryCode")
    private int callingCountryCode;

    @SerializedName(value = "contacts")
    private List<ContactDto> Contacts;

    public AuthRequestDto() {
    }

    public AuthRequestDto(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AuthRequestDto(String phoneNumber, int callingCountryCode, String firstname, String lastname) {
        this.phoneNumber = phoneNumber;
        this.callingCountryCode = callingCountryCode;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setCallingCountryCode(int callingCountryCode) {
        this.callingCountryCode = callingCountryCode;
    }

    public void setContacts(List<ContactDto> contacts) {
        Contacts = contacts;
    }
}
