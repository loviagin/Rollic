package com.loviagin.rollic.fragments;

import static com.loviagin.rollic.Constants.POSTS_STR;
import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.urlAvatar;
import static com.loviagin.rollic.UserData.username;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loviagin.rollic.R;
import com.loviagin.rollic.activities.AccountActivity;
import com.loviagin.rollic.activities.MainActivity;
import com.loviagin.rollic.models.Post;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PostAddFragment extends Fragment {

    private EditText editTextDescription, editTextTags, editTextTitle;
    private Button buttonSend, buttonCancel;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_add, container, false);
        editTextDescription = view.findViewById(R.id.etDescriptionPostAdd);
        editTextTitle = view.findViewById(R.id.etTitlePostAdd);
        editTextTags = view.findViewById(R.id.etTagsPostAdd);
        buttonCancel = view.findViewById(R.id.bCancelPostAdd);
        buttonSend = view.findViewById(R.id.bPublishPostAdd);
        progressBar = view.findViewById(R.id.pbPostAdd);

        buttonCancel.setOnClickListener(v -> startActivity(new Intent(getActivity(), MainActivity.class)));
        buttonSend.setOnClickListener(v -> {
            if (editTextTitle.getText() != null && editTextTitle.getText().length() > 0 && editTextDescription.getText() != null && editTextDescription.getText().length() > 0) {
                progressBar.setVisibility(View.VISIBLE);
                List<String> likes = new ArrayList<>();
                List<String> comments = new LinkedList<>();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(POSTS_STR)
                        .add(new Post(editTextTitle.getText().toString().trim(), editTextDescription.getText().toString().trim(),
                                editTextTags.getText().toString().trim(), uid, name, urlAvatar, username, null, likes, comments, 0))
                        .addOnSuccessListener(documentReference -> {
                            db.collection(USERS_COLLECTION).document(uid).update(POSTS_STR, FieldValue.arrayUnion(documentReference.getId()));

                            db.collection(POSTS_STR).document(documentReference.getId()).update("uid", documentReference.getId());
                            progressBar.setVisibility(View.GONE);
                            Log.d("TAG2506", documentReference.getId());
                            startActivity(new Intent(getActivity(), AccountActivity.class));
                        });

            } else {
                Toast.makeText(getActivity(), "Заполните заголовок и описание", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}