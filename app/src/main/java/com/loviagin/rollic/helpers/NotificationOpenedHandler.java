package com.loviagin.rollic.helpers;

import android.content.Intent;

import com.loviagin.rollic.App;
import com.loviagin.rollic.activities.NotificationActivity;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class NotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {
    // This fires when a notification is opened by tapping on it.
    @Override
    public void notificationOpened(OSNotificationOpenedResult result) {
        OSNotification notification = result.getNotification();
        JSONObject data = notification.getAdditionalData();

        if (data != null) {
            String activityToBeOpened = data.optString("activityToBeOpened", null);

            if (activityToBeOpened != null && activityToBeOpened.equals("NotificationActivity")) {
                Intent intent = new Intent(App.getContext(), NotificationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                App.getContext().startActivity(intent);
            }
        }
    }
}