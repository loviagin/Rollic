package com.loviagin.rollic;

import static com.loviagin.rollic.models.Objects.currentUser;
import static com.loviagin.rollic.models.Objects.mAuth;
import static com.loviagin.rollic.models.Objects.preferences;

import android.app.Application;
import android.preference.PreferenceManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase Auth
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

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
}
