package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.AVATAR_URL;
import static com.loviagin.rollic.Constants.COMMENTS_STR;
import static com.loviagin.rollic.Constants.LIKES_STR;
import static com.loviagin.rollic.Constants.POSTS_STR;
import static com.loviagin.rollic.Constants.POST_UID;
import static com.loviagin.rollic.Constants.SUBSCRIBERS_STR;
import static com.loviagin.rollic.Constants.SUBSCRIPTIONS_STR;
import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.Constants.USER_STR;
import static com.loviagin.rollic.Constants.USER_UID;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.subscribers;
import static com.loviagin.rollic.UserData.subscriptions;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.urlAvatar;
import static com.loviagin.rollic.UserData.username;
import static com.loviagin.rollic.models.Objects.preferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loviagin.rollic.R;
import com.loviagin.rollic.adapters.CommentAdapter;
import com.loviagin.rollic.adapters.PostsAdapter;
import com.loviagin.rollic.models.Comment;
import com.loviagin.rollic.models.Notification;
import com.loviagin.rollic.models.Objects;
import com.loviagin.rollic.models.Post;
import com.loviagin.rollic.models.User;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "Post_Activity_TAG";
    private ImageView imageViewAvatar, imageView1, imageViewSelfAvatar;
    private TextView textViewName, textViewNickname, textViewTitle, textViewDescription;
    private EditText editText;
    private ImageButton buttonSend, buttonSubscribe, buttonBack;
    private Button buttonLike, buttonRepost;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private ProgressBar progressBar;

    private List<Comment> commentsList;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        findViewById(R.id.bCommentPost).setVisibility(View.GONE);
        findViewById(R.id.bDislikePost).setVisibility(View.GONE);
        findViewById(R.id.bMessage).setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.pbPost);
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        imageViewAvatar = findViewById(R.id.ivAvatarRegister);
        imageView1 = findViewById(R.id.ivPhoto1Post);
        imageViewSelfAvatar = findViewById(R.id.ivAvatarComment);
        textViewName = findViewById(R.id.tvNamePost);
        textViewNickname = findViewById(R.id.tvNicknamePost);
        textViewTitle = findViewById(R.id.tvTitlePost);
        textViewDescription = findViewById(R.id.tvDescriptionPost);
        editText = findViewById(R.id.etTextComment);
        buttonLike = findViewById(R.id.bLikePost);
        buttonRepost = findViewById(R.id.bRepost);
        buttonSend = findViewById(R.id.bSendComment);
        recyclerView = findViewById(R.id.rvPost);
        buttonSubscribe = findViewById(R.id.bSubscribePost);
        buttonBack = findViewById(R.id.bNotifications);

        buttonBack.setOnClickListener(v -> finish());
        Intent intent = getIntent();
        Uri data = getIntent().getData();

        if (intent.hasExtra(POST_UID) || data != null) {
            String postUid;
            if (data != null) {
                postUid = data.getQueryParameter("u");
                buttonBack.setOnClickListener(view -> startActivity(new Intent(PostActivity.this, MainActivity.class)));
            } else {
                postUid = intent.getStringExtra(POST_UID);
            }
            if (intent.hasExtra(AVATAR_URL)) {
                uri = Uri.parse(intent.getStringExtra(AVATAR_URL));
                if (uri != null && !uri.equals("")) {
                    Picasso.get().load(uri).into(imageViewAvatar);
                }
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            commentsList = new LinkedList<>();
            adapter = new CommentAdapter(commentsList);
            adapter.setOnCommentClickListener(usrUid -> startActivity(new Intent(PostActivity.this, AccountActivity.class).putExtra(USER_STR, usrUid)));
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);
            db.collection(POSTS_STR).document(postUid).get().addOnSuccessListener(documentSnapshot -> {
                Post post = documentSnapshot.toObject(Post.class);
                buttonSend.setOnClickListener(view -> sendComment(postUid, post));
                if (post != null && !intent.hasExtra(AVATAR_URL) && post.getAuthorAvatarUrl() != null) {
                    uri = Uri.parse(post.getAuthorAvatarUrl());
                    if (uri != null && !uri.equals("")) {
                        Log.e(TAG, postUid + " 3");
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        storageRef.child(post.getAuthorAvatarUrl()).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Picasso.get().load(uri).into(imageViewAvatar);
                                });
                    }
                }
                db.collection(COMMENTS_STR).whereEqualTo("pstUid", postUid).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            commentsList.add(document.toObject(Comment.class));
                            adapter.notifyItemInserted(adapter.getItemCount());
                        }
                    }
                });
                if (post.getTitle().equals("")) { // photo post
                    imageView1.setVisibility(View.VISIBLE);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    if (post.getImagesUrls() != null && post.getImagesUrls().get(0) != null) {
                        storageRef.child(post.getImagesUrls().get(0)).getDownloadUrl()
                                .addOnSuccessListener(uri -> Picasso.get().load(uri).into((imageView1)));
                    }
                    textViewTitle.setVisibility(View.GONE);
                } else { // text post
                    textViewTitle.setText(post.getTitle());
                    textViewTitle.setVisibility(View.VISIBLE);
                    imageView1.setVisibility(View.GONE);
                }

                if (post.getAuthorName() == null || post.getAuthorName().equals("")) {
                    textViewName.setVisibility(View.GONE);
                } else {
                    textViewName.setVisibility(View.VISIBLE);
                    textViewName.setText(post.getAuthorName());
                }
                textViewNickname.setText(post.getAuthorNickname());
                textViewDescription.setText(post.getDescription());
                if (subscriptions == null) {
                    uid = Objects.preferences.getString(USER_UID, "");
                    db.collection(USERS_COLLECTION).document(uid).get().addOnSuccessListener(documentSnapshot1 -> {
                        User u0 = documentSnapshot1.toObject(User.class);
                        subscriptions = u0.getSubscriptions();
                        subscribers = u0.getSubscribers();

                        if (subscriptions != null && subscriptions.contains(post.getUidAuthor()) || post.getUidAuthor().equals(uid)) {
                            buttonSubscribe.setVisibility(View.GONE);
                        } else if (subscriptions != null) {
                            buttonSubscribe.setVisibility(View.VISIBLE);
                            buttonSubscribe.setOnClickListener(v -> {
                                if (!subscriptions.contains(post.getUidAuthor())) {
                                    subscriptions.add(post.getUidAuthor());
                                }
                                FirebaseFirestore db0 = FirebaseFirestore.getInstance();
                                db0.collection(USERS_COLLECTION).document(uid).update(SUBSCRIPTIONS_STR, FieldValue.arrayUnion(post.getUidAuthor()));
                                db0.collection(USERS_COLLECTION).document(post.getUidAuthor()).update(SUBSCRIBERS_STR, FieldValue.arrayUnion(uid));
                                buttonSubscribe.setVisibility(View.GONE);
                            });
                        }
                    });
                }
                if (subscriptions != null && subscriptions.contains(post.getUidAuthor()) || post.getUidAuthor().equals(uid)) {
                    buttonSubscribe.setVisibility(View.GONE);
                } else if (subscriptions != null) {
                    buttonSubscribe.setVisibility(View.VISIBLE);
                    buttonSubscribe.setOnClickListener(v -> {
                        if (!subscriptions.contains(post.getUidAuthor())) {
                            subscriptions.add(post.getUidAuthor());
                        }
                        FirebaseFirestore db0 = FirebaseFirestore.getInstance();
                        db0.collection(USERS_COLLECTION).document(uid).update(SUBSCRIPTIONS_STR, FieldValue.arrayUnion(post.getUidAuthor()));
                        db0.collection(USERS_COLLECTION).document(post.getUidAuthor()).update(SUBSCRIBERS_STR, FieldValue.arrayUnion(uid));
                        buttonSubscribe.setVisibility(View.GONE);
                    });
                }
                buttonLike.setText(String.valueOf(post.getLikes().size()));
