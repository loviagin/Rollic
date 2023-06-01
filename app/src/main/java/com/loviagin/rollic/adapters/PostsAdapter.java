package com.loviagin.rollic.adapters;

import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.UserData.uid;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loviagin.rollic.Constants;
import com.loviagin.rollic.R;
import com.loviagin.rollic.UserData;
import com.loviagin.rollic.models.Post;
import com.loviagin.rollic.workers.PostUploader;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    private List<Post> posts;
    private OnReachListener onReachListener;

    public PostsAdapter(List<Post> posts) {
        this.posts = posts;
    }

    public interface OnReachListener {
        void onReachEnd();
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostsViewHolder(view);
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        if (position == posts.size() - 1 && onReachListener != null) {
            onReachListener.onReachEnd();
        }
        Post post = posts.get(position);
        holder.textViewNickname.setText("@" + post.getAuthorNickname());

        if (post.getAuthorName() == null || post.getAuthorName().equals("")){
            holder.textViewName.setVisibility(View.GONE);
        } else {
            holder.textViewName.setVisibility(View.VISIBLE);
            holder.textViewName.setText(post.getAuthorName());
        }

        Picasso.get().load(Uri.parse(post.getAuthorAvatarUrl())).fit().centerInside().into(holder.imageViewAvatar);
        holder.textViewDescription.setText(post.getDescription());
        if (post.getTitle().equals("")){
            holder.imageView1.setVisibility(View.VISIBLE);
            Picasso.get().load(Uri.parse(post.getImagesUrls().get(0))).into(holder.imageView1);
            holder.textViewTitle.setVisibility(View.GONE);
        } else {
            holder.textViewTitle.setText(post.getTitle());
            holder.textViewTitle.setVisibility(View.VISIBLE);
            holder.imageView1.setVisibility(View.GONE);
        }
//        holder.buttonDislike.setOnClickListener(v -> {
//            posts.remove(post);
//            setPosts(posts);
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            db.collection(USERS_COLLECTION).document(uid).update("hiddenPosts", FieldValue.arrayUnion(post.getUid()));
//            synchronized (holder){
//                holder.notifyAll();
//            }
//            holder.imageView1.setVisibility(View.GONE);
//            holder.textViewTitle.setVisibility(View.GONE);
//            holder.textViewDescription.setText("ПОСТ СКРЫТ");
//            holder.buttonLike.setVisibility(View.GONE);
//            holder.buttonComment.setVisibility(View.GONE);
//            holder.buttonRepost.setVisibility(View.GONE);
//            holder.buttonDislike.setVisibility(View.GONE);
//            UserData.hiddenPosts.add(post.getUid());
//
//        });
        counter(holder, post);
        holder.buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.getLikes().contains(uid)) {
                    post.deleteLike(uid);
                    counter(holder, post);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("posts").document(post.getUid());
                    docRef.update("likes", FieldValue.arrayRemove(uid));
                } else {
                    post.addLike(uid);
                    counter(holder, post);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("posts").document(post.getUid());
                    docRef.update("likes", FieldValue.arrayUnion(uid));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setOnReachListener(OnReachListener onReachListener) {
        this.onReachListener = onReachListener;
    }

    private void counter(PostsViewHolder holder, Post post) {
        setColorToButton(holder, post);
        holder.buttonLike.setText(String.valueOf(post.getLikes().size()));
        holder.buttonComment.setText(String.valueOf(post.getCommentsCount()));
        holder.buttonRepost.setText(String.valueOf(post.getRepostCount()));
    }

    private void setColorToButton(PostsViewHolder holder, Post post) {
        if (post.getLikes().contains(uid)) {
            holder.buttonLike.setBackgroundColor(Color.parseColor("#fba6f5"));
        } else {
            holder.buttonLike.setBackgroundColor(Color.parseColor("#507178fd"));
        }
    }

    class PostsViewHolder extends RecyclerView.ViewHolder {

        private ShapeableImageView imageViewAvatar;
        private TextView textViewName, textViewNickname, textViewDescription, textViewTitle;
        private ImageView imageView1;
        private Button buttonLike, buttonComment, buttonRepost;
        private ImageButton buttonDislike;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewAvatar = itemView.findViewById(R.id.ivAvatarRegister);
            textViewName = itemView.findViewById(R.id.tvNamePost);
            textViewNickname = itemView.findViewById(R.id.tvNicknamePost);
            textViewTitle = itemView.findViewById(R.id.tvTitlePost);
            imageView1 = itemView.findViewById(R.id.ivPhoto1Post);
            buttonLike = itemView.findViewById(R.id.bLikePost);
            buttonComment = itemView.findViewById(R.id.bCommentPost);
            buttonRepost = itemView.findViewById(R.id.bRepost);
            buttonDislike = itemView.findViewById(R.id.bDislikePost);
            textViewDescription = itemView.findViewById(R.id.tvDescriptionPost);
        }
    }
}
