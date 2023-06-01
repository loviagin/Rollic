package com.loviagin.rollic.models;

import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Objects {

    public static FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    public static SharedPreferences preferences;
    public static long postsCount;
}
