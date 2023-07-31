package com.loviagin.rollic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.loviagin.rollic.R;

import java.util.List;
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<String> videoUrls;

    public VideoAdapter(List<String> videoUrls) {
        this.videoUrls = videoUrls;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.setVideoUrl(videoUrls.get(position));
    }

    @Override
    public int getItemCount() {
        return videoUrls.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        private PlayerView playerView;
        private ExoPlayer exoPlayer;
        private String videoUrl;
        private SeekBar seekBar;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.video_view);
            seekBar = itemView.findViewById(R.id.seekBar);

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

            playerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (exoPlayer.getPlayWhenReady()) {
                        exoPlayer.setPlayWhenReady(false);
                    } else {
                        exoPlayer.setPlayWhenReady(true);
                    }
                }
            });
        }

        void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
            if (this.videoUrl != null) {
                initializePlayer();
            } else {
                releasePlayer();
            }
        }

        public void initializePlayer() {
            if (exoPlayer == null) {
                exoPlayer = new ExoPlayer.Builder(playerView.getContext()).build();
                playerView.setPlayer(exoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(videoUrl);
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
