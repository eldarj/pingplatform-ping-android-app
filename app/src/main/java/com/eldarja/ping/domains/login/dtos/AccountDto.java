package com.eldarja.ping.domains.login.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AccountDto implements Serializable {
    private String phoneNumber;

    private String firstname;

    private String lastname;

    private int callingCountryCode;

    private String dateRegistered;

    private String avatarImageUrl;

    private String coverImageUrl;

    private List<ContactDto> contacts;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public List<ContactDto> getContacts() {
        return contacts;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
