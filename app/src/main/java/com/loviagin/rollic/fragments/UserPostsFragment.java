package com.loviagin.rollic.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loviagin.rollic.R;
import com.loviagin.rollic.adapters.GalleryPostAdapter;
import com.loviagin.rollic.models.Post;

import java.util.LinkedList;
import java.util.List;

public class UserPostsFragment extends Fragment {

    private GalleryPostAdapter adapter;
    private LinkedList<Post> lp;

    public UserPostsFragment(LinkedList<Post> lp) {
        this.lp = lp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_posts, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rvUserPosts);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        adapter = new GalleryPostAdapter(lp);
        recyclerView.setAdapter(adapter);
        Log.e("ACCCCCCCCCCCCCCC", lp.toString());
        return view;
    }
}