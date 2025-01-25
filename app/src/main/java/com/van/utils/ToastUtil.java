package com.van.utils;

import android.content.Context;
import android.widget.Toast;

public final class ToastUtil {
    private static Toast toast;

    public static void show(Context context, String msg) {
        if (null != toast) {
            toast.cancel();
            toast = null;
        }

        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
