package com.loviagin.rollic.adapters;

import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.Constants.USER_STR;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.urlAvatar;
import static com.loviagin.rollic.UserData.username;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.fragment.app.FragmentManager;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loviagin.rollic.R;
import com.loviagin.rollic.activities.AccountActivity;
import com.loviagin.rollic.activities.MainActivity;
import com.loviagin.rollic.fragments.CommentsBottomSheet;
import com.loviagin.rollic.models.Notification;
import com.loviagin.rollic.models.Video;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<Video> videoUrls;
    private FragmentManager fragmentManager;

    public VideoAdapter(List<Video> videoUrls, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.videoUrls = videoUrls;
    }

    public void addVideo(Video video) {
        this.videoUrls.add(video);
        notifyItemInserted(videoUrls.size() - 1);
    }

    public void addVideo(int i, Video video) {
        this.videoUrls.add(i, video);
        notifyItemInserted(i);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoUrls.get(position);
        holder.setVideoUrl(video);

        if (video.getLikes().contains(uid)) {
            holder.buttonLike.setIcon(holder.itemView.getContext().getResources().getDrawable(R.drawable.fi_rr_heart_fill));
        } else {
            holder.buttonLike.setIcon(holder.itemView.getContext().getResources().getDrawable(R.drawable.fi_rr_like));
        }

        holder.buttonLike.setText(video.getLikes().size() + "");
        holder.buttonComment.setText(video.getComments().size() + "");

        if (video.getDescription() != null && !video.getDescription().equals("")) {
            holder.textViewDescription.setVisibility(View.VISIBLE);
            holder.textViewDescription.setText(video.getDescription());
        } else {
            holder.textViewDescription.setVisibility(View.GONE);
        }

        holder.buttonRepost.setOnClickListener(view -> shareContent("Посмотри мое новое видео в Роллик", "https://rollic.loviagin.com/video?u=" + video.getUid(), holder));
        holder.textViewName.setText(String.format("@%s", video.getAuthorNickname()));

        holder.buttonComment.setOnClickListener(v -> {
            CommentsBottomSheet bottomSheet = new CommentsBottomSheet(video.getUid(), video.getUidAuthor());
            bottomSheet.show(fragmentManager, bottomSheet.getTag());
        });

        if (video.getAuthorAvatarUrl() != null && !video.getAuthorAvatarUrl().equals("")) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            storageRef.child(video.getAuthorAvatarUrl()).getDownloadUrl()
                    .addOnSuccessListener(uri -> Picasso.get().load(uri).into(holder.imageView));
        }
        holder.imageView.setOnClickListener(view -> holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), AccountActivity.class).putExtra(USER_STR, video.getUidAuthor())));

        holder.buttonLike.setOnClickListener(view -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (!video.getLikes().contains(uid)) {
                db.collection("video-posts").document(video.getUid()).update("likes", FieldValue.arrayUnion(uid));
                holder.buttonLike.setText(String.valueOf(video.getLikes().size() + 1));
                holder.buttonLike.setIcon(holder.itemView.getContext().getResources().getDrawable(R.drawable.fi_rr_heart_fill));
                db.collection(USERS_COLLECTION).document(video.getUidAuthor()).get().addOnSuccessListener(documentSnapshot -> {
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
                                Notification("Новый лайк от @" + username, "Теперь их всего " + video.getLikes().size() + 1, urlAvatar, "vl/" + video.getUid()))
                        .addOnSuccessListener(documentReference ->
                                db.collection(USERS_COLLECTION).document(video.getUidAuthor()).update("notifications", FieldValue.arrayUnion(documentReference.getId())));


            } else { // TODO realise if user click unlike
                db.collection("video-posts").document(video.getUid()).update("likes", FieldValue.arrayRemove(uid));
                holder.buttonLike.setText(String.valueOf(video.getLikes().size() - 1));
                holder.buttonLike.setIcon(holder.itemView.getContext().getResources().getDrawable(R.drawable.fi_rr_like));
            }
        });
    }

    private void shareContent(String title, String url, VideoViewHolder holder) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        holder.itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Поделиться видео"));
    }

    @Override
    public int getItemCount() {
        return videoUrls.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        private PlayerView playerView;
        private ExoPlayer exoPlayer;
        private Video videoUrl;
        private SeekBar seekBar;
        private MaterialButton buttonLike, buttonComment, buttonRepost;
        private ShapeableImageView imageView;
        private TextView textViewName, textViewDescription;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.video_view);
            seekBar = itemView.findViewById(R.id.seekBar);
            buttonLike = itemView.findViewById(R.id.bLikeVideo);
            buttonComment = itemView.findViewById(R.id.bCommentVideo);
            buttonRepost = itemView.findViewById(R.id.bRepostVideo);
            imageView = itemView.findViewById(R.id.ivAvatarVideo);
            textViewName = itemView.findViewById(R.id.tvNicknameVideo);
            textViewDescription = itemView.findViewById(R.id.tvDescriptionVideo);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        exoPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // Not needed
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // Not needed
                }
            });

            playerView.setOnClickListener(v -> {
                exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
            });
        }

        void setVideoUrl(Video videoUrl) {
            this.videoUrl = videoUrl;
            if (this.videoUrl != null) {
                initializePlayer();
            } else {
                releasePlayer();
            }
        }

        @OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
        public void initializePlayer() {
            if (exoPlayer == null) {
                exoPlayer = new ExoPlayer.Builder(playerView.getContext()).build();

                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

                playerView.setPlayer(exoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(videoUrl.getVideoUrl());
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();

                exoPlayer.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int state) {
                        if (state == Player.STATE_READY && exoPlayer.getPlayWhenReady()) {
                            seekBar.setMax((int) exoPlayer.getDuration());
                            startSeekBarUpdater();
                        }
                    }
                });
            }
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE); // добавить эту строку для циклического воспроизведения
        }

        private void startSeekBarUpdater() {
            seekBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (exoPlayer != null) {
                        seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                        startSeekBarUpdater();
                    }
                }
            }, 1000);
        }

        public void releasePlayer() {
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(false);
            }
        }
    }
}


//public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
//    private final List<Video> videoList;
//    private final Context context;
//
//    public VideoAdapter(Context context) {
//        this.videoList = new ArrayList<>();
//        this.context = context;
//    }
//
//    public void addVideo(Video v) {
//        videoList.add(v);
//    }
//
//    @NonNull
//    @Override
//    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
//        return new VideoViewHolder(view);
//    }
//
//    @Override
//    public void onViewAttachedToWindow(@NonNull VideoViewHolder holder) {
//        super.onViewAttachedToWindow(holder);
//        holder.preparePlayer();
//    }
//
//    @Override
//    public void onViewDetachedFromWindow(@NonNull VideoViewHolder holder) {
//        super.onViewDetachedFromWindow(holder);
//        holder.releasePlayer();
//    }
//
//    @Override
//    public void onViewRecycled(@NonNull VideoViewHolder holder) {
//        if (holder.exoPlayer != null) {
//            holder.exoPlayer.release();
//            holder.exoPlayer = null;
//        }
//        super.onViewRecycled(holder);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
//        Video video = videoList.get(position);
//
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference videoRef = storage.getReference().child(video.getVideoUrl());
//        videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
//            if (holder.exoPlayer != null) {
//                MediaItem mediaItem = MediaItem.fromUri(uri);
//                holder.exoPlayer.setMediaItem(mediaItem);
//                holder.exoPlayer.prepare();
//                holder.exoPlayer.play();
//            }
//        });
//
//        // Play or pause video on click
//        holder.videoView.setOnClickListener(v -> {
//            if (holder.exoPlayer.isPlaying()) {
//                holder.exoPlayer.pause();
//            } else {
//                holder.exoPlayer.play();
//            }
//        });
////        Video video = videoList.get(position);
//////        holder.authorNickname.setText(video.getAuthorNickname());
////
////        // Prepare the ExoPlayer
////        holder.exoPlayer = new ExoPlayer.Builder(context).build();
////        holder.videoView.setPlayer(holder.exoPlayer);
////
////        FirebaseStorage storage = FirebaseStorage.getInstance();
////        StorageReference videoRef = storage.getReference().child(video.getVideoUrl());
////        videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
////            MediaItem mediaItem = MediaItem.fromUri(uri);
////            holder.exoPlayer.setMediaItem(mediaItem);
////            holder.exoPlayer.prepare();
////            holder.exoPlayer.play();  // Play the video as soon as it is ready
////        });
//////        MediaItem mediaItem = MediaItem.fromUri(video.getVideoUrl());
//////        exoPlayer.setMediaItem(mediaItem);
//////        exoPlayer.prepare();
////
////        // Handle like button click
//////        holder.likeButton.setOnClickListener(v -> {
//////            // handle like action
//////        });
//////
//////        // Handle comment button click
//////        holder.commentButton.setOnClickListener(v -> {
//////            // handle comment action
//////        });
////
////        // Play or pause video on click
////        holder.videoView.setOnClickListener(v -> {
////            if (holder.exoPlayer.isPlaying()) {
////                holder.exoPlayer.pause();
////            } else {
////                holder.exoPlayer.play();
////            }
////        });
////
////        // Update the seek bar as video plays
//////        exoPlayer.addListener(new Player.Listener() {
//////            @Override
//////            public void onEvents(Player player, Player.Events events) {
//////                if (player.isPlaying()) {
////////                    holder.seekBar.setMax((int) player.getDuration());
////////                    holder.seekBar.postDelayed(new Runnable() {
////////                        @Override
////////                        public void run() {
////////                            holder.seekBar.setProgress((int) player.getCurrentPosition());
////////                            holder.seekBar.postDelayed(this, 100);
////////                        }
////////                    }, 100);
//////                }
//////            }
//////        });
////
////        // Handle changes in seek bar
//////        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//////            @Override
//////            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//////                if (fromUser) {
//////                    exoPlayer.seekTo(progress);
//////                }
//////            }
//////
//////            @Override
//////            public void onStartTrackingTouch(SeekBar seekBar) {
//////                // No-op
//////            }
//////
//////            @Override
//////            public void onStopTrackingTouch(SeekBar seekBar) {
//////                // No-op
//////            }
//////        });
////
////        // Important: Release the player when the view is recycled
////        holder.itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
////            @Override
////            public void onViewAttachedToWindow(View v) {
////                // No-op
////            }
////
////            @Override
////            public void onViewDetachedFromWindow(View v) {
////                holder.exoPlayer.release();
////            }
////        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return videoList.size();
//    }
//
//    static class VideoViewHolder extends RecyclerView.ViewHolder {
//        //        private TextView authorNickname;
//        private PlayerView videoView;
//        private ExoPlayer exoPlayer;
//        //        private ImageButton likeButton, commentButton;
////        private SeekBar seekBar;
//
//        VideoViewHolder(@NonNull View itemView) {
//            super(itemView);
//            videoView = itemView.findViewById(R.id.pvVideo);
//            videoView.setUseController(false);
////            authorNickname = itemView.findViewById(R.id.author_nickname);
////            likeButton = itemView.findViewById(R.id.like_button);
////            commentButton = itemView.findViewById(R.id.comment_button);
////            seekBar = itemView.findViewById(R.id.sbVideo);
//        }
//
//        void preparePlayer() {
//            exoPlayer = new ExoPlayer.Builder(videoView.getContext()).build();
//            videoView.setPlayer(exoPlayer);
//        }
//
//        void releasePlayer() {
//            if (exoPlayer != null) {
//                exoPlayer.release();
//                exoPlayer = null;
//            }
//        }
//    }
//}
