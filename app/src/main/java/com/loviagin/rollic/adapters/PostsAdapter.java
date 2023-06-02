package com.loviagin.rollic.adapters;

import static com.loviagin.rollic.UserData.subscriptions;
import static com.loviagin.rollic.UserData.uid;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loviagin.rollic.R;
import com.loviagin.rollic.UserData;
import com.loviagin.rollic.models.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    private List<Post> posts;
    private OnReachListener onReachListener;
    private OnPostClickListener onPostClickListener;

    public PostsAdapter(List<Post> posts) {
        this.posts = posts;
    }

    public interface OnReachListener {
        void onReachEnd();
    }

    public interface OnPostClickListener {
        void onClickAvatar(String usrUid);
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

    public void setOnPostClickListener(OnPostClickListener onPostClickListener) {
        this.onPostClickListener = onPostClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        if (position == posts.size() - 1 && onReachListener != null) {
            onReachListener.onReachEnd();
        }
        Post post = posts.get(position);
        holder.textViewNickname.setText("@" + post.getAuthorNickname());

        if (post.getAuthorName() == null || post.getAuthorName().equals("")) {
            holder.textViewName.setVisibility(View.GONE);
        } else {
            holder.textViewName.setVisibility(View.VISIBLE);
            holder.textViewName.setText(post.getAuthorName());
        }

        Picasso.get().load(Uri.parse(post.getAuthorAvatarUrl())).fit().centerInside().into(holder.imageViewAvatar);
        holder.textViewDescription.setText(post.getDescription());
        if (post.getTitle().equals("")) {
            holder.imageView1.setVisibility(View.VISIBLE);
            Picasso.get().load(Uri.parse(post.getImagesUrls().get(0))).into(holder.imageView1);
            holder.textViewTitle.setVisibility(View.GONE);
        } else {
            holder.textViewTitle.setText(post.getTitle());
            holder.textViewTitle.setVisibility(View.VISIBLE);
            holder.imageView1.setVisibility(View.GONE);
        }
        View.OnClickListener clnr = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPostClickListener != null) {
                    onPostClickListener.onClickAvatar(post.getUidAuthor());
                }
            }
        };
        holder.layout.setOnClickListener(clnr);
        holder.imageViewAvatar.setOnClickListener(clnr);
        if (subscriptions.contains(post.getUidAuthor()) || post.getUidAuthor().equals(uid)){
            holder.buttonSubscribe.setVisibility(View.GONE);
        } else {
            holder.buttonSubscribe.setVisibility(View.VISIBLE);
            holder.buttonSubscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subscriptions.add(post.getUidAuthor());
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(uid).update("subscriptions", FieldValue.arrayUnion(post.getUidAuthor()));
                    holder.buttonSubscribe.setVisibility(View.GONE);
                }
            });
        }
        counter(holder, post);
        holder.buttonLike.setOnClickListener(v -> {
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

    static class PostsViewHolder extends RecyclerView.ViewHolder {

        private ShapeableImageView imageViewAvatar;
        private TextView textViewName, textViewNickname, textViewDescription, textViewTitle;
        private ImageView imageView1;
        private Button buttonLike, buttonComment, buttonRepost;
        private ImageButton buttonDislike, buttonSubscribe;
        private LinearLayout layout;

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
            layout = itemView.findViewById(R.id.llPostMain);
            buttonSubscribe = itemView.findViewById(R.id.bSubscribePost);
        }
    }
}
