package com.loviagin.rollic.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.loviagin.rollic.R;
import com.loviagin.rollic.adapters.AddPostTabAdapter;
import com.loviagin.rollic.fragments.PhotoAddFragment;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yandex.mobile.ads.banner.AdSize;
import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddActivity extends AppCompatActivity implements AddPostTabAdapter.OnAddPostClickListener {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AddPostTabAdapter adapter;
    private ProgressBar progressBar;

//    private BannerAdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        findViewById(R.id.bNotifications).setVisibility(View.GONE);
        findViewById(R.id.bMessage).setVisibility(View.GONE);
        tabLayout = findViewById(R.id.tlAdd);
        viewPager = findViewById(R.id.vpAdd);
        progressBar = findViewById(R.id.pbAdd);

        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

//        mAdView = (BannerAdView) findViewById(R.id.adView);
//        mAdView.setAdSize(AdSize.stickySize(this, 500));
//        mAdView.setAdUnitId("R-M-2427151-2");

//        mAdView.setBannerAdEventListener(new BannerAdEventListener() {
//            @Override
//            public void onAdLoaded() {
//                mAdView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
//                mAdView.setVisibility(View.GONE);
//                Log.e("ERROR", "ERROOOOOOOOOOOOOOOR");
//            }
//
//            @Override
//            public void onAdClicked() {
//
//            }
//
//            @Override
//            public void onLeftApplication() {
//
//            }
//
//            @Override
//            public void onReturnedToApplication() {
//
//            }
//
//            @Override
//            public void onImpression(@Nullable ImpressionData impressionData) {
//
//            }
//        });
//
//        mAdView.loadAd(new AdRequest.Builder().build());

        adapter = new AddPostTabAdapter(this);

        viewPager.setAdapter(adapter);
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

        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onImageClick() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        openImagePicker();
    }

    private void openImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAllowFlipping(true)
                .setAllowRotation(true)
                .setAspectRatio(4,3)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri croppedImageUri = result.getUri();
//                // Конвертирование в формат JPEG
                File outputFile = new File(croppedImageUri.getPath());
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeFile(outputFile.getAbsolutePath());
                    FileOutputStream outStream = new FileOutputStream(outputFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PhotoAddFragment.setImageView(bitmap);
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                // Обработка ошибки обрезки изображения
            }
        }
    }
}