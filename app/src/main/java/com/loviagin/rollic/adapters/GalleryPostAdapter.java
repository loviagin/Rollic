package com.loviagin.rollic.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loviagin.rollic.R;
import com.loviagin.rollic.models.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GalleryPostAdapter extends RecyclerView.Adapter<GalleryPostAdapter.GalleryPostViewHolder> {

    private List<Post> listPosts;

    public GalleryPostAdapter(List<Post> listPosts) {
        this.listPosts = listPosts;
    }

    public GalleryPostAdapter() {
        listPosts = new ArrayList<>();
    }

    public void addPost(Post p0){
        listPosts.add(p0);
    }

    @NonNull
    @Override
    public GalleryPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_post_item, parent, false);
        return new GalleryPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryPostViewHolder holder, int position) {
        Post post = listPosts.get(position);
        Picasso.get().load(Uri.parse(post.getImagesUrls().get(0))).into(holder.imageView);
        Log.e("ADAAAAAAAAA", "red " + post.getImagesUrls().get(0));
    }

    @Override
    public int getItemCount() {
        return listPosts.size();
    }

    static class GalleryPostViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;

        public GalleryPostViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ivGalleryPost);
        }
    }
}
