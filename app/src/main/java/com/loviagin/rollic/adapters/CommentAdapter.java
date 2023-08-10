package com.loviagin.rollic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loviagin.rollic.R;
import com.loviagin.rollic.models.Comment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments;
    private OnCommentClickListener onCommentClickListener;

    public interface OnCommentClickListener{
        void onAccountClick(String usrUid);
    }

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        this.onCommentClickListener = onCommentClickListener;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void addComment(int i, Comment comment) {
        comments.add(0, comment);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        if (comment.getUrlAvatar() != null) {
            storageRef.child(comment.getUrlAvatar()).getDownloadUrl()
                    .addOnSuccessListener(uri -> Picasso.get().load(uri).into((holder.imageViewAvatar)));
        }
        if (comment.getUsrName() == null || comment.getUsrName().equals("")) {
            holder.textViewName.setVisibility(View.GONE);
        } else {
            holder.textViewName.setVisibility(View.VISIBLE);
            holder.textViewName.setText(comment.getUsrName());
        }

        holder.textViewNickname.setText(comment.getUsrNickname());
        holder.textViewText.setText(comment.getText());

        holder.linearLayout.setOnClickListener(v -> {
            if (onCommentClickListener != null){
                onCommentClickListener.onAccountClick(comment.getUsrUid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageViewAvatar;
        private TextView textViewName, textViewNickname, textViewText;
        private LinearLayout linearLayout;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.findViewById(R.id.bSubscribePost).setVisibility(View.INVISIBLE);
            imageViewAvatar = itemView.findViewById(R.id.ivAvatarRegister);
            textViewName = itemView.findViewById(R.id.tvNamePost);
            textViewNickname = itemView.findViewById(R.id.tvNicknamePost);
            textViewText = itemView.findViewById(R.id.tvTextComment);
            linearLayout = itemView.findViewById(R.id.llPostMain);
        }
    }
}
