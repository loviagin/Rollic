package com.loviagin.rollic;

import static com.loviagin.rollic.models.Objects.currentUser;
import static com.loviagin.rollic.models.Objects.mAuth;
import static com.loviagin.rollic.models.Objects.preferences;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.loviagin.rollic.helpers.NotificationOpenedHandler;
import com.onesignal.OneSignal;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

public class App extends Application {

    private static final String ONESIGNAL_APP_ID = "0d1565d4-4727-4ae9-a689-3e5959a2c66c";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase Auth
        FirebaseApp.initializeApp(this);
        App.context = getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.setNotificationOpenedHandler(new NotificationOpenedHandler());

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
//        OneSignal.promptForPushNotifications();

        // Creating an extended library configuration.
        String API_key = "f21a04ac-e25b-45e5-8227-c5aff3ba1160";
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(API_key).build();
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this);

//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Query query = db.collection(POSTS_STR);
//        AggregateQuery countQuery = query.count();
//        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    // Count fetched successfully
//                    AggregateQuerySnapshot snapshot = task.getResult();
//                    Log.d("TAG", "Count: " + snapshot.getCount());
//                    postsCount = snapshot.getCount();
//                } else {
//                    Log.d("TAG", "Count failed: ", task.getException());
//                }
//            }
//        });
    }

    public static Context getContext() {
        return context;
    }
}
