package com.loviagin.rollic.fragments;

import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.username;
import static com.loviagin.rollic.models.Objects.postsCount;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.loviagin.rollic.R;
import com.loviagin.rollic.UserData;
import com.loviagin.rollic.activities.MainActivity;
import com.loviagin.rollic.models.Objects;
import com.loviagin.rollic.models.Post;

import java.util.ArrayList;
import java.util.List;

public class PhotoAddFragment extends Fragment {

    private EditText editTextDescription, editTextTags;
    private Button buttonSend, buttonCancel;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_add, container, false);
        editTextDescription = view.findViewById(R.id.etDescriptionPhotoAdd);
        editTextTags = view.findViewById(R.id.etTagsPhotoAdd);
        buttonCancel = view.findViewById(R.id.bCancelPhotoAdd);
        buttonSend = view.findViewById(R.id.bPublishPhotoAdd);
        progressBar = view.findViewById(R.id.pbPhotoAdd);

        buttonCancel.setOnClickListener(v -> startActivity(new Intent(getActivity(), MainActivity.class)));
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                List<String> strings = new ArrayList<>();
                List<String> likes = new ArrayList<>();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                strings.add("https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/images%2Ffe22a67a-f02f-4c6c-a8bc-70cd0ba23051?alt=media&token=0455a591-6e1d-4bf3-b579-5fcfbc9bd521");
                db.collection("posts")
//                        .document(String.valueOf(postsCount + 1)).set
                        .add(new Post("", editTextDescription.getText().toString().trim(),
                        editTextTags.getText().toString().trim(), uid, name,
                        "https://firebasestorage.googleapis.com/v0/b/workisland.appspot.com/o/avatars%2FfMClAWEqOybSPf8pFqYvc4OhSPu2cropped3876546716996985108.jpg?alt=media&token=149bcfcf-4e86-4460-bf99-a72e58c87baa",
                        username, strings, likes,0,0));
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        return view;
    }
}