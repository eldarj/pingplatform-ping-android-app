package com.eldarja.ping.domains.login.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CallingCodeDto implements Serializable {
    @SerializedName(value = "prefixCode")
    public String PrefixCode;

    @SerializedName(value = "countryName")
    public String CountryName;

    @SerializedName(value = "isoCode")
    public String IsoCode;

    @Override
    public String toString() {
        return "+" + PrefixCode + " " + CountryName + " (" + IsoCode + ")";
    }

    public static String[] toString(CallingCodeDto[] dtoCallingCodes) {
        String[] stringCallingCodes = new String[dtoCallingCodes.length];
        for (int i = 0; i < dtoCallingCodes.length; i++) {
            stringCallingCodes[i] = dtoCallingCodes[i].toString();
        }
        return stringCallingCodes;
    }
}
