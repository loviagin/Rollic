package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.AUTHOR_UID;
import static com.loviagin.rollic.Constants.POSTS_STR;
import static com.loviagin.rollic.Constants.SUBSCRIBERS_STR;
import static com.loviagin.rollic.Constants.SUBSCRIPTIONS_STR;
import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.Constants.USER_STR;
import static com.loviagin.rollic.Constants.USER_UID;
import static com.loviagin.rollic.UserData.bio;
import static com.loviagin.rollic.UserData.dynPosts;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.subscribers;
import static com.loviagin.rollic.UserData.subscriptions;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.urlAvatar;
import static com.loviagin.rollic.UserData.username;
import static com.loviagin.rollic.UserData.usrPosts;
import static com.loviagin.rollic.models.Objects.currentUser;
import static com.loviagin.rollic.models.Objects.mAuth;
import static com.loviagin.rollic.models.Objects.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loviagin.rollic.R;
import com.loviagin.rollic.UserData;
import com.loviagin.rollic.adapters.TabAccountAdapter;
import com.loviagin.rollic.models.Notification;
import com.loviagin.rollic.models.Post;
import com.loviagin.rollic.models.User;
import com.loviagin.rollic.models.Video;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class AccountActivity extends AppCompatActivity {

    public static final String TAG = "Account_Activity_TAG";
    private ImageButton buttonHome, buttonAccount, buttonBack, buttonSettings, buttonExplore, buttonStore;
    private Button buttonSubscribers, buttonSubscriptions, buttonPosts;
    private ImageView imageViewAvatar;
    private FloatingActionButton buttonAdd;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabAccountAdapter adapter;
    private TextView textViewUsername, textViewName, textViewBio, textButtonSubscribe;
    private List<String> listPosts;
    private List<String> listVideos;
    private List<String> listSubscriptions;
    private List<String> listSubscribers;
    private String usrName, usrNickname, usrUid, usrUrlAvatar;
    private ProgressBar progressBar;
    private Button buttonEditProfile;

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
        buttonExplore = findViewById(R.id.bDiscover);
        buttonStore = findViewById(R.id.bStore);
        tabLayout = findViewById(R.id.tlAccount);
        viewPager = findViewById(R.id.vpAccount);
        textButtonSubscribe = findViewById(R.id.tvSubscribe);
        buttonBack = findViewById(R.id.bNotifications);
        buttonSettings = findViewById(R.id.bMessage);
        imageViewAvatar = findViewById(R.id.ivAvatarAccount);
        progressBar = findViewById(R.id.pbAccount);
        buttonEditProfile = findViewById(R.id.bEditAccount);

        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        Intent intent = getIntent();
        Log.e("TA245G", intent.hasExtra(USER_STR) + "");
        if (intent.hasExtra(USER_STR) && !intent.getStringExtra(USER_STR).equals(uid)) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            textButtonSubscribe.setVisibility(View.VISIBLE);
            if (!subscriptions.contains(intent.getStringExtra(USER_STR))) {
                subscribe(intent);
            } else {
                unsubscribe(intent);
            }
            usrUid = intent.getStringExtra(USER_STR);
            buttonSettings.setVisibility(View.INVISIBLE);
            db.collection(USERS_COLLECTION).document(usrUid).get().addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                Log.e("TA245G", usrUid);
                listPosts = user.getPosts();
                listVideos = user.getVideoposts();
                listSubscribers = user.getSubscribers();
                listSubscriptions = user.getSubscriptions();
                if (user.getBio() != null && user.getBio().length() > 0) {
                    textViewBio.setVisibility(View.VISIBLE);
                    textViewBio.setText(user.getBio());
                }
                usrName = user.getF_name();
                usrUrlAvatar = user.getAvatarUrl();
                usrNickname = user.getUsername();
                listSubscriptions = user.getSubscriptions();
                listSubscribers = user.getSubscribers();
                setInfoTable();
            });
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            textButtonSubscribe.setVisibility(View.GONE);
            usrUid = uid;
            buttonEditProfile.setVisibility(View.VISIBLE);
            buttonEditProfile.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, EditProfileActivity.class)));
            buttonSettings.setImageDrawable(getResources().getDrawable(R.drawable.fi_rr_menu_dots));
            buttonSettings.setOnClickListener(this::showPopupMenu);
            db.collection(USERS_COLLECTION).document(uid).get().addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                listPosts = user.getPosts();
                listVideos = user.getVideoposts();
                listSubscribers = user.getSubscribers();
                listSubscriptions = user.getSubscriptions();
                if (user.getBio() != null && user.getBio().length() > 0) {
                    bio = user.getBio();
                    textViewBio.setVisibility(View.VISIBLE);
                    textViewBio.setText(user.getBio());
                }
                usrName = name;
                usrUrlAvatar = urlAvatar;
                usrNickname = username;
                listSubscriptions = subscriptions;
                listSubscribers = subscribers;
                buttonAccount.setColorFilter(R.color.black);
                setInfoTable();
            });
        }

        buttonHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        buttonAccount.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, AccountActivity.class)));
        buttonExplore.setOnClickListener(v -> startActivity(new Intent(this, ExploreActivity.class)));
        buttonStore.setOnClickListener(v -> Toast.makeText(this, getResources().getString(R.string.hello_blank_fragment), Toast.LENGTH_SHORT).show());
        buttonAdd.setColorFilter(R.color.white);
        buttonAdd.setOnClickListener(v -> startActivity(new Intent(this, AddActivity.class)));
        buttonBack.setImageDrawable(getResources().getDrawable(R.drawable.fi_rr_back));
        buttonBack.setOnClickListener(v -> finish());
    }

    private void setInfoTable() {
        LinkedList<Post> lp = new LinkedList<>();
        LinkedList<Video> vp = new LinkedList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(POSTS_STR).whereEqualTo(AUTHOR_UID, usrUid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Post p0 = document.toObject(Post.class);
                    lp.add(0, p0);
                    usrPosts.add(0, p0);
                }
                db.collection("video-posts").whereEqualTo(AUTHOR_UID, usrUid).get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task1.getResult()) {
                            Video v0 = document.toObject(Video.class);
                            vp.add(0, v0);
//                            usrPosts.add(0, v0);
                        }
                        db.collection(POSTS_STR).whereArrayContains("likes", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                LinkedList<Post> plist = new LinkedList<>();
                                for (QueryDocumentSnapshot document : task2.getResult()) {
                                    Post p2 = document.toObject(Post.class);
                                    plist.add(p2);
                                }
                                UserData.likPosts = plist;
                                dynPosts = lp;
                                UserData.dynVideos = vp;
                                adapter = new TabAccountAdapter(AccountActivity.this, lp, vp, plist);
                                viewPager.setAdapter(adapter);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        });
                    }
                });
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
            startActivity(new Intent(this, EditProfileActivity.class));
        }
        textViewUsername.setText(String.format(getResources().getString(R.string.nicknameofuser_str), usrNickname));
        buttonPosts.setText(String.format(getResources().getString(R.string.posts_str), (listPosts.size() + listVideos.size()) + ""));
        buttonSubscriptions.setText(String.format(getResources().getString(R.string.subscriptions_str), listSubscriptions.size() + ""));
        buttonSubscribers.setText(String.format(getResources().getString(R.string.subscribers_str), listSubscribers.size() + ""));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        if (usrUrlAvatar != null && !usrUrlAvatar.equals("")) {
            storageRef.child(usrUrlAvatar).getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).into(imageViewAvatar);
                progressBar.setVisibility(View.GONE);
            }).addOnFailureListener(exception -> {
                progressBar.setVisibility(View.GONE);
                // Handle any errors
            });
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void subscribe(Intent intent) {
        textButtonSubscribe.setText(getResources().getString(R.string.subscribe_str));
        textButtonSubscribe.setBackground(getResources().getDrawable(R.drawable.ground_button));
        textButtonSubscribe.setOnClickListener(v -> {
            subscriptions.add(intent.getStringExtra(USER_STR));
            FirebaseFirestore db0 = FirebaseFirestore.getInstance();
            sendNotification(intent);
            db0.collection(USERS_COLLECTION).document(uid).update(SUBSCRIPTIONS_STR, FieldValue.arrayUnion(intent.getStringExtra(USER_STR)));
            db0.collection(USERS_COLLECTION).document(intent.getStringExtra(USER_STR)).update(SUBSCRIBERS_STR, FieldValue.arrayUnion(uid));
            unsubscribe(intent);
        });
    }

    private void sendNotification(Intent intent) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USERS_COLLECTION).document(intent.getStringExtra(USER_STR)).get().addOnSuccessListener(documentSnapshot -> {
            List<String> devices = (List<String>) documentSnapshot.get("deviceTokens");
            if (devices != null && devices.size() > 0) {
                try {
                    for (String str : devices) {
                        JSONObject notificationContent = new JSONObject(
                                "{'contents': {'en':'Новый подписчик @" + username + "'}, " +
                                        "'include_player_ids': ['" + str + "'], " +
                                        "'data': {'activityToBeOpened': 'NotificationActivity'}}"
                        );
                        OneSignal.postNotification(notificationContent, null);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        db.collection("notifications").add(new
                        Notification("Новый подписчик @" + username, "", urlAvatar, "p/" + uid))
                .addOnSuccessListener(documentReference ->
                        db.collection(USERS_COLLECTION).document(intent.getStringExtra(USER_STR)).update("notifications", FieldValue.arrayUnion(documentReference.getId())));
    }

    private void unsubscribe(Intent intent) {
        textButtonSubscribe.setText(R.string.unsubscribe_str);
        textButtonSubscribe.setBackground(getResources().getDrawable(R.drawable.ground_box));
        textButtonSubscribe.setOnClickListener(v -> {
            subscriptions.remove(intent.getStringExtra(USER_STR));
            FirebaseFirestore db0 = FirebaseFirestore.getInstance();
            db0.collection(USERS_COLLECTION).document(uid).update(SUBSCRIPTIONS_STR, FieldValue.arrayRemove(intent.getStringExtra(USER_STR)));
            db0.collection(USERS_COLLECTION).document(intent.getStringExtra(USER_STR)).update(SUBSCRIBERS_STR, FieldValue.arrayRemove(uid));
            subscribe(intent);
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.setting_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.setting:
                    startActivity(new Intent(AccountActivity.this, SettingsActivity.class));
                    return true;
                case R.id.logout:
                    mAuth.signOut();
                    currentUser = null;
                    UserData.remove();
                    preferences.edit().remove(USER_UID).apply();
                    startActivity(new Intent(AccountActivity.this, MainActivity.class));
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }
}