package com.loviagin.rollic.adapters;

import static com.loviagin.rollic.UserData.uid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.loviagin.rollic.R;
import com.loviagin.rollic.helpers.ImageDialog;
import com.loviagin.rollic.models.Message;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private final int SELF_MESSAGE = 0;
    private final int PERSON_MESSAGE = 1;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    public MessageAdapter() {
        this.messageList = new LinkedList<>();
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public void addMessage(Message message) {
        messageList.add(message);
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getcUid().equals(uid)) {
            return SELF_MESSAGE;
        } else {
            return PERSON_MESSAGE;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SELF_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.self_message_item, parent, false);
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message m0 = messageList.get(position);

        if (m0.getMediaUrl() != null && !m0.getMediaUrl().isEmpty()) {
            if (m0.getText() != null && m0.getText().equals("VIDEO")) {
                holder.imageView.setVisibility(View.GONE);
                holder.textView.setVisibility(View.GONE);
                holder.playerView.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.VISIBLE);

                holder.setVideoUrl(m0.getMediaUrl());
            } else {
                holder.imageView.setVisibility(View.VISIBLE);
                Picasso.get().load(m0.getMediaUrl()).into(holder.imageView);
                holder.textView.setVisibility(View.GONE);
                holder.playerView.setVisibility(View.GONE);
                holder.imageView.setOnClickListener(v -> {
                    ImageDialog dialog = new ImageDialog(m0.getMediaUrl());
                    dialog.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "ImageDialog");
                });
            }
        } else {
            holder.textView.setText(m0.getText());
            holder.textView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.playerView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;
        private PlayerView playerView;
        private ExoPlayer exoPlayer;
        private String videoUrl;
        private ProgressBar progressBar;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.tvMessage);
            imageView = itemView.findViewById(R.id.ivMessage);
            playerView = itemView.findViewById(R.id.pvMessage);
            progressBar = itemView.findViewById(R.id.videoLoadingProgressBar);
        }

        void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
            if (this.videoUrl != null) {
                initializePlayer();
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
                        if (state == Player.STATE_BUFFERING) {
                            progressBar.setVisibility(View.VISIBLE);
                        } else if (state == Player.STATE_READY || state == Player.STATE_ENDED) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    }
}