package com.loviagin.rollic.fragments;

import static com.loviagin.rollic.Constants.POSITION;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loviagin.rollic.R;
import com.loviagin.rollic.activities.MainActivity;
import com.loviagin.rollic.adapters.GalleryPostAdapter;
import com.loviagin.rollic.models.Post;

import java.util.LinkedList;

public class LikedFragment extends Fragment {
    private GalleryPostAdapter adapter;
    private LinkedList<Post> plist;

    public LikedFragment(LinkedList<Post> plist) {
        this.plist = plist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_liked, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rvLikedPosts);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        adapter = new GalleryPostAdapter(plist);
//        adapter.setOnGalleryPostClickListener(position -> {});
        adapter.setOnGalleryPostClickListener(position -> startActivity(new Intent(getActivity(), MainActivity.class).putExtra(POSITION, position).putExtra("like", true)));
        recyclerView.setAdapter(adapter);
        return view;
    }
}