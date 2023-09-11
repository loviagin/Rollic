package com.loviagin.rollic.adapters;

import static com.loviagin.rollic.Constants.USER_STR;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loviagin.rollic.R;
import com.loviagin.rollic.activities.AccountActivity;
import com.loviagin.rollic.models.User;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_view_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textViewName.setText(user.getF_name());
        holder.textViewNickname.setText(String.format("@%s", user.getUsername()));
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().equals("")) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            storageRef.child(user.getAvatarUrl()).getDownloadUrl()
                    .addOnSuccessListener(uri -> Picasso.get().load(uri).into(holder.imageView));
        }
        holder.itemView.setOnClickListener(view -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .whereEqualTo("email", user.getEmail())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    view.getContext().startActivity(new Intent(view.getContext(), AccountActivity.class).putExtra(USER_STR, document.getId()));
                                }
                            }
                        });

        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void clear(){
        if (userList != null){
            userList.clear();
        } else {
            userList = new LinkedList<>();
        }
        notifyDataSetChanged();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName, textViewNickname;
        private ShapeableImageView imageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.tvNameUser);
            textViewNickname = itemView.findViewById(R.id.tvNicknameUser);
            imageView = itemView.findViewById(R.id.ivAvatarUser);
        }
    }
}
