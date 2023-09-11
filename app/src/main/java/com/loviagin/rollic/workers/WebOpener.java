package com.loviagin.rollic.workers;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class WebOpener {
    public static Intent openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Log.d("Web_Opener_Worker", "u1" + webpage);
        return new Intent(Intent.ACTION_VIEW, webpage);
    }
}
