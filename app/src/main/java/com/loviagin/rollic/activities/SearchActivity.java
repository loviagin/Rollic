package com.loviagin.rollic.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loviagin.rollic.R;
import com.loviagin.rollic.adapters.TopUsersAdapter;
import com.loviagin.rollic.models.User;
import com.yandex.mobile.ads.banner.AdSize;
import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ImageButton buttonHome, buttonSearch, buttonVideo, buttonAccount, buttonMessages, buttonNotifications;
    private FloatingActionButton buttonAdd;
    private BannerAdView mAdView;
    private RecyclerView recyclerViewUsers;
    private SearchView searchView;

    private TopUsersAdapter topUsersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        buttonHome = findViewById(R.id.bHome);
        buttonSearch = findViewById(R.id.bSearch);
        buttonVideo = findViewById(R.id.bVideo);
        buttonAccount = findViewById(R.id.bAccount);
        buttonMessages = findViewById(R.id.bMessage);
        buttonNotifications = findViewById(R.id.bNotifications);
        buttonAdd = findViewById(R.id.bAdd);
        recyclerViewUsers = findViewById(R.id.rvTopUsers);
        searchView = findViewById(R.id.svSearch);

        searchView.setOnQueryTextFocusChangeListener((view1, view2) -> startActivity(new Intent(this, SearchingActivity.class)));

        buttonHome.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        buttonSearch.setOnClickListener(view -> startActivity(new Intent(this, SearchActivity.class)));
        buttonVideo.setOnClickListener(view -> startActivity(new Intent(this, VideoActivity.class)));
        buttonAccount.setOnClickListener(view -> startActivity(new Intent(this, AccountActivity.class)));
        buttonMessages.setOnClickListener(view -> startActivity(new Intent(this, ChatActivity.class)));
        buttonNotifications.setOnClickListener(view -> startActivity(new Intent(this, NotificationActivity.class)));
        buttonSearch.setColorFilter(R.color.black);
        buttonAdd.setColorFilter(R.color.white);
        buttonAdd.setOnClickListener(v -> startActivity(new Intent(this, AddActivity.class)));

        mAdView = (BannerAdView) findViewById(R.id.adViewMain);
        mAdView.setAdSize(AdSize.stickySize(this, 500));
        mAdView.setAdUnitId("R-M-2427151-2");

        mAdView.setBannerAdEventListener(new BannerAdEventListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
                mAdView.setVisibility(View.GONE);
                Log.e("ERROR", "ERROOOOOOOOOOOOOOOR");
            }

            @Override
            public void onAdClicked() {

            }

            @Override
            public void onLeftApplication() {

            }

            @Override
            public void onReturnedToApplication() {

            }

            @Override
            public void onImpression(@Nullable ImpressionData impressionData) {

            }
        });

        mAdView.loadAd(new AdRequest.Builder().build());

        List<User> userList = new ArrayList<>();
        userList.add(new User("loviagin", "Ilia", "https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/avatars%2FdJKR73600Bq8G4SqH0Zk1689922285574.jpg?alt=media&token=ae31af90-4d3f-4528-8f18-39173a215c2c", "loviagin.ilya@ya.ru"));
        userList.add(new User("BraveBee", "Май нэйм", "https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/avatars%2FI5dgjDRlyBKPeB8enKVv1685945211629.jpg?alt=media&token=1e30e67c-631b-4007-ab2b-5fbe8dfa4dee", "begemot@ya.com"));
        userList.add(new User("Setevik", "Евгений", "https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/avatars%2FAWCsbb8PEKgeC6GfBWpC1688526596102.jpg?alt=media&token=6965352f-b9b2-4e84-ac52-7742a0cc2d09", "eborovikov8@gmail.com"));

        topUsersAdapter = new TopUsersAdapter(userList);
        recyclerViewUsers.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewUsers.setAdapter(topUsersAdapter);
    }
}