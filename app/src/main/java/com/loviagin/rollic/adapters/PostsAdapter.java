package com.loviagin.rollic.adapters;

import static com.loviagin.rollic.Constants.LIKES_STR;
import static com.loviagin.rollic.Constants.POSTS_STR;
import static com.loviagin.rollic.Constants.SUBSCRIBERS_STR;
import static com.loviagin.rollic.Constants.SUBSCRIPTIONS_STR;
import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.UserData.isPaid;
import static com.loviagin.rollic.UserData.subscriptions;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.urlAvatar;
import static com.loviagin.rollic.UserData.username;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.loviagin.rollic.models.Notification;
import com.loviagin.rollic.models.Post;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;
import com.yandex.mobile.ads.banner.AdSize;
import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> posts;
    private final List<Post> allPosts;
    //    private OnReachListener onReachListener;
    private OnPostClickListener onPostClickListener;

    private static final int TYPE_REGULAR = 0;
    private static final int TYPE_SPECIAL = 1;

    public PostsAdapter(List<Post> posts) {
        this.posts = posts;
        this.allPosts = posts;
    }

//    public interface OnReachListener {
//        void onReachEnd();
//    }

    public interface OnPostClickListener {
        void onClickAvatar(String usrUid);

        void onMenuClick(View view, String pstUid);

        void onCommentClick(String pstUid, Uri uri);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 6 == 0 && position != 0 && !isPaid ? TYPE_SPECIAL : TYPE_REGULAR;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_REGULAR) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
            return new PostsViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_item, parent, false);
            return new AdsViewHolder(view);
        }
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public void setOnPostClickListener(OnPostClickListener onPostClickListener) {
        this.onPostClickListener = onPostClickListener;
    }

    public void filter(boolean bb) {
        posts.clear();

        for (Post item : allPosts) {
            if (item.isPaid() == bb) {
                posts.add(item);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Uri[] uri0 = {null};
        if (getItemViewType(position) == TYPE_REGULAR) {
            PostsViewHolder viewHolder = (PostsViewHolder) holder;
//            if (position == posts.size() - 1 && onReachListener != null) {
//                onReachListener.onReachEnd();
//            }
            int postPosition = position - position / 6;
            Post post = posts.get(postPosition);
            viewHolder.textViewNickname.setText(String.format("@%s", post.getAuthorNickname()));

            if (post.getAuthorName() == null || post.getAuthorName().equals("")) {
                viewHolder.textViewName.setVisibility(View.GONE);
            } else {
                viewHolder.textViewName.setVisibility(View.VISIBLE);
                viewHolder.textViewName.setText(post.getAuthorName());
            }

            viewHolder.buttonRepost.setOnClickListener(view -> shareContent("Посмотри мой новый пост в Роллик", "https://rollic.loviagin.com/ulink?u=" + post.getUid(), viewHolder));

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            if (post.getAuthorAvatarUrl() != null && !post.getAuthorAvatarUrl().equals("")) {
                storageRef.child(post.getAuthorAvatarUrl()).getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            uri0[0] = uri;
                            Picasso.get().load(uri).into(viewHolder.imageViewAvatar);
                        });
            }

            viewHolder.textViewDescription.setText(post.getDescription());
            if (post.getTitle().equals("")) {
                viewHolder.imageView1.setVisibility(View.VISIBLE);
                if (post.getImagesUrls() != null && post.getImagesUrls().get(0) != null) {
                    storageRef.child(post.getImagesUrls().get(0)).getDownloadUrl()
                            .addOnSuccessListener(uri -> Picasso.get().load(uri).into((viewHolder.imageView1)));
                }
                viewHolder.textViewTitle.setVisibility(View.GONE);
            } else {
                viewHolder.textViewTitle.setText(post.getTitle());
                viewHolder.textViewTitle.setVisibility(View.VISIBLE);
                viewHolder.imageView1.setVisibility(View.GONE);
            }
            View.OnClickListener clnr = v -> {
                if (onPostClickListener != null) {
                    onPostClickListener.onClickAvatar(post.getUidAuthor());
                }
            };
            viewHolder.layout.setOnClickListener(clnr);
            viewHolder.imageViewAvatar.setOnClickListener(clnr);
            if (subscriptions.contains(post.getUidAuthor())) {
                viewHolder.buttonSubscribe.setVisibility(View.GONE);
            } else if (post.getUidAuthor().equals(uid)) {
                viewHolder.buttonSubscribe.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.fi_rr_menu_dots));
                viewHolder.buttonSubscribe.setOnClickListener(v -> {
                    if (onPostClickListener != null) {
                        onPostClickListener.onMenuClick(v, post.getUid());
                    }
                });

            } else {
                viewHolder.buttonSubscribe.setVisibility(View.VISIBLE);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                viewHolder.buttonSubscribe.setOnClickListener(v -> {
                    if (!subscriptions.contains(post.getUidAuthor())) {
                        subscriptions.add(post.getUidAuthor());
                        db.collection(USERS_COLLECTION).document(post.getUidAuthor()).get().addOnSuccessListener(documentSnapshot -> {
                            List<String> devices = (List<String>) documentSnapshot.get("deviceTokens");
                            if (devices != null && devices.size() > 0) {
                                try {
                                    for (String str : devices) {
                                        JSONObject notificationContent = new JSONObject(
                                                "{'contents': {'en':'Новый подписчик @" + username + "'}, " +
                                                        "'include_player_ids': ['" + str + "'], " +
                                                        "'data': {'activityToBeOpened': 'NotificationActivity'}}"
                                        );
                                        OneSignal.postNotification(notificationContent, null);
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });

                        db.collection("notifications").add(new
                                        Notification("Новый подписчик @" + username, "", urlAvatar, "p/" + uid))
                                .addOnSuccessListener(documentReference ->
                                        db.collection(USERS_COLLECTION).document(post.getUidAuthor()).update("notifications", FieldValue.arrayUnion(documentReference.getId())));
                    }
                    db.collection(USERS_COLLECTION).document(uid).update(SUBSCRIPTIONS_STR, FieldValue.arrayUnion(post.getUidAuthor()));
                    db.collection(USERS_COLLECTION).document(post.getUidAuthor()).update(SUBSCRIBERS_STR, FieldValue.arrayUnion(uid));
                    viewHolder.buttonSubscribe.setVisibility(View.GONE);
                });
            }
            viewHolder.buttonComment.setOnClickListener(v -> {
                if (onPostClickListener != null) {
                    onPostClickListener.onCommentClick(post.getUid(), uri0[0]);
                }
            });
            viewHolder.buttonDislike.setOnClickListener(v -> Toast.makeText(v.getContext(), v.getResources().getString(R.string.hello_blank_fragment), Toast.LENGTH_SHORT).show());
            counter(viewHolder, post);
            viewHolder.buttonLike.setOnClickListener(v -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                if (post.getLikes().contains(uid)) {
                    post.deleteLike(uid);
                    counter(viewHolder, post);
                    DocumentReference docRef = db.collection(POSTS_STR).document(post.getUid());
                    docRef.update(LIKES_STR, FieldValue.arrayRemove(uid));
                } else {
                    post.addLike(uid);
                    counter(viewHolder, post);
                    db.collection(USERS_COLLECTION).document(post.getUidAuthor()).get().addOnSuccessListener(documentSnapshot -> {
                        List<String> devices = (List<String>) documentSnapshot.get("deviceTokens");
                        if (devices != null && devices.size() > 0) {
                            try {
                                for (String str : devices) {
                                    JSONObject notificationContent = new JSONObject(
                                            "{'contents': {'en':'Новый лайк от @" + username + "'}, " +
                                                    "'include_player_ids': ['" + str + "'], " +
                                                    "'data': {'activityToBeOpened': 'NotificationActivity'}}"
                                    );
                                    OneSignal.postNotification(notificationContent, null);
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    db.collection("notifications").add(new
                                    Notification("Новый лайк от @" + username, "Теперь их всего " + viewHolder.buttonLike.getText(), urlAvatar, "l/" + post.getUid()))
                            .addOnSuccessListener(documentReference ->
                                    db.collection(USERS_COLLECTION).document(post.getUidAuthor()).update("notifications", FieldValue.arrayUnion(documentReference.getId())));


                    DocumentReference docRef = db.collection(POSTS_STR).document(post.getUid());
                    docRef.update(LIKES_STR, FieldValue.arrayUnion(uid));
                }
            });
        } else {
            AdsViewHolder ignored = (AdsViewHolder) holder;
        }
    }

    private void shareContent(String title, String url, PostsViewHolder holder) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        holder.itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Поделиться постом"));
    }

    @Override
    public int getItemCount() {
        return posts.size() + posts.size() / 5;  // Add an ad after every 5 posts
    }

    //    public void setOnReachListener(OnReachListener onReachListener) {
//        this.onReachListener = onReachListener;
//    }
    private void counter(PostsViewHolder holder, Post post) {
        setColorToButton(holder, post);
        holder.buttonLike.setText(String.valueOf(post.getLikes().size()));
        holder.buttonComment.setText(String.valueOf(post.getComments().size()));
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

    class AdsViewHolder extends RecyclerView.ViewHolder {

        private BannerAdView mAdView;

        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);
            mAdView = itemView.findViewById(R.id.adViewMain);
            mAdView.setVisibility(View.GONE);
            mAdView.setAdSize(AdSize.stickySize(itemView.getContext(), 400));
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