//                buttonRepost.setText(String.valueOf(post.getRepostCount()));

                setColorToButton(post);

                buttonLike.setOnClickListener(v -> {
                    if (post.getLikes().contains(uid)) {
                        post.deleteLike(uid);
                        counter(post);
                        FirebaseFirestore db0 = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db0.collection(POSTS_STR).document(post.getUid());
                        docRef.update(LIKES_STR, FieldValue.arrayRemove(uid));
                    } else {
                        post.addLike(uid);
                        counter(post);
                        FirebaseFirestore db0 = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db0.collection(POSTS_STR).document(post.getUid());
                        docRef.update(LIKES_STR, FieldValue.arrayUnion(uid));
                    }
                });

                buttonRepost.setOnClickListener(view -> shareContent("Посмотри мой новый пост в Роллик", "https://rollic.loviagin.com/ulink?u=" + post.getUid()));

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });

            buttonBack.setImageDrawable(getResources().getDrawable(R.drawable.fi_rr_back));

            if (urlAvatar != null && !urlAvatar.equals("")) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                storageRef.child(urlAvatar).getDownloadUrl().addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(imageViewSelfAvatar);
                });
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    private void shareContent(String title, String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(shareIntent, "Поделиться постом"));
    }

    private void sendComment(String pstUid, Post post) {
        if (editText.getText() != null && !editText.getText().toString().trim().equals("")) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Comment cmnt = new Comment(name, username, urlAvatar, editText.getText().toString().trim(), pstUid, uid);
            db.collection(COMMENTS_STR).add(cmnt).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    db.collection(POSTS_STR).document(pstUid).update(COMMENTS_STR, FieldValue.arrayUnion(documentReference.getId())).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            editText.setText("");
                            adapter.addComment(cmnt);
                            adapter.notifyItemInserted(adapter.getItemCount());
                        }
                    });
                }
            });
            db.collection(USERS_COLLECTION).document(post.getUidAuthor()).get().addOnSuccessListener(documentSnapshot -> {
                List<String> devices = (List<String>) documentSnapshot.get("deviceTokens");
                Log.e(TAG, devices + "DVC");
                if (devices != null && devices.size() > 0) {
                    try {
                        for (String str : devices) {
                            JSONObject notificationContent = new JSONObject(
                                    "{'contents': {'en':'Новый комментарий от @" + username + "'}, " +
                                            "'include_player_ids': ['" + str + "'], " +
                                            "'data': {'activityToBeOpened': 'NotificationActivity'}}"
                            );
                            OneSignal.postNotification(notificationContent, null);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            String i;
            if (editText.getText().toString().trim().length() > 10) {
                i = editText.getText().toString().trim().substring(0, 10) + "...";
            } else {
                i = editText.getText().toString().trim();
            }
            db.collection("notifications").add(new
                            Notification("Новый комментарий от @" + username, i, urlAvatar, "c/" + pstUid))
                    .addOnSuccessListener(documentReference ->
                            db.collection(USERS_COLLECTION).document(post.getUidAuthor()).update("notifications", FieldValue.arrayUnion(documentReference.getId())));

        } else {
            Toast.makeText(this, "Комментарий не может быть пустым", Toast.LENGTH_SHORT).show();
        }
    }

    private void counter(Post post) {
        setColorToButton(post);
        buttonLike.setText(String.valueOf(post.getLikes().size()));
        buttonRepost.setText(String.valueOf(post.getRepostCount()));
    }

    private void setColorToButton(Post post) {
        if (post.getLikes().contains(uid)) {
            buttonLike.setBackgroundColor(Color.parseColor("#fba6f5"));
        } else {
            buttonLike.setBackgroundColor(Color.parseColor("#507178fd"));
        }
    }

}