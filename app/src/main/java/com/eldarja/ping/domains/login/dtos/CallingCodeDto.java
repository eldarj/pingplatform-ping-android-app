package com.eldarja.ping.domains.login.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CallingCodeDto implements Serializable {
    @SerializedName(value = "callingCountryCode")
    public String callingCountryCode;

    @SerializedName(value = "countryName")
    public String countryName;

    @SerializedName(value = "isoCode")
    public String isoCode;

    public String getCallingCountryCode() {
        return callingCountryCode;
    }

    @Override
    public String toString() {
        return "+" + callingCountryCode + " " + countryName + " (" + isoCode + ")";
    }

    public static String[] toString(CallingCodeDto[] dtoCallingCodes) {
        String[] stringCallingCodes = new String[dtoCallingCodes.length];
        for (int i = 0; i < dtoCallingCodes.length; i++) {
            stringCallingCodes[i] = dtoCallingCodes[i].toString();
        }
        return stringCallingCodes;
    }
}
