package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.VIDEO_POSTS;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.models.Objects.currentUser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loviagin.rollic.R;
import com.loviagin.rollic.adapters.VideoAdapter;
import com.loviagin.rollic.models.Video;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {

    private VideoAdapter videoAdapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    public static final String TAG = "Explore_Activity_TAG";
    private ImageButton buttonHome, buttonExplore, buttonStore, buttonAccount, buttonSearch;
    private FloatingActionButton buttonAdd;
    private boolean isData = false;

    private DocumentSnapshot lastVisibleDocument;
    private boolean isLoading = false;
    private String cUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        recyclerView = findViewById(R.id.rvVideoExplore);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        buttonHome = findViewById(R.id.bHome);
        buttonExplore = findViewById(R.id.bVideo);
        buttonStore = findViewById(R.id.bStore);
        buttonAccount = findViewById(R.id.bAccount);
        buttonSearch = findViewById(R.id.bSearch);
        buttonAdd = findViewById(R.id.bAdd);

        buttonHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        buttonAccount.setOnClickListener(v -> {
            if (currentUser == null) {
                startActivity(new Intent(this, AuthActivity.class));
            } else {
                startActivity(new Intent(this, AccountActivity.class));
            }
        });

        buttonExplore.setColorFilter(R.color.black);
        buttonExplore.setOnClickListener(v -> startActivity(new Intent(this, VideoActivity.class)));
        buttonSearch.setOnClickListener(view -> startActivity(new Intent(this, SearchActivity.class)));
        buttonStore.setOnClickListener(v -> Toast.makeText(this, getResources().getString(R.string.hello_blank_fragment), Toast.LENGTH_SHORT).show());
        buttonAdd.setColorFilter(R.color.white);
        buttonAdd.setOnClickListener(v -> startActivity(new Intent(this, AddActivity.class)));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    playCurrentVisibleVideo();
                } else {
                    pauseCurrentPlayingVideo();
                }
                if (layoutManager.findLastVisibleItemPosition() == 4 && !isLoading) {
                    loadVideos();
                }
            }
        });

        // Замените этот список URL-адресами ваших видео
        List<Video> videoUrls = new ArrayList<>();
        videoAdapter = new VideoAdapter(videoUrls, getSupportFragmentManager());
        recyclerView.setAdapter(videoAdapter);

        Intent intent = getIntent();
        Uri data = getIntent().getData();

        if (intent.hasExtra("video_uid") || data != null) {
            isData = true;
            if (data != null) {
                cUser = data.getQueryParameter("u");
            } else {
                cUser = intent.getStringExtra("video_uid");
            }
        }
        loadVideos();
//        thread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseCurrentPlayingVideo();
    }

    @Override
    public void onResume() {
        super.onResume();
        playCurrentVisibleVideo();
    }

    private void pauseCurrentPlayingVideo() {
        int position = layoutManager.findFirstVisibleItemPosition();
        VideoAdapter.VideoViewHolder viewHolder =
                (VideoAdapter.VideoViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            viewHolder.releasePlayer();
        }
    }

    private void playCurrentVisibleVideo() {
        int position = layoutManager.findFirstVisibleItemPosition();
        VideoAdapter.VideoViewHolder viewHolder =
                (VideoAdapter.VideoViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            viewHolder.initializePlayer();
        }
    }

    private void loadVideos() {
        if (isLoading) return;

        isLoading = true;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query videoQuery = db.collection(VIDEO_POSTS).limit(5);

        if (lastVisibleDocument != null) {
            videoQuery = videoQuery.startAfter(lastVisibleDocument);
        }

        videoQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                Video video = documentSnapshot.toObject(Video.class);
                if (video != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference videoRef = storage.getReference().child(video.getVideoUrl());
                    videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        video.setVideoUrl(String.valueOf(uri));
                        addVideoBasedOnCondition(video, video.getLikes().contains(uid), isData);
                    });
                }
            }
            if (isData) {
                db.collection("video-posts").document(cUser).get().addOnSuccessListener(documentSnapshot -> {
                    Video video = documentSnapshot.toObject(Video.class);
                    if (video != null) {
                        Log.e(TAG, "loaded");
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference videoRef = storage.getReference().child(video.getVideoUrl());
                        videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            video.setVideoUrl(String.valueOf(uri));
                            videoAdapter.addVideo(0, video);
                            recyclerView.smoothScrollToPosition(0);
                        });
                    }
                });
                isData = false;
            }
            int size = queryDocumentSnapshots.size();
            if (size > 0) {
                lastVisibleDocument = queryDocumentSnapshots.getDocuments().get(size - 1);
            }
            isLoading = false;
        });
    }


    public void addVideoBasedOnCondition(Video video, boolean liked, boolean hasVideoUID) {
        if (liked) {
            videoAdapter.addVideo(video);
        } else {
            videoAdapter.addVideo(hasVideoUID ? 1 : 0, video);
        }
    }

//    public void downloadFile(Context context, String url, final String fileName) {
//        File directory = context.getFilesDir(); // это будет приватной директорией вашего приложения
//
//        final File localFile = new File(directory, fileName);
//
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference httpsReference = storage.getReferenceFromUrl(url);
//
//        httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                // Успешно загружено
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Загрузка не удалась
//            }
//        });
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteFiles(getFilesDir());
    }

    private void deleteFiles(File directory) {
        if (directory != null) {
            for (File child : directory.listFiles()) {
                if (child.isDirectory()) {
                    deleteFiles(child);
                } else {
                    child.delete();
                }
            }
        }
    }
}