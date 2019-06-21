package com.eldarja.ping.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {
    public static Gson build() { return builder().create(); }

    public static GsonBuilder builder() {
        GsonBuilder builder = new GsonBuilder();

        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return builder;
    }
}
