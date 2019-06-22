package com.eldarja.ping.helpers.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.eldarja.ping.domains.login.dtos.AccountDto;
import com.eldarja.ping.domains.login.dtos.ContactDto;
import com.eldarja.ping.helpers.GsonHelper;
import com.eldarja.ping.helpers.WeakRefApp;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefSession {
    private static String SHARED_PREFS_STORAGE_NAME = "PingSharedPreferencesStore";
    private static String PREF_LOGGED_USER = "LoggedInUser";
    private static String PREF_CONTACTS = "LoggedInUserContacts";

    public static AccountDto getUser () {
        AccountDto userAccount;

        SharedPreferences prefs = WeakRefApp.getContext().getSharedPreferences(SHARED_PREFS_STORAGE_NAME, Context.MODE_PRIVATE);

        String jsonSerializedUser = prefs.getString(PREF_LOGGED_USER, "");

        if (jsonSerializedUser.isEmpty()) {
            return null;
        }

        userAccount = GsonHelper.build().fromJson(jsonSerializedUser, AccountDto.class);

        return userAccount;
    }

    public static void setUser(AccountDto userAccount) {
        String jsonSerializedUser = userAccount == null ? "" : GsonHelper.build().toJson(userAccount);

        SharedPreferences prefs = WeakRefApp.getContext().getSharedPreferences(SHARED_PREFS_STORAGE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(PREF_LOGGED_USER, jsonSerializedUser);
        editor.apply();

        List<ContactDto> contacts = userAccount.getContacts();
        setContacts(contacts.toArray(new ContactDto[0]));
    }

    public static ContactDto[] getContacts() {
        ContactDto[] contacts;

        SharedPreferences prefs = WeakRefApp.getContext().getSharedPreferences(SHARED_PREFS_STORAGE_NAME, Context.MODE_PRIVATE);

        String jsonSerializedContacts = prefs.getString(PREF_CONTACTS, "");

        if (jsonSerializedContacts.isEmpty()) {
            return null;
        }

        Type listType = new TypeToken<ArrayList<ContactDto>>(){}.getType();
        contacts = GsonHelper.build().fromJson(jsonSerializedContacts, listType);

        return contacts;
    }

    public static void setContacts(ContactDto[] contacts) {
        String jsonSerializedContacts = contacts == null ? "" : GsonHelper.build().toJson(contacts);

        SharedPreferences prefs = WeakRefApp.getContext().getSharedPreferences(SHARED_PREFS_STORAGE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(PREF_CONTACTS, jsonSerializedContacts);
        editor.apply();
    }

}
