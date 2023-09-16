package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.AVATAR_URL;
import static com.loviagin.rollic.Constants.EDIT_POST;
import static com.loviagin.rollic.Constants.LANGUAGE;
import static com.loviagin.rollic.Constants.POSITION;
import static com.loviagin.rollic.Constants.POSTS_STR;
import static com.loviagin.rollic.Constants.POST_UID;
import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.Constants.USER_STR;
import static com.loviagin.rollic.Constants.USER_UID;
import static com.loviagin.rollic.UserData.dynPosts;
import static com.loviagin.rollic.UserData.email;
import static com.loviagin.rollic.UserData.isPaid;
import static com.loviagin.rollic.UserData.likPosts;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.paidSub;
import static com.loviagin.rollic.UserData.posts;
import static com.loviagin.rollic.UserData.self_messages;
import static com.loviagin.rollic.UserData.subscribers;
import static com.loviagin.rollic.UserData.subscriptions;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.urlAvatar;
import static com.loviagin.rollic.UserData.username;
import static com.loviagin.rollic.models.Objects.currentUser;
import static com.loviagin.rollic.models.Objects.preferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.loviagin.rollic.R;
import com.loviagin.rollic.UserData;
import com.loviagin.rollic.activities.pro.PaidFeedActivity;
import com.loviagin.rollic.adapters.PostsAdapter;
import com.loviagin.rollic.models.Objects;
import com.loviagin.rollic.models.Post;
import com.loviagin.rollic.models.User;
import com.loviagin.rollic.workers.LanguageUtils;
import com.onesignal.OneSignal;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main_Activity_TAG";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1022;
    private ImageButton buttonHome, buttonAccount, buttonExplore, buttonStore, buttonNotification, buttonSearch, buttonMessage;
    private FloatingActionButton buttonAdd;
    private PostsAdapter postsAdapter;
    private RecyclerView recyclerViewPosts;
    private ProgressBar progressBar;
    private TextView textViewEnd, textViewSubscriptions, textViewPaid;
    private List<Post> postList;
