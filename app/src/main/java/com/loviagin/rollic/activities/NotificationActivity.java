package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.uid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.loviagin.rollic.R;
import com.loviagin.rollic.adapters.NotificationAdapter;
import com.loviagin.rollic.models.Notification;

import java.util.Collections;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private ImageButton buttonBack;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<String> noti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        findViewById(R.id.bMessage).setVisibility(View.INVISIBLE);
        buttonBack = findViewById(R.id.bNotifications);
        recyclerView = findViewById(R.id.rvNotifications);
        adapter = new NotificationAdapter();

        buttonBack.setImageDrawable(getResources().getDrawable(R.drawable.fi_rr_back));
        buttonBack.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(USERS_COLLECTION).document(uid).get().addOnSuccessListener(documentSnapshot -> {
            noti = (List<String>) documentSnapshot.get("notifications");
            thread.start();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collectionRef = db.collection("notifications");

            Query query = collectionRef.whereIn(FieldPath.documentId(), noti);
            query.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                                // Сортировка документов по полю "time"
                                Collections.sort(documents, (doc1, doc2) -> {
                                    Long time1 = doc1.getLong("time");
                                    Long time2 = doc2.getLong("time");
                                    if (time1 != null && time2 != null) {
                                        return time2.compareTo(time1);
                                    }
                                    return 0;
                                });
                                for (DocumentSnapshot document : documents) {
                                    Notification notification = document.toObject(Notification.class);
                                    adapter.addNotification(notification);
                                    adapter.notifyItemInserted(adapter.getItemCount());

                                    document.getReference().update("read", true);
                                }
                            }
                        } else {
                            Log.d("Firestore", "Ошибка получения документов: " + task.getException());
                        }
                    });
        }
    });
}