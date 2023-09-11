package com.loviagin.rollic.fragments;

import static com.loviagin.rollic.Constants.POSTS_STR;
import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.UserData.isPaid;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.urlAvatar;
import static com.loviagin.rollic.UserData.username;

import static okhttp3.Request.*;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.privacysandbox.tools.core.model.Method;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loviagin.rollic.R;
import com.loviagin.rollic.UserData;
import com.loviagin.rollic.activities.AccountActivity;
import com.loviagin.rollic.activities.MainActivity;
import com.loviagin.rollic.models.Message;
import com.loviagin.rollic.models.Post;
import com.loviagin.rollic.workers.OpenAIApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostAddFragment extends Fragment {

    private EditText editTextDescription, editTextTags, editTextTitle;
    private Button buttonSend, buttonCancel;
    private ProgressBar progressBar;

    private MaterialSwitch aSwitch;
    private String apiurl = "https://api.openai.com/v1/completions";
    private boolean isPaid = false;
//    String accessToken = "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";

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
        aSwitch = view.findViewById(R.id.tsPostAdd);

        /**
         * It's for ChatGPT
         */
//        if (isPaid) {
//            Drawable icon = ContextCompat.getDrawable(view.getContext(), R.drawable.fi_rr_ai);
//            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
//            editTextTitle.setCompoundDrawables(null, null, icon, null);
//            editTextTitle.setOnTouchListener((v, event) -> {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    // Проверяем, что касание было в области иконки
//                    if (event.getRawX() >= (editTextTitle.getRight() - editTextTitle.getCompoundDrawables()[2].getBounds().width())) {
//                        v.performClick();
//                        String question = editTextTitle.getText().toString().trim();
//                        String query = editTextTitle.getText().toString();
//                        new GetGPT4ResponseTask().execute(query);
//
//                        //                        Toast.makeText(view.getContext(), "Icon clicked!", Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                }
//                return false;
//            });
//
//        }

        aSwitch.setVisibility(UserData.isPaid ? View.VISIBLE : View.GONE);

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> isPaid = isChecked);

        buttonCancel.setOnClickListener(v -> startActivity(new Intent(getActivity(), MainActivity.class)));
        buttonSend.setOnClickListener(v -> {
            if (editTextTitle.getText() != null && editTextTitle.getText().length() > 0 && editTextDescription.getText() != null && editTextDescription.getText().length() > 0) {
                progressBar.setVisibility(View.VISIBLE);
                List<String> likes = new ArrayList<>();
                List<String> comments = new LinkedList<>();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(POSTS_STR)
                        .add(new Post(editTextTitle.getText().toString().trim(), editTextDescription.getText().toString().trim(),
                                editTextTags.getText().toString().trim(), uid, name, urlAvatar, username, null, likes, comments, 0, isPaid))
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

    private class GetGPT4ResponseTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return OpenAIApi.getResponse(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            editTextDescription.setText(result);
        }
    }
}