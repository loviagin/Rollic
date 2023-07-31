package com.loviagin.rollic.fragments;

import static com.loviagin.rollic.UserData.uid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.loviagin.rollic.R;
import com.loviagin.rollic.activities.AccountActivity;
import com.loviagin.rollic.activities.AddActivity;
import com.loviagin.rollic.activities.MainActivity;
import com.loviagin.rollic.adapters.AddPostTabAdapter;
import com.loviagin.rollic.workers.UploadVideoWorker;


public class VideoAddFragment extends Fragment {

    private static VideoView videoView;
    private EditText editTextDescription, editTextTags;
    private Button buttonCancel, buttonPublish;
    private ImageButton buttonAdd;
    private ProgressBar progressBar;

    private AddPostTabAdapter.OnAddPostClickListener onAddPostClickListener;
    private boolean isVideoClick = false;
    private static Uri urlVideo;

    public VideoAddFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onAddPostClickListener = (AddPostTabAdapter.OnAddPostClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnAddPostClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_add, container, false);
        videoView = view.findViewById(R.id.vvVideoAdd);
        buttonAdd = view.findViewById(R.id.ibUploadVideoAdd);
        editTextDescription = view.findViewById(R.id.etDescriptionVideoAdd);
        editTextTags = view.findViewById(R.id.etTagsVideoAdd);
        buttonCancel = view.findViewById(R.id.bCancelVideoAdd);
        buttonPublish = view.findViewById(R.id.bPublishVideoAdd);
        progressBar = view.findViewById(R.id.pbVideoAdd);

        View.OnClickListener listener = view12 -> {
            if (onAddPostClickListener != null) {
                onAddPostClickListener.onVideoClick();
                isVideoClick = true;
                buttonAdd.setVisibility(View.GONE);
            }
        };

        /**
         * It's temp
         */
//        buttonAdd.setOnClickListener(listener);
//        videoView.setOnClickListener(listener);

        buttonCancel.setOnClickListener(v -> startActivity(new Intent(getActivity(), MainActivity.class)));
        /**
         * It's temp
         */
//        buttonPublish.setOnClickListener(view1 -> {
//            if (isVideoClick) {
//                progressBar.setVisibility(View.VISIBLE);
//                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//
//                // Создайте Data объект, содержащий URL вашего видео и другие данные
//                Data videoData = new Data.Builder()
//                        .putString("video_uri", urlVideo.getLastPathSegment())
//                        .putString("uid", uid)  // uid должен быть объявлен и инициализирован
//                        .putString("description", editTextDescription.getText().toString())
//                        .putString("tags", editTextTags.getText().toString())
//                        .build();
//
//                // Создайте объект OneTimeWorkRequest для запуска вашего UploadVideoWorker
//                OneTimeWorkRequest uploadVideoRequest = new OneTimeWorkRequest.Builder(UploadVideoWorker.class)
//                        .setInputData(videoData)
//                        .build();
//
//                // Запустите вашего Worker'a
//                WorkManager.getInstance(getContext()).enqueue(uploadVideoRequest);
//
//                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                progressBar.setVisibility(View.GONE);
//                startActivity(new Intent(getActivity(), AccountActivity.class));
//            } else {
//                Toast.makeText(getActivity(), "Видео обязательно к добавлению", Toast.LENGTH_SHORT).show();
//            }
//        });

        return view;
    }

    public static void setVideoView(Uri videoView) {
        VideoAddFragment.videoView.setVideoURI(videoView);
        VideoAddFragment.urlVideo = videoView;
        VideoAddFragment.videoView.start();
    }
}