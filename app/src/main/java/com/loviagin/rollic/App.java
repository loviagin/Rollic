package com.loviagin.rollic;

import static com.loviagin.rollic.models.Objects.currentUser;
import static com.loviagin.rollic.models.Objects.mAuth;
import static com.loviagin.rollic.models.Objects.postsCount;
import static com.loviagin.rollic.models.Objects.preferences;

import android.app.Application;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.loviagin.rollic.models.Objects;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("posts");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    Log.d("TAG", "Count: " + snapshot.getCount());
                    postsCount = snapshot.getCount();
                } else {
                    Log.d("TAG", "Count failed: ", task.getException());
                }
            }
        });
    }
}