//    private int feed = 0; // 0 - all, 1 - sub, 2 - paid

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            startActivity(new Intent(this, AuthActivity.class));
        } else if (preferences.contains(LANGUAGE)) {
            String lang = preferences.getString(LANGUAGE, "en");
            LanguageUtils.setAppLocale(getResources(), lang);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.pbMain);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
        buttonNotification = findViewById(R.id.bNotifications);
        textViewSubscriptions = findViewById(R.id.tvSubscriptionsMain);
        textViewPaid = findViewById(R.id.tvPaidMain);
        buttonSearch = findViewById(R.id.bSearch);
        buttonMessage = findViewById(R.id.bMessage);

        /**
         * TEMP
         */
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Получение всех документов из коллекции POSTS
        db.collection(USERS_COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Проверка на наличие поля paid
                    if (!document.contains("paid")) {
                        // Обновление документа, добавление поля paid со значением false
                        db.collection(USERS_COLLECTION).document(document.getId())
                                .update("paid", false);
                    }
                }
            } else {
                System.out.println("Error getting documents: " + task.getException());
            }
        });

        textViewPaid.setOnClickListener(view -> startActivity(new Intent(this, PaidFeedActivity.class)));

        Intent intent = getIntent();
        showNotificationPermissionDialog();
        if (Intent.ACTION_VIEW.equals(intent.getAction())){
            Uri uri = intent.getData();
            if (uri != null) {
                Toast.makeText(this, ""+uri.getPath(), Toast.LENGTH_SHORT).show();
                // Обработка URI здесь
                // Например: uri.getAuthority(), uri.getPath(), uri.getQueryParameter("some_param")
            }
        }
        if (intent.hasExtra(POSITION)) {
            findViewById(R.id.llHeadMain).setVisibility(View.GONE);
            if (intent.hasExtra("like")) {
                recyclerViewPosts = findViewById(R.id.rvPostsMain);
                findViewById(R.id.bMessage).setVisibility(View.INVISIBLE);
                findViewById(R.id.inFMain).setVisibility(View.GONE);
                buttonNotification.setImageDrawable(getResources().getDrawable(R.drawable.fi_rr_back));
                buttonNotification.setOnClickListener(v -> finish());
                int pos = intent.getIntExtra(POSITION, 0);
                postList = likPosts;
                postsAdapter = new PostsAdapter(postList);
                recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                recyclerViewPosts.setAdapter(postsAdapter);
                Log.e(TAG, postList.get(0).toString());
                recyclerViewPosts.smoothScrollToPosition(pos);
                postsAdapter.setOnPostClickListener(new PostsAdapter.OnPostClickListener() {
                    @Override
                    public void onClickAvatar(String usrUid) {
                    }

                    @Override
                    public void onMenuClick(View v, String pstUid) {
                    }

                    @Override
                    public void onCommentClick(String pstUid, Uri uri) {
                        startActivity(new Intent(MainActivity.this, PostActivity.class).putExtra(POST_UID, pstUid).putExtra(AVATAR_URL, String.valueOf(uri)));
                    }
                });
            } else {
                recyclerViewPosts = findViewById(R.id.rvPostsMain);
                findViewById(R.id.bMessage).setVisibility(View.INVISIBLE);
                findViewById(R.id.inFMain).setVisibility(View.GONE);
                buttonNotification.setImageDrawable(getResources().getDrawable(R.drawable.fi_rr_back));
                buttonNotification.setOnClickListener(v -> finish());
                int pos = intent.getIntExtra(POSITION, 0);
                postList = dynPosts;
                postsAdapter = new PostsAdapter(postList);
                recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                recyclerViewPosts.setAdapter(postsAdapter);
                Log.e(TAG, postList.get(0).toString());
                recyclerViewPosts.smoothScrollToPosition(pos);
                postsAdapter.setOnPostClickListener(new PostsAdapter.OnPostClickListener() {
                    @Override
                    public void onClickAvatar(String usrUid) {
                    }

                    @Override
                    public void onMenuClick(View v, String pstUid) {
                        showPopupMenu(v, pstUid);
                    }

                    @Override
                    public void onCommentClick(String pstUid, Uri uri) {
                        startActivity(new Intent(MainActivity.this, PostActivity.class).putExtra(POST_UID, pstUid).putExtra(AVATAR_URL, String.valueOf(uri)));
                    }
                });
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.GONE);
        } else if (currentUser != null && (uid == null || subscriptions == null)) {
            findViewById(R.id.llHeadMain).setVisibility(View.VISIBLE);
            Log.d(TAG, Objects.preferences.getString(USER_UID, " - user loaded"));
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String u = Objects.preferences.getString(USER_UID, "");
            DocumentReference docRef = db.collection(USERS_COLLECTION).document(u);
            List<Map<String, String>> msgs = new LinkedList<>();
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                email = user.getEmail();
                name = user.getF_name();
                username = user.getUsername();
                if (!documentSnapshot.contains("paid")) {
                    docRef.update("paid", false);
                    isPaid = false;
                } else {
                    UserData.isPaid = user.isPaid();
                }
//                if (!documentSnapshot.contains("paidPosts")) {
//                    docRef.update("paidPosts", msgs);
//                } else {
//                    paidPosts = user.getPaidPosts();
//                }
                posts = user.getPosts();
                paidSub = user.getPaidSubscriptions();
                if (!documentSnapshot.contains("messages")) {
                    docRef.update("messages", msgs);
                    self_messages = new LinkedList<>();
                } else {
                    self_messages = user.getMessages();
                }
                subscriptions = user.getSubscriptions();
                subscribers = user.getSubscribers();
                urlAvatar = user.getAvatarUrl();
                uid = u;
                postLoading();
            });
            OneSignal.addSubscriptionObserver(stateChanges -> {
                String playerId = stateChanges.getTo().getUserId();
                if (playerId != null) {
                    db.collection(USERS_COLLECTION).document(uid).update("deviceTokens", FieldValue.arrayUnion(playerId));
                    preferences.edit().putString("player", playerId).apply();
                    Log.e(TAG, "PLAYER2 " + playerId);
                }
//            else {
//                db.collection(USERS_COLLECTION).document(uid).update("deviceTokens", FieldValue.arrayUnion(OneSignal.getDeviceState().getUserId()));
//            }
            });
            db.collection(USERS_COLLECTION).document(preferences.getString(USER_UID, "")).update("deviceTokens", FieldValue.arrayUnion(OneSignal.getDeviceState().getUserId()));
        } else {
            postLoading();
        }
    }

    private void showNotificationPermissionDialog() {
        if (!NotificationManagerCompat.from(MainActivity.this).areNotificationsEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Включите уведомления");
            builder.setMessage("Получайте уведомления о новых сообщениях");
            builder.setPositiveButton("Подключить", (dialog, which) -> requestNotificationPermission());
            builder.setNegativeButton("Отказаться", null);
            builder.show();
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void postLoading() {
        buttonHome = findViewById(R.id.bHome);
        buttonAccount = findViewById(R.id.bAccount);
        buttonAdd = findViewById(R.id.bAdd);
        recyclerViewPosts = findViewById(R.id.rvPostsMain);
        buttonExplore = findViewById(R.id.bVideo);
        buttonStore = findViewById(R.id.bStore);
        textViewEnd = findViewById(R.id.tvEndMain);

        buttonHome.setColorFilter(R.color.black);
        buttonHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        buttonAccount.setOnClickListener(v -> {
            if (currentUser == null) {
                startActivity(new Intent(this, AuthActivity.class));
            } else {
                startActivity(new Intent(this, AccountActivity.class));
            }
        });
        buttonAdd.setColorFilter(R.color.white);
        textViewSubscriptions.setOnClickListener(view -> Toast.makeText(this, R.string.in_dev_str, Toast.LENGTH_SHORT).show());
//        textViewPaid.setOnClickListener(view -> Toast.makeText(this, R.string.in_dev_str, Toast.LENGTH_SHORT).show());
        buttonAdd.setOnClickListener(v -> startActivity(new Intent(this, AddActivity.class)));
        buttonSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        buttonStore.setOnClickListener(v -> Toast.makeText(this, R.string.in_dev_str, Toast.LENGTH_SHORT).show());
        buttonNotification.setOnClickListener(view -> startActivity(new Intent(this, NotificationActivity.class)));
        buttonExplore.setOnClickListener(v -> startActivity(new Intent(this, VideoActivity.class)));
        buttonMessage.setOnClickListener(view -> startActivity(new Intent(this, ChatActivity.class)));

        postList = new LinkedList<>();
        postsAdapter = new PostsAdapter(postList);

        thread1.start();

        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewPosts.setAdapter(postsAdapter);
//        postsAdapter.setOnReachListener(() -> textViewEnd.setVisibility(View.VISIBLE));
        synchronized (postsAdapter) {
            postsAdapter.notifyAll();
        }

        postsAdapter.setOnPostClickListener(new PostsAdapter.OnPostClickListener() {
            @Override
            public void onClickAvatar(String usrUid) {
                startActivity(new Intent(MainActivity.this, AccountActivity.class).putExtra(USER_STR, usrUid));
            }

            @Override
            public void onMenuClick(View v, String pstUid) {
                showPopupMenu(v, pstUid);
            }

            @Override
            public void onCommentClick(String pstUid, Uri uri) {
                startActivity(new Intent(MainActivity.this, PostActivity.class).putExtra(POST_UID, pstUid).putExtra(AVATAR_URL, String.valueOf(uri)));
            }
        });

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.GONE);
    }

    Thread thread1 = new Thread(new Runnable() {
        @Override
        public void run() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(POSTS_STR).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Post post = document.toObject(Post.class);
                                post.setUid(document.getId());
                                if (!post.isPaid()) {
                                    if (post.getLikes().contains(uid)) {
                                        postList.add(post);
                                        postsAdapter.notifyItemInserted(postsAdapter.getItemCount() - 1);
                                    } else {
                                        postList.add(0, post);
                                        postsAdapter.notifyItemInserted(0);
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    });

    private void showPopupMenu(View view, String pstUid) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.item_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            switch (item.getItemId()) {
                case R.id.editPost:
                    startActivity(new Intent(MainActivity.this, EditPostActivity.class).putExtra(EDIT_POST, pstUid));
                    return true;
                case R.id.deletePost:
                    db.collection(POSTS_STR).document(pstUid).delete();
                    db.collection(USERS_COLLECTION).document(uid).update(POSTS_STR, FieldValue.arrayRemove(pstUid));
                    Toast.makeText(this, "Пост удален. Обновите вкладку, чтобы увидеть изменения", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }
}