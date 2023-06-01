package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.Constants.USER_UID;
import static com.loviagin.rollic.UserData.email;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.username;
import static com.loviagin.rollic.models.Objects.currentUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.loviagin.rollic.R;
import com.loviagin.rollic.UserData;
import com.loviagin.rollic.adapters.PostsAdapter;
import com.loviagin.rollic.models.Objects;
import com.loviagin.rollic.models.Post;
import com.loviagin.rollic.models.User;
import com.loviagin.rollic.workers.PostUploader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main_Activity_TAG";
    private ImageButton buttonHome, buttonAccount, buttonExplore;
    private FloatingActionButton buttonAdd;
    private PostsAdapter postsAdapter;
    private RecyclerView recyclerViewPosts;
    private ProgressBar progressBar;
    private TextView textViewEnd;
    private List<Post> postList;

    @Override
    protected void onStart() {
        super.onStart();

        progressBar = findViewById(R.id.pbMain);

        if (currentUser == null) {
            startActivity(new Intent(this, AuthActivity.class));
        } else if (uid == null) {
            Log.d(TAG, Objects.preferences.getString(USER_UID, ""));
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String u = Objects.preferences.getString(USER_UID, "");
            DocumentReference docRef = db.collection(USERS_COLLECTION).document(u);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                email = user.getEmail();
                name = user.getF_name();
                username = user.getUsername();
                uid = u;
                progressBar.setVisibility(View.GONE);
            });
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        buttonHome = findViewById(R.id.bHome);
        buttonAccount = findViewById(R.id.bAccount);
        buttonAdd = findViewById(R.id.bAdd);
        recyclerViewPosts = findViewById(R.id.rvPostsMain);
        buttonExplore = findViewById(R.id.bDiscover);
        textViewEnd = findViewById(R.id.tvEndMain);

        buttonHome.setColorFilter(R.color.black);
        buttonAccount.setOnClickListener(v -> {
            if (currentUser == null) {
                startActivity(new Intent(this, AuthActivity.class));
            } else {
                startActivity(new Intent(this, AccountActivity.class));
            }
        });
        buttonAdd.setColorFilter(R.color.white);
        buttonAdd.setOnClickListener(v -> startActivity(new Intent(this, AddActivity.class)));
        buttonExplore.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ExploreActivity.class)));

        postList = new LinkedList<>();
        postsAdapter = new PostsAdapter(postList);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        List<String> strings = new ArrayList<>();
//        strings.add("https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/images%2Ffe22a67a-f02f-4c6c-a8bc-70cd0ba23051?alt=media&token=0455a591-6e1d-4bf3-b579-5fcfbc9bd521");
//        db.collection("posts").add(new Post("", "Wow", "#tg #tag", "5AoYmKTHSsjzWyOpvgd", "Elian", "https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/avatars%2FfMClAWEqOybSPf8pFqYvc4OhSPu2cropped3876546716996985108.jpg?alt=media&token=149bcfcf-4e86-4460-bf99-a72e58c87baa",
//                "BraveDog435", strings, 0, 0, 0));
//        db.collection("posts").add(new Post("", "Wow2", "#tg #tag", "5AoYmKTHSsjzWyOpvgd", "Elian", "https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/avatars%2FfMClAWEqOybSPf8pFqYvc4OhSPu2cropped3876546716996985108.jpg?alt=media&token=149bcfcf-4e86-4460-bf99-a72e58c87baa",
//                "BraveDog435", strings, 0, 0, 0));
//        db.collection("posts").add(new Post("", "Wow3", "#tg #tag", "5AoYmKTHSsjzWyOpvgd", "Elian", "https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/avatars%2FfMClAWEqOybSPf8pFqYvc4OhSPu2cropped3876546716996985108.jpg?alt=media&token=149bcfcf-4e86-4460-bf99-a72e58c87baa",
//                "BraveDog435", strings, 0, 0, 0));
//        db.collection("posts").add(new Post("", "Wow4", "#tg #tag", "5AoYmKTHSsjzWyOpvgd", "Elian", "https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/avatars%2FfMClAWEqOybSPf8pFqYvc4OhSPu2cropped3876546716996985108.jpg?alt=media&token=149bcfcf-4e86-4460-bf99-a72e58c87baa",
//                "BraveDog435", strings, 0, 0, 0));
//        db.collection("posts").add(new Post("", "Wow5", "#tg #tag", "5AoYmKTHSsjzWyOpvgd", "Elian", "https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/avatars%2FfMClAWEqOybSPf8pFqYvc4OhSPu2cropped3876546716996985108.jpg?alt=media&token=149bcfcf-4e86-4460-bf99-a72e58c87baa",
//                "BraveDog435", strings, 0, 0, 0));
//        db.collection("posts").add(new Post("", "Wow6", "#tg #tag", "5AoYmKTHSsjzWyOpvgd", "Elian", "https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/avatars%2FfMClAWEqOybSPf8pFqYvc4OhSPu2cropped3876546716996985108.jpg?alt=media&token=149bcfcf-4e86-4460-bf99-a72e58c87baa",
//                "BraveDog435", strings, 0, 0, 0));
//        new PostUploader(postsAdapter, postList, recyclerViewPosts, getWindow());
        thread1.start();
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewPosts.setAdapter(postsAdapter);
        postsAdapter.setOnReachListener(new PostsAdapter.OnReachListener() {
            @Override
            public void onReachEnd() {
                textViewEnd.setVisibility(View.VISIBLE);
            }
        });
        synchronized (postsAdapter) {
            postsAdapter.notifyAll();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    Thread thread1 = new Thread(new Runnable() {
        @Override
        public void run() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("posts").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Post post = document.toObject(Post.class);
                                post.setUid(document.getId());
                                if (post.getLikes().contains(uid)) {
                                    postList.add(post);
                                    postsAdapter.notifyItemInserted(postsAdapter.getItemCount() - 1);
                                } else {
                                    postList.add(0, post);
                                    postsAdapter.notifyItemInserted(0);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    });
}