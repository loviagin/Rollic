package com.loviagin.rollic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loviagin.rollic.R;
import com.loviagin.rollic.models.Post;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

public class GalleryPostAdapter extends RecyclerView.Adapter<GalleryPostAdapter.GalleryPostViewHolder> {

    private LinkedList<Post> listPosts;
    public static OnGalleryPostClickListener onGalleryPostClickListener;

    public GalleryPostAdapter(LinkedList<Post> lp) {
        listPosts = lp;
    }

    public void addPost(Post p0) {
        listPosts.add(p0);
    }

    public interface OnGalleryPostClickListener{
        void onPostClick(int position);
    }

    public void setOnGalleryPostClickListener(OnGalleryPostClickListener onGalleryPostClickListener) {
        GalleryPostAdapter.onGalleryPostClickListener = onGalleryPostClickListener;
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
        if (post.getImagesUrls() == null) {
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(String.format("«%s»", post.getTitle().charAt(0)));
            holder.imageView.setVisibility(View.GONE);
        } else {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            if (post.getImagesUrls() != null && post.getImagesUrls().get(0) != null) {
                storageRef.child(post.getImagesUrls().get(0)).getDownloadUrl()
                        .addOnSuccessListener(uri -> Picasso.get().load(uri).into((holder.imageView)));
            }
        }
    }

    @Override
    public int getItemCount() {
        return listPosts.size();
    }

    static class GalleryPostViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;

        public GalleryPostViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ivGalleryPost);
            textView = itemView.findViewById(R.id.tvTitlePostGallery);

            View.OnClickListener listener = v -> {
                if (onGalleryPostClickListener != null){
                    onGalleryPostClickListener.onPostClick(getAdapterPosition());
                }
            };

            imageView.setOnClickListener(listener);
            textView.setOnClickListener(listener);
        }
    }
}
