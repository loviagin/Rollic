package com.loviagin.rollic.adapters;

import static com.loviagin.rollic.Constants.LIKES_STR;
import static com.loviagin.rollic.Constants.POSTS_STR;
import static com.loviagin.rollic.Constants.SUBSCRIBERS_STR;
import static com.loviagin.rollic.Constants.SUBSCRIPTIONS_STR;
import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.UserData.subscriptions;
import static com.loviagin.rollic.UserData.uid;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loviagin.rollic.R;
import com.loviagin.rollic.models.Post;
import com.squareup.picasso.Picasso;
import com.yandex.mobile.ads.banner.AdSize;
import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    private List<Post> posts;
    private OnReachListener onReachListener;
    private OnPostClickListener onPostClickListener;

    private int count = 1;

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
        View view;
        if (count % 5 == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        }
        count++;
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
        if ((count - 1) % 5 != 0) {
            if (position == posts.size() - 1 && onReachListener != null) {
                onReachListener.onReachEnd();
            }
            Post post = posts.get(position);
            holder.textViewNickname.setText(String.format("@%s", post.getAuthorNickname()));

            if (post.getAuthorName() == null || post.getAuthorName().equals("")) {
                holder.textViewName.setVisibility(View.GONE);
            } else {
                holder.textViewName.setVisibility(View.VISIBLE);
                holder.textViewName.setText(post.getAuthorName());
            }

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            if (post.getAuthorAvatarUrl() != null && !post.getAuthorAvatarUrl().equals("")) {
                storageRef.child(post.getAuthorAvatarUrl()).getDownloadUrl()
                        .addOnSuccessListener(uri -> Picasso.get().load(uri).into((holder.imageViewAvatar)));
            }

            holder.textViewDescription.setText(post.getDescription());
            if (post.getTitle().equals("")) {
                holder.imageView1.setVisibility(View.VISIBLE);
                if (post.getImagesUrls() != null && post.getImagesUrls().get(0) != null) {
                    storageRef.child(post.getImagesUrls().get(0)).getDownloadUrl()
                            .addOnSuccessListener(uri -> Picasso.get().load(uri).into((holder.imageView1)));
                }
                holder.textViewTitle.setVisibility(View.GONE);
            } else {
                holder.textViewTitle.setText(post.getTitle());
                holder.textViewTitle.setVisibility(View.VISIBLE);
                holder.imageView1.setVisibility(View.GONE);
            }
            View.OnClickListener clnr = v -> {
                if (onPostClickListener != null) {
                    onPostClickListener.onClickAvatar(post.getUidAuthor());
                }
            };
            holder.layout.setOnClickListener(clnr);
            holder.imageViewAvatar.setOnClickListener(clnr);
            if (subscriptions.contains(post.getUidAuthor()) || post.getUidAuthor().equals(uid)) {
                holder.buttonSubscribe.setVisibility(View.GONE);
            } else {
                holder.buttonSubscribe.setVisibility(View.VISIBLE);
                holder.buttonSubscribe.setOnClickListener(v -> {
                    if (!subscriptions.contains(post.getUidAuthor())) {
                        subscriptions.add(post.getUidAuthor());
                    }
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(USERS_COLLECTION).document(uid).update(SUBSCRIPTIONS_STR, FieldValue.arrayUnion(post.getUidAuthor()));
                    db.collection(USERS_COLLECTION).document(post.getUidAuthor()).update(SUBSCRIBERS_STR, FieldValue.arrayUnion(uid));
                    holder.buttonSubscribe.setVisibility(View.GONE);
                });
            }
            holder.buttonComment.setOnClickListener(v -> Toast.makeText(v.getContext(), v.getResources().getString(R.string.hello_blank_fragment), Toast.LENGTH_SHORT).show());
            holder.buttonDislike.setOnClickListener(v -> Toast.makeText(v.getContext(), v.getResources().getString(R.string.hello_blank_fragment), Toast.LENGTH_SHORT).show());
            counter(holder, post);
            holder.buttonLike.setOnClickListener(v -> {
                if (post.getLikes().contains(uid)) {
                    post.deleteLike(uid);
                    counter(holder, post);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection(POSTS_STR).document(post.getUid());
                    docRef.update(LIKES_STR, FieldValue.arrayRemove(uid));
                } else {
                    post.addLike(uid);
                    counter(holder, post);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection(POSTS_STR).document(post.getUid());
                    docRef.update(LIKES_STR, FieldValue.arrayUnion(uid));
                }
            });
        }
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
        private ImageButton buttonDislike, buttonSubscribe;
        private LinearLayout layout;

        private BannerAdView mAdView;

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

            if ((count - 1) % 5 == 0) {
                mAdView = (BannerAdView) itemView.findViewById(R.id.adViewMain);
                mAdView.setAdSize(AdSize.stickySize(itemView.getContext(), 500));
                mAdView.setAdUnitId("R-M-2427151-2");
                mAdView.setBannerAdEventListener(new BannerAdEventListener() {
                    @Override
                    public void onAdLoaded() {
                        mAdView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
                        mAdView.setVisibility(View.GONE);
                        Log.e("ERROR", "ERROOOOOOOOOOOOOOOR");
                    }

                    @Override
                    public void onAdClicked() {

                    }

                    @Override
                    public void onLeftApplication() {

                    }

                    @Override
                    public void onReturnedToApplication() {

                    }

                    @Override
                    public void onImpression(@Nullable ImpressionData impressionData) {

                    }
                });

                mAdView.loadAd(new AdRequest.Builder().build());
            }
        }
    }
}
