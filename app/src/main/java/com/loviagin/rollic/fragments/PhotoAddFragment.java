package com.loviagin.rollic.fragments;

import static com.loviagin.rollic.Constants.POSTS_STR;
import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.urlAvatar;
import static com.loviagin.rollic.UserData.username;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loviagin.rollic.R;
import com.loviagin.rollic.activities.AccountActivity;
import com.loviagin.rollic.activities.MainActivity;
import com.loviagin.rollic.adapters.AddPostTabAdapter;
import com.loviagin.rollic.models.Post;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PhotoAddFragment extends Fragment {

    public static final String TAG = "Photo_Add_Fragment_TAG";
    private EditText editTextDescription, editTextTags;
    private Button buttonSend, buttonCancel;
    private static ImageView imageView1;
    private ProgressBar progressBar;

    private AddPostTabAdapter.OnAddPostClickListener mListener;
    private boolean isImageClick = false;

    public PhotoAddFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (AddPostTabAdapter.OnAddPostClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnAddPostClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_add, container, false);
        editTextDescription = view.findViewById(R.id.etDescriptionPhotoAdd);
        editTextTags = view.findViewById(R.id.etTagsPhotoAdd);
        buttonCancel = view.findViewById(R.id.bCancelPhotoAdd);
        buttonSend = view.findViewById(R.id.bPublishPhotoAdd);
        imageView1 = view.findViewById(R.id.vvVideoAdd);
        progressBar = view.findViewById(R.id.pbPhotoAdd);

        imageView1.setOnClickListener(v -> {
            if (mListener != null) {
                isImageClick = true;
                mListener.onImageClick();
            }
        });

        buttonCancel.setOnClickListener(v -> startActivity(new Intent(getActivity(), MainActivity.class)));
        buttonSend.setOnClickListener(v -> {
            if (isImageClick) {
                progressBar.setVisibility(View.VISIBLE);
                List<String> strings = new ArrayList<>();
                List<String> likes = new ArrayList<>();
                List<String> comments = new LinkedList<>();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                String iva = "post-images/" + uid + "/" + System.currentTimeMillis() + ".jpg";
                StorageReference imagesRef = storageRef.child(iva);
                imageView1.setDrawingCacheEnabled(true);
                imageView1.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imageView1.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = imagesRef.putBytes(data);
                uploadTask.addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                }).addOnSuccessListener(taskSnapshot -> {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    strings.add(iva);
                    db.collection(POSTS_STR)
//                        .document(String.valueOf(postsCount + 1)).set
                            .add(new Post("", editTextDescription.getText().toString().trim(),
                                    editTextTags.getText().toString().trim(), uid, name, urlAvatar, username, strings, likes, comments, 0))
                            .addOnSuccessListener(documentReference -> {
                                db.collection(USERS_COLLECTION).document(uid).update(POSTS_STR, FieldValue.arrayUnion(documentReference.getId()));

                                db.collection(POSTS_STR).document(documentReference.getId()).update("uid", documentReference.getId());
                                progressBar.setVisibility(View.GONE);
                                Log.d("TAG2506", documentReference.getId());
                                startActivity(new Intent(getActivity(), AccountActivity.class));
                            });
                });
            } else {
                Toast.makeText(getActivity(), R.string.image_requared_str, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public static void setImageView(Bitmap imageView) {
        imageView1.setImageBitmap(imageView);
    }
}