package com.eldarja.ping.helpers;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

public class UiUtils {
    public static <T> void fillSpinner(Activity activity, T[] data, int layoutId, int layoutItemId, Spinner el) {
        ArrayAdapter<T> dataAdapter = new ArrayAdapter<T>(activity, layoutId, data);
        dataAdapter.setDropDownViewResource(layoutItemId);
        el.setAdapter(dataAdapter);
    }
}
