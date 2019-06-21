package com.eldarja.ping.domains.login.dtos;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class ContactDto {
    @SerializedName(value = "contactName")
    private String contactName;

    @SerializedName(value = "contactPhoneNumber")
    private String contactPhoneNumber;

    @SerializedName(value = "callingCountryCode")
    private int callingCountryCode;

    @SerializedName(value = "messages")
    private List<MessageDto> messages;

    public ContactDto(String contactName, String contactPhoneNumber, int callingCountryCode) {
        this.contactName = contactName;
        this.contactPhoneNumber = contactPhoneNumber;
        this.callingCountryCode = callingCountryCode;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public int getCallingCountryCode() {
        return callingCountryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactDto that = (ContactDto) o;
        return contactPhoneNumber.equals(that.contactPhoneNumber) ||
                contactPhoneNumber.equals(that.contactPhoneNumber.substring(1)) ||
                that.contactPhoneNumber.equals(contactPhoneNumber.substring(1));
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactPhoneNumber, callingCountryCode);
    }
}
