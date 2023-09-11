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
import com.loviagin.rollic.R;
import com.loviagin.rollic.activities.AccountActivity;
import com.loviagin.rollic.activities.MainActivity;
import com.loviagin.rollic.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TopUsersAdapter extends RecyclerView.Adapter<TopUsersAdapter.TopUsersViewHolder> {

    private List<User> users;

    public TopUsersAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public TopUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_user_item, parent, false);
        return new TopUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopUsersViewHolder holder, int position) {
        User u0 = users.get(position);

        holder.textViewName.setText(u0.getF_name());
        holder.textViewNickname.setText(String.format("@%s", u0.getUsername()));

        holder.imageView.setOnClickListener(view -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .whereEqualTo("email", u0.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    view.getContext().startActivity(new Intent(view.getContext(), AccountActivity.class).putExtra(USER_STR, document.getId()));
                                }
                            }
                        }
                    });
        });

        Picasso.get().load(u0.getAvatarUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class TopUsersViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName, textViewNickname;
        private ShapeableImageView imageView;

        public TopUsersViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.tvNameTopUser);
            textViewNickname = itemView.findViewById(R.id.tvNicknameTopUser);
            imageView = itemView.findViewById(R.id.ivAvatarTopUser);
        }
    }
}
