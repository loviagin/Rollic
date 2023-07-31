package com.loviagin.rollic.activities;

import static com.loviagin.rollic.models.Objects.currentUser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loviagin.rollic.R;
import com.loviagin.rollic.adapters.VideoAdapter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {

    private VideoAdapter videoAdapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    public static final String TAG = "Explore_Activity_TAG";
    private ImageButton buttonHome, buttonExplore, buttonStore, buttonAccount;
    private FloatingActionButton buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        recyclerView = findViewById(R.id.rvVideoExplore);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        buttonHome = findViewById(R.id.bHome);
        buttonExplore = findViewById(R.id.bDiscover);
        buttonStore = findViewById(R.id.bStore);
        buttonAccount = findViewById(R.id.bAccount);
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
        buttonExplore.setOnClickListener(v -> startActivity(new Intent(this, ExploreActivity.class)));
        buttonStore.setOnClickListener(v -> Toast.makeText(this, getResources().getString(R.string.hello_blank_fragment), Toast.LENGTH_SHORT).show());
        buttonAdd.setColorFilter(R.color.white);
        buttonAdd.setOnClickListener(v -> startActivity(new Intent(this, AddActivity.class)));

        /**
         * It's temp
         */
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    playCurrentVisibleVideo();
//                } else {
//                    pauseCurrentPlayingVideo();
//                }
//            }
//        });
//
//        // Замените этот список URL-адресами ваших видео
//        List<String> videoUrls = Arrays.asList(
//                "https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/videos%2FdJKR73600Bq8G4SqH0Zk%2Fcontent%3A%2Fmedia%2Fexternal%2Fvideo%2Fmedia%2F1000001962?alt=media&token=054dcf03-13f9-4dc2-b8fa-a82598d92bda",
//                "https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/videos%2FdJKR73600Bq8G4SqH0Zk%2F1000002166?alt=media&token=ec9aeaee-c284-4a41-90cd-e3eaf4902f3a");
//        videoAdapter = new VideoAdapter(videoUrls);
//        recyclerView.setAdapter(videoAdapter);
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

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
//            FirebaseStorage storage = FirebaseStorage.getInstance();
//            StorageReference videoRef = storage.getReference().child(video.getVideoUrl());
//            videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
//
//            });
        }
    });

    public void downloadFile(Context context, String url, final String fileName) {
        File directory = context.getFilesDir(); // это будет приватной директорией вашего приложения

        final File localFile = new File(directory, fileName);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);

        httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Успешно загружено
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Загрузка не удалась
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteFiles(getFilesDir());
    }

    private void deleteFiles(File directory) {
        if(directory!=null) {
            for(File child : directory.listFiles()) {
                if(child.isDirectory()) {
                    deleteFiles(child);
                } else {
                    child.delete();
                }
            }
        }
    }
}