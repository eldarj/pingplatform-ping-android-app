package com.eldarja.ping.helpers.contacts;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.eldarja.ping.domains.login.dtos.CallingCodeDto;
import com.eldarja.ping.domains.login.dtos.ContactDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactReader {

    private final static String EXIT_CODES_REGEX = "^(00|001|011|0011|007|008|009)?";

    public static List<ContactDto> GetDeviceContacts(ContentResolver contentResolverCompat, CallingCodeDto[] callingCodes) {
        String callingCountryCodesRegex = "^" + concatCallingCountryCodes(callingCodes, "|");
        HashSet<ContactDto> contactHashSet = new HashSet<>();

        Cursor phones = contentResolverCompat.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int callingCountryCode = 0;

            // strip empty spaces, parenthesis and exit codes (eg. 00, 001, 011, 0011, 007...)
            phoneNumber = phoneNumber.replaceAll("(^(0[01][1]{0,2}[789]?))|[+()\\s-]+", "");

            // check for a country prefix code and pick it
            Matcher matcher = Pattern.compile(callingCountryCodesRegex).matcher(phoneNumber);
            if (matcher.find()) {
                try {
                    callingCountryCode = Integer.parseInt(matcher.group(0)); // pick the matched prefix into prefix var
                } catch (Exception e) {
                    Log.e("Tagx", "Couldn't fetch prefix when should be fine.");
                }
                phoneNumber = matcher.replaceFirst(""); // remove the picked prefix from phone var
            }

            /*
            phoneNumber = phoneNumber.replaceFirst(callingCountryCodesRegex, "");
            for (int i = 0; i < callingCodes.length; i++) {
                String _prefix = callingCodes[i].getCallingCountryCode();

                if (phoneNumber.matches(EXIT_CODES_REGEX + _prefix + ".*")) {
                    phoneNumber = phoneNumber.replaceFirst(EXIT_CODES_REGEX + _prefix, "");
                    callingCountryCode = _prefix;
                    break;
                }
            }
            }*/

            Log.e("Tagx", "Contact: " + name + " (" + callingCountryCode + ") " + phoneNumber);
            contactHashSet.add(new ContactDto(name, phoneNumber, callingCountryCode));
        }
        phones.close();

        return new ArrayList<>(contactHashSet);
    }

    private static String concatCallingCountryCodes(CallingCodeDto[] callingCodes, String delimiter) {
        String regex = "";
        for(int i = 0; i < callingCodes.length - 1; i++) {
            regex += callingCodes[i].getCallingCountryCode() + delimiter;
        }
        regex += delimiter + callingCodes[callingCodes.length - 1].getCallingCountryCode();
        return regex;
    }
}
