package com.loviagin.rollic.adapters;

import static com.loviagin.rollic.Constants.POSITION;
import static com.loviagin.rollic.UserData.dynPosts;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loviagin.rollic.R;
import com.loviagin.rollic.activities.MainActivity;
import com.loviagin.rollic.models.PaidPost;
import com.loviagin.rollic.models.Post;

import java.util.LinkedList;
import java.util.List;

public class PaidFeedAdapter extends RecyclerView.Adapter<PaidFeedAdapter.PaidFeedViewHolder> {

    private List<PaidPost> paidFeedPosts;
    private GalleryPostAdapter adapter;

    public PaidFeedAdapter(List<PaidPost> paidFeedPosts) {
        this.paidFeedPosts = paidFeedPosts;
    }

    public void addPaidPost(PaidPost pp) {
        paidFeedPosts.add(pp);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PaidFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paid_feed_item, parent, false);
        return new PaidFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaidFeedViewHolder holder, int position) {
        PaidPost post = paidFeedPosts.get(position);
        holder.textView.setText(post.getName());
        List<Post> postList = new LinkedList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").whereIn("uid", post.getPosts()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Проверка на наличие поля paid
                        Post p = document.toObject(Post.class);
                        if (p.isPaid()) {
                            postList.add(p);
                        }
                    }
                    adapter = new GalleryPostAdapter((LinkedList<Post>) postList);
                    adapter.setOnGalleryPostClickListener(position1 -> {
                        dynPosts = postList;
                        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), MainActivity.class).putExtra(POSITION, position1));
                    });
                    holder.recyclerView.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), 3));
                    holder.recyclerView.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return paidFeedPosts.size();
    }

    class PaidFeedViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private TextView textView;

        public PaidFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.tvPaidFeed);
            recyclerView = itemView.findViewById(R.id.rvPaidFeed);
        }
    }
}
