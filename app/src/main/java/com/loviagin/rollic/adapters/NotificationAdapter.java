package com.loviagin.rollic.adapters;

import static com.loviagin.rollic.Constants.AVATAR_URL;
import static com.loviagin.rollic.Constants.POST_UID;
import static com.loviagin.rollic.Constants.USER_STR;
import static com.loviagin.rollic.Constants.USER_UID;

import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loviagin.rollic.R;
import com.loviagin.rollic.activities.AccountActivity;
import com.loviagin.rollic.activities.MessageActivity;
import com.loviagin.rollic.activities.PostActivity;
import com.loviagin.rollic.models.Notification;
import com.loviagin.rollic.models.User;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;

    public NotificationAdapter() {
        notificationList = new LinkedList<>();
    }

    public void addNotification(Notification notification) {
        this.notificationList.add(notification);
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.textViewName.setText(notification.getTitle());
        holder.textViewDesc.setText(notification.getDescription());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        if (notification.getAvatarUrl() != null && !notification.getAvatarUrl().equals("")) {
            storageRef.child(notification.getAvatarUrl()).getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Picasso.get().load(uri).into(holder.imageView);
                    });
        }
        if (!notification.isRead()) {
            holder.textViewName.setTypeface(null, Typeface.BOLD);
        } else {
            holder.textViewName.setTypeface(null, Typeface.NORMAL);
        }
        holder.itemView.setOnClickListener(view -> {
            String string = notification.getUrl().substring(0, 1);
            String postId = notification.getUrl().substring(2);
            Log.e("TA245G", notification.getUrl() + " ----- " + string + " ----- " + postId);
            Intent intent = new Intent(holder.itemView.getContext(), PostActivity.class).putExtra(POST_UID, postId).putExtra(AVATAR_URL, String.valueOf(notification.getAvatarUrl()));
            switch (string) {
                case "c": // comment TODO open post page with uid
                case "l": // like TODO open post page with uid
                    holder.itemView.getContext().startActivity(intent);
                    break;
                case "p": // subscribe TODO open user page
                    holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), AccountActivity.class).putExtra(USER_STR, postId));
                    break;
                case "m": // сообщение
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(postId).get().addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), MessageActivity.class).putExtra(USER_UID, postId).putExtra("FIRST_NAME", user.getF_name()).putExtra(AVATAR_URL, user.getAvatarUrl() != null ? user.getAvatarUrl() : "null"));
                        }
                    });
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {

        private ShapeableImageView imageView;
        private TextView textViewName, textViewDesc;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivAvatarNotification);
            textViewName = itemView.findViewById(R.id.tvTitleNotification);
            textViewDesc = itemView.findViewById(R.id.tvDescriptionNotification);
        }
    }
}
