package com.loviagin.rollic.adapters;

import static com.loviagin.rollic.models.Objects.preferences;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loviagin.rollic.R;
import com.loviagin.rollic.models.ChatItem;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatItem> messageList;
    private OnChatItemCLickListener onChatItemCLickListener;

    public ChatAdapter() {
        this.messageList = new LinkedList<>();
    }

    public ChatAdapter(List<ChatItem> messageList) {
        this.messageList = messageList;
    }

    public void addMessage(ChatItem string) {
        messageList.add(string);
    }

    public void setOnChatItemCLickListener(OnChatItemCLickListener onChatItemCLickListener) {
        this.onChatItemCLickListener = onChatItemCLickListener;
    }

    public interface OnChatItemCLickListener {
        void onClick(String cUid, String name, String uri);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatItem m0 = messageList.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (preferences.contains(m0.getcUser())) {
            holder.textViewText.setVisibility(View.VISIBLE);
            holder.textViewText.setText(preferences.getString(m0.getcUser(), ""));
        } else {
            holder.textViewText.setVisibility(View.GONE);
        }
        final Uri[] url = new Uri[1];

        if (m0.getAvatarUrl() != null && !m0.getAvatarUrl().equals("")) {
            if (m0.getAvatarUrl().contains("avatars/")) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference avatarRef = storageRef.child(m0.getAvatarUrl());
                avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    url[0] = uri;
                    Picasso.get().load(uri).into(holder.imageView);
                });
            } else if (m0.getAvatarUrl().equals("null")) {
                holder.imageView.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.user));
            } else {
                url[0] = Uri.parse(m0.getAvatarUrl());
                Picasso.get().load(m0.getAvatarUrl()).into(holder.imageView);
            }
        }
        holder.textViewName.setText(m0.getName());
        holder.itemView.setOnClickListener(view -> {
            if (onChatItemCLickListener != null) {
                onChatItemCLickListener.onClick(m0.getcUser(), m0.getName(), String.valueOf(url[0]));
            }
        });
    }

    public void updateList(List<ChatItem> newList) {
        messageList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        private ShapeableImageView imageView;
        private TextView textViewName, textViewText;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ivAvatarChat);
            textViewName = itemView.findViewById(R.id.tvNameChat);
            textViewText = itemView.findViewById(R.id.tvDescriptionChat);
        }
    }
}

