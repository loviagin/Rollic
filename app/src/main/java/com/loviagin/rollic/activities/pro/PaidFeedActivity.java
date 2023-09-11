package com.loviagin.rollic.activities.pro;

import static com.loviagin.rollic.UserData.uid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loviagin.rollic.R;
import com.loviagin.rollic.activities.MainActivity;
import com.loviagin.rollic.adapters.GalleryPostAdapter;
import com.loviagin.rollic.adapters.PaidFeedAdapter;
import com.loviagin.rollic.models.PaidPost;
import com.loviagin.rollic.models.Post;
import com.loviagin.rollic.models.User;

import java.util.LinkedList;
import java.util.List;

public class PaidFeedActivity extends AppCompatActivity {

    private ImageButton buttonBack;
    private RecyclerView recyclerView;
    private PaidFeedAdapter adapter;
    private TextView textViewNo;
    private List<PaidPost> paidPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_feed);

        buttonBack = findViewById(R.id.ibBackPaidFeed);
        recyclerView = findViewById(R.id.rvPaidFeedPosts);
        textViewNo = findViewById(R.id.tvSubPaidNo);

        paidPosts = new LinkedList<>();
        adapter = new PaidFeedAdapter(paidPosts);

        buttonBack.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        List<String> ss = new LinkedList<>();
//        ss.add("2sluYNWx592Jmlbe9x17");
//        ss.add("49AQr7xKqZQPqIPxqzRx");
//        adapter.addPaidPost(new PaidPost("naming", ss));
//        adapter.addPaidPost(new PaidPost("na2ming", ss));
//        adapter.addPaidPost(new PaidPost("naming3", ss));
        recyclerView.setAdapter(adapter);
        thread.start();
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User u0 = documentSnapshot.toObject(User.class);
                    List<String> strings = u0.getPaidSubscriptions();
                    if (strings != null && !strings.isEmpty()) {
                        db.collection("users").whereIn(FieldPath.documentId(), strings).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        User user = document.toObject(User.class);
                                        adapter.addPaidPost(new PaidPost(user.getF_name(), user.getPosts()));
                                    }
                                }
                            }
                        });
                    } else {
                        textViewNo.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    });
}