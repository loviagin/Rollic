package com.loviagin.rollic.fragments;

import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.username;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.loviagin.rollic.R;
import com.loviagin.rollic.activities.MainActivity;
import com.loviagin.rollic.models.Post;

import java.util.ArrayList;
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
            if (editTextTitle.getText() != null && editTextDescription.getText() != null) {
                progressBar.setVisibility(View.VISIBLE);
                List<String> likes = new ArrayList<>();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("posts")
//                        .document(String.valueOf(postsCount + 1)).set
                        .add(new Post(editTextTitle.getText().toString().trim(), editTextDescription.getText().toString().trim(),
                                editTextTags.getText().toString().trim(), uid, name,
                                "https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/avatars%2FfMClAWEqOybSPf8pFqYvc4OhSPu2cropped3876546716996985108.jpg?alt=media&token=149bcfcf-4e86-4460-bf99-a72e58c87baa",
                                username, null, likes, 0, 0));
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(), MainActivity.class));
            } else {
                Toast.makeText(getActivity(), "Заполните заголовок и описание", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}