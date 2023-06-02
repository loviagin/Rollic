package com.loviagin.rollic.activities;

import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.posts;
import static com.loviagin.rollic.UserData.subscribers;
import static com.loviagin.rollic.UserData.subscriptions;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.username;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.loviagin.rollic.R;
import com.loviagin.rollic.adapters.TabAccountAdapter;
import com.loviagin.rollic.models.Post;
import com.loviagin.rollic.models.User;

import java.util.LinkedList;
import java.util.List;

public class AccountActivity extends AppCompatActivity {

    public static final String TAG = "Accont_Activity_TAG";
    private ImageButton buttonHome, buttonAccount;
    private Button buttonSubscribers, buttonSubscriptions, buttonPosts;
    private FloatingActionButton buttonAdd;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabAccountAdapter adapter;
    private TextView textViewUsername, textViewName, textViewBio, textButtonSubscribe;
    private List<String> listPosts;
    private List<String> listSubscriptions;
    private List<String> listSubscribers;
    private String usrName, usrNickname, usrUid;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        buttonHome = findViewById(R.id.bHome);
        buttonAccount = findViewById(R.id.bAccount);
        buttonAdd = findViewById(R.id.bAdd);
        textViewUsername = findViewById(R.id.tvUsernameAccount);
        textViewName = findViewById(R.id.tvNameAccount);
        buttonSubscriptions = findViewById(R.id.bSubscriptionsAccount);
        buttonSubscribers = findViewById(R.id.bSubscribersAccount);
        buttonPosts = findViewById(R.id.bPostsAccount);
        textViewBio = findViewById(R.id.tvBioAccount);
        tabLayout = findViewById(R.id.tlAccount);
        viewPager = findViewById(R.id.vpAccount);
        textButtonSubscribe = findViewById(R.id.tvSubscribe);
        progressBar = findViewById(R.id.pbAccount);

        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        Intent intent = getIntent();
        Log.e("Account_Activity_TAG", intent.hasExtra("user") + "");
        if (intent.hasExtra("user") && !intent.getStringExtra("user").equals(uid)) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            textButtonSubscribe.setVisibility(View.VISIBLE);
            if (!subscriptions.contains(intent.getStringExtra("user"))){
                subscribe(intent);
            }else {
                unsubscribe(intent);
            }
            usrUid = intent.getStringExtra("user");
            db.collection("users").document(intent.getStringExtra("user")).get().addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                listPosts = user.getPosts();
                listSubscribers = user.getSubscribers();
                listSubscriptions = user.getSubscriptions();
                textViewBio.setText(user.getBio());
                usrName = user.getF_name();
                usrNickname = user.getUsername();
                listSubscriptions = user.getSubscriptions();
                listSubscribers = user.getSubscribers();
                setInfoTable();
            });
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            textButtonSubscribe.setVisibility(View.GONE);
            usrUid = uid;
            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                listPosts = posts;
                listSubscribers = user.getSubscribers();
                listSubscriptions = user.getSubscriptions();
                textViewBio.setText(user.getBio());
                usrName = name;
                usrNickname = username;
                listSubscriptions = subscriptions;
                listSubscribers = subscribers;
                buttonAccount.setColorFilter(R.color.black);
                setInfoTable();
            });
        }

        buttonHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        buttonAccount.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, AccountActivity.class)));
        buttonAdd.setColorFilter(R.color.white);
        buttonAdd.setOnClickListener(v -> startActivity(new Intent(this, AddActivity.class)));
    }

    private void setInfoTable() {
        LinkedList<Post> lp = new LinkedList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").whereEqualTo("uidAuthor", usrUid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Post p0 = document.toObject(Post.class);
                        lp.add(p0);
                    }
                    adapter = new TabAccountAdapter(this, lp);
                    viewPager.setAdapter(adapter);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
        if (usrName != null) {
            if (!usrName.equals("")) {
                textViewName.setText(usrName);
            }
        } else {
            textViewName.setOnClickListener(v -> Toast.makeText(this, "WOW", Toast.LENGTH_SHORT).show());
        }
        textViewUsername.setText("@" + usrNickname);
        buttonPosts.setText(String.format(getResources().getString(R.string.posts_str), listPosts.size() + ""));
        buttonSubscriptions.setText(String.format(getResources().getString(R.string.subscriptions_str), listSubscriptions.size() + ""));
        buttonSubscribers.setText(String.format(getResources().getString(R.string.subscribers_str), listSubscribers.size() + ""));

        progressBar.setVisibility(View.GONE);
    }

    private void subscribe(Intent intent){
        textButtonSubscribe.setText("Подписаться");
        textButtonSubscribe.setBackgroundColor(getResources().getColor(R.color.blue));
        textButtonSubscribe.setOnClickListener(v -> {
            subscriptions.add(intent.getStringExtra("user"));
            FirebaseFirestore db0 = FirebaseFirestore.getInstance();
            db0.collection("users").document(uid).update("subscriptions", FieldValue.arrayUnion(intent.getStringExtra("user")));
            unsubscribe(intent);
        });
    }

    private void unsubscribe(Intent intent){
        textButtonSubscribe.setText("Отписаться");
        textButtonSubscribe.setBackgroundColor(getResources().getColor(R.color.blue50));
        textButtonSubscribe.setOnClickListener(v -> {
            subscriptions.remove(intent.getStringExtra("user"));
            FirebaseFirestore db0 = FirebaseFirestore.getInstance();
            db0.collection("users").document(uid).update("subscriptions", FieldValue.arrayRemove(intent.getStringExtra("user")));
            subscribe(intent);
        });
    }
}