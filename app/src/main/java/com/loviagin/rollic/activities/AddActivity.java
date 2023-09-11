package com.loviagin.rollic.activities;

import static com.loviagin.rollic.UserData.isPaid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.loviagin.rollic.R;
import com.loviagin.rollic.adapters.AddPostTabAdapter;
import com.loviagin.rollic.fragments.PhotoAddFragment;
import com.loviagin.rollic.fragments.VideoAddFragment;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AddActivity extends AppCompatActivity implements AddPostTabAdapter.OnAddPostClickListener {

    private static final int REQUEST_CODE_SELECT_VIDEO = 56698;
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

    @Override
    public void onVideoClick() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        selectVideo();
    }

    private void selectVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CODE_SELECT_VIDEO);
    }

    private void openImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAllowFlipping(true)
                .setAllowRotation(true)
                .setAspectRatio(4, 3)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
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
                    Exception ignored = result.getError();
                    // Обработка ошибки обрезки изображения
                }
                break;
            case REQUEST_CODE_SELECT_VIDEO:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    Uri selectedVideoUri = data.getData();
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(this, selectedVideoUri);
                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long timeInMillisec = Long.parseLong(time);
                    try {
                        retriever.release();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    if (!isPaid && timeInMillisec > 15000) {
                        Toast.makeText(this, "Видео длиннее 15 секунд", Toast.LENGTH_SHORT).show();
                        //                                .setTitle("Video Duration")
//                                .setMessage("The video is longer than 15 seconds. Would you like to trim it?")
//                                .setPositiveButton("Yes", (dialog, which) -> {
//                                    // Call the method to trim video. Note: You need to implement this using a library such as FFmpeg.
//                                    trimVideo(selectedVideoUri, 15000);
//                                })
//                                .setNegativeButton("No", (dialog, which) -> {
//                                    dialog.dismiss();
//                                    // Do nothing.
//                                })
//                                .show();
                    } else if (isPaid && timeInMillisec > 180000) {
                        Toast.makeText(this, "Видео длиннее 3 минут", Toast.LENGTH_SHORT).show();
                    } else {
                        VideoAddFragment.setVideoView(selectedVideoUri);
                    }
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);//                        new AlertDialog.Builder(this)
                }
        }
    }
//    private void trimVideo(Uri sourceUri, int durationMs) {
//        MediaExtractor extractor = new MediaExtractor();
//        try {
//            extractor.setDataSource(this, sourceUri, null);
//            int trackIndex = -1;
//            for (int i = 0; i < extractor.getTrackCount(); i++) {
//                MediaFormat format = extractor.getTrackFormat(i);
//                String mime = format.getString(MediaFormat.KEY_MIME);
//                if (mime.startsWith("video/")) {
//                    trackIndex = i;
//                    break;
//                }
//            }
//
//            if (trackIndex >= 0) {
//                MediaMuxer muxer = null;
//                try {
//                    String outputFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/trimmed_video.mp4";
//                    muxer = new MediaMuxer(outputFileName, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//
//                    extractor.selectTrack(trackIndex);
//                    MediaFormat format = extractor.getTrackFormat(trackIndex);
//                    int muxerTrackIndex = muxer.addTrack(format);
//
//                    boolean sawEOS = false;
//                    long frameTime = 0;
//                    ByteBuffer buffer = ByteBuffer.allocate(256 * 1024);
//                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//
//                    muxer.start();
//                    while (!sawEOS) {
//                        bufferInfo.offset = 0;
//                        bufferInfo.size = extractor.readSampleData(buffer, 0);
//                        if (bufferInfo.size < 0) {
//                            sawEOS = true;
//                            bufferInfo.size = 0;
//                        } else {
//                            bufferInfo.presentationTimeUs = extractor.getSampleTime();
//                            if (bufferInfo.presentationTimeUs > durationMs * 1000) {
//                                sawEOS = true;
//                                bufferInfo.size = 0;
//                            } else {
//                                if ((extractor.getSampleFlags() & MediaExtractor.SAMPLE_FLAG_SYNC) != 0) {
//                                    bufferInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME;
//                                }
//                                muxer.writeSampleData(muxerTrackIndex, buffer, bufferInfo);
//                                extractor.advance();
//                            }
//                        }
//                    }
//                    muxer.stop();
//                    Uri trimmedUri = Uri.fromFile(new File(outputFileName));
//                    runOnUiThread(() -> {
//                        // Update the video view with the new trimmed video.
//                        VideoAddFragment.setVideoView(trimmedUri);
//                        progressBar.setVisibility(View.GONE);
//                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                    });
//                } catch (IOException e) {
//                    // Handle exception.
//                } finally {
//                    progressBar.setVisibility(View.GONE);
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                    if (muxer != null) {
//                        try {
//                            muxer.release();
//                        } catch (Exception e) {
//                            // Handle exception.
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            // Handle exception.
//        } finally {
//            extractor.release();
//        }
//    }
}