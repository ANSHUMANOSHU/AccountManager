package com.example.accountmanager.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import java.util.Date;
import androidx.core.content.ContextCompat;

public class GeneralHelper {

    private static final String PHOTO_NAME_SPLITTER_IDENTIFIER = "_";

    public static boolean isGranted(Context context,String...permissions){
        boolean isGranted = true;
        for(String permission : permissions){
            isGranted = isGranted && (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context,permission));
        }
        return isGranted;
    }

    public static String getFormattedDateTime() {
        String[] date = new Date().toString().split(" ");
        date[0] = date[2] + PHOTO_NAME_SPLITTER_IDENTIFIER + date[1]
                + PHOTO_NAME_SPLITTER_IDENTIFIER + date[5]
                + PHOTO_NAME_SPLITTER_IDENTIFIER + date[3];
        return date[0];
    }

}
