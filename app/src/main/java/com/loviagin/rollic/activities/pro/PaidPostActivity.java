package com.loviagin.rollic.activities.pro;

import static com.loviagin.rollic.Constants.POSITION;
import static com.loviagin.rollic.Constants.POSTS_STR;
import static com.loviagin.rollic.Constants.USER_UID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loviagin.rollic.R;
import com.loviagin.rollic.UserData;
import com.loviagin.rollic.activities.MainActivity;
import com.loviagin.rollic.adapters.GalleryPostAdapter;
import com.loviagin.rollic.models.Post;
import com.yandex.metrica.impl.ob.L;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PaidPostActivity extends AppCompatActivity {

    private TextView textViewAll, textViewNo;
    private RecyclerView recyclerView;
    private GalleryPostAdapter adapter;
    private List<Post> posts;
    private String cUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_post);

        recyclerView = findViewById(R.id.rvPaidPosts);
        textViewAll = findViewById(R.id.tvAllPostsPaid);
        textViewNo = findViewById(R.id.tvNoPostPaid);

        posts = new LinkedList<>();

        textViewAll.setOnClickListener(view -> finish());
        adapter = new GalleryPostAdapter((LinkedList<Post>) posts);
        adapter.setOnGalleryPostClickListener(position -> {
            UserData.dynPosts = posts;
            startActivity(new Intent(PaidPostActivity.this, MainActivity.class).putExtra(POSITION, position));
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent.hasExtra(USER_UID)) {
            cUser = intent.getStringExtra(USER_UID);
            if (!UserData.usrPosts.isEmpty()) {
                Log.e("TAG2455", "HERE");
                thread.start();
            } else {
                textViewNo.setVisibility(View.VISIBLE);
            }
        } else {
            textViewNo.setVisibility(View.VISIBLE);
        }
    }

    Thread thread = new Thread(() -> {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<String> uids = UserData.usrPosts.stream()
                .filter(Post::isPaid)
                .map(Post::getUid)
                .collect(Collectors.toList());
        if (!uids.isEmpty()) {
            Query query = db.collection(POSTS_STR)
                    .whereEqualTo("paid", true)
                    .whereIn(FieldPath.documentId(), uids);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot.getDocuments().size() > 0) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Post post = document.toObject(Post.class);
                            posts.add(post);
                            adapter.notifyItemInserted(posts.size() - 1);
                        }
                    }
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            });
        }
    });
}