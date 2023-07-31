package com.loviagin.rollic.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loviagin.rollic.R;
import com.loviagin.rollic.adapters.GalleryVideoAdapter;
import com.loviagin.rollic.models.Video;

import java.util.LinkedList;

public class VideoFragment extends Fragment {

    private LinkedList<Video> videoList;
    private GalleryVideoAdapter adapter;
    public VideoFragment(LinkedList<Video> videoList) {
        this.videoList = videoList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_posts, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rvUserPosts);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        adapter = new GalleryVideoAdapter(videoList);
        adapter.setListener(new GalleryVideoAdapter.OnGalleryVideoClickListener() {
            @Override
            public void onVideoClick(int position) {

            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }
}