package com.loviagin.rollic.workers;

import android.view.Window;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.loviagin.rollic.adapters.PostsAdapter;
import com.loviagin.rollic.models.Post;

import java.util.List;

public class PostUploader {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = "Post_Uploader_TAG";

    public PostUploader(PostsAdapter postsAdapter, List<Post> postList, RecyclerView recyclerView, Window window) {

    }
}
