package com.loviagin.rollic.workers;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LanguageUtils {
    public static void setAppLocale(Resources resources, String languageCode) {
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(languageCode));
        } else {
            config.locale = new Locale(languageCode);
        }
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}