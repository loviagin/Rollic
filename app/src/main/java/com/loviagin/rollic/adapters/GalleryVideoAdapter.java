package com.loviagin.rollic.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loviagin.rollic.R;
import com.loviagin.rollic.models.Video;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

public class GalleryVideoAdapter extends RecyclerView.Adapter<GalleryVideoAdapter.GalleryVideoViewHolder> {

    private LinkedList<Video> listVideo;
    private GalleryVideoAdapter.OnGalleryVideoClickListener listener;

    public GalleryVideoAdapter(LinkedList<Video> listVideo) {
        this.listVideo = listVideo;
    }

    public void addVideo(Video v0) {
        listVideo.add(v0);
    }

    public interface OnGalleryVideoClickListener {
        void onVideoClick(int position);
    }

    public void setListener(OnGalleryVideoClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GalleryVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_post_item, parent, false);
        return new GalleryVideoAdapter.GalleryVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryVideoViewHolder holder, int position) {
        Video v0 = listVideo.get(position);
        Log.i("GA", position + v0.toString());

        Log.e("TAG2456", v0.getCaptureUrl());
        Picasso.get().load(Uri.parse(v0.getCaptureUrl())).into(holder.imageView);
//        Video v0 = listVideo.get(position);
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//        storageRef.child(v0.getVideoUrl()).getDownloadUrl()
//                .addOnSuccessListener(uri -> {
//                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                    try {
//                        retriever.setDataSource(holder.imageView.getContext(), uri);
//                        Bitmap bitmap = retriever.getFrameAtTime(1000,
//                                MediaMetadataRetriever.OPTION_CLOSEST); // получение кадра в заданное время
//                        // использование битмапа, например, можно установить его в ImageView
//                        holder.imageView.setImageBitmap(bitmap);
//                    } catch (IllegalArgumentException ex) {
//                        Log.e("GA", ex.getMessage());
//                        // обработка ошибки
//                    } finally {
//                        try {
//                            retriever.release(); // не забудьте освободить ресурсы
//                        } catch (IOException ignored) {}
//                    }
//                });
    }

    @Override
    public int getItemCount() {
        return listVideo.size();
    }

    class GalleryVideoViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public GalleryVideoViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ivGalleryPost);
            itemView.findViewById(R.id.tvTitlePostGallery).setVisibility(View.GONE);

            imageView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVideoClick(getAdapterPosition());
                }
            });
        }
    }
}
