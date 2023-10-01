package com.loviagin.rollic.activities.pro;

import static com.loviagin.rollic.Constants.USER_STR;
import static com.loviagin.rollic.UserData.paidSub;
import static com.loviagin.rollic.UserData.uid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loviagin.rollic.R;
import com.loviagin.rollic.activities.AccountActivity;
import com.loviagin.rollic.models.User;

public class UserPaidActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_paid);

        Intent intent = getIntent();
        if (intent.hasExtra("cud")) {
//            String name = intent.getStringExtra("user");
            String cud = intent.getStringExtra("cud");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid)
                    .update("paidSubscriptions", FieldValue.arrayUnion(cud)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            db.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    User user = documentSnapshot.toObject(User.class);
                                    if (user != null) {
                                        paidSub = user.getPaidSubscriptions();
                                    }
                                }
                            });
                            startActivity(new Intent(UserPaidActivity.this, AccountActivity.class)
                                    .putExtra(USER_STR, cud));
                        }
                    });
        }
    }
}