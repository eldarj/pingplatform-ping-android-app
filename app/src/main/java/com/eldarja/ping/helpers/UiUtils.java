package com.eldarja.ping.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class UiUtils {
    public static <T> void fillSpinner(Activity activity, T[] data, Spinner el) {
        fillSpinner(activity, data, android.R.layout.simple_spinner_item, android.R.layout.simple_spinner_dropdown_item, el);
    }

    public static <T> void fillSpinner(Activity activity, T[] data, int layoutId, int layoutItemId, Spinner el) {
        ArrayAdapter<T> dataAdapter = new ArrayAdapter<T>(activity, layoutId, data);
        dataAdapter.setDropDownViewResource(layoutItemId);
        el.setAdapter(dataAdapter);
    }

    public static void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getApplicationWindowToken(),
                    0);
        }
    }
}
