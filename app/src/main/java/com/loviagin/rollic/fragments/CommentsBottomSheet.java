package com.loviagin.rollic.fragments;

import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.Constants.USER_STR;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.urlAvatar;
import static com.loviagin.rollic.UserData.username;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.loviagin.rollic.R;
import com.loviagin.rollic.UserData;
import com.loviagin.rollic.activities.AccountActivity;
import com.loviagin.rollic.activities.PostActivity;
import com.loviagin.rollic.adapters.CommentAdapter;
import com.loviagin.rollic.models.Comment;
import com.loviagin.rollic.models.Notification;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class CommentsBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton buttonSend;
    private CommentAdapter adapter;
    private List<Comment> comments;
    private String cUid;
    private String cUser;

    public CommentsBottomSheet(String uid, String cUser) {
        cUid = uid;
        this.cUser = cUser;
    }

    public CommentsBottomSheet() {
        onCancel(getDialog());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comment_video_layout, container, false);
        recyclerView = view.findViewById(R.id.rvVideoItem);
        editText = view.findViewById(R.id.etCommentVideo);
        buttonSend = view.findViewById(R.id.ibSendCommentVideo);

        comments = new LinkedList<>();
        adapter = new CommentAdapter(comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter.setOnCommentClickListener(usrUid -> startActivity(new Intent(getContext(), AccountActivity.class).putExtra(USER_STR, usrUid)));
        recyclerView.setAdapter(adapter);
        thread.start();

        buttonSend.setOnClickListener(view1 -> {
            if (editText.getText() != null && !editText.getText().toString().trim().equals("")) {
                sendComment();
            } else {
                Toast.makeText(view1.getContext(), "Комментарий не может быть пустым", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void sendComment() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Comment comment = new Comment(name, username, urlAvatar, editText.getText().toString().trim(), cUid, uid);
        db.collection("video-comments").add(comment).addOnSuccessListener(documentReference -> db.collection("video-posts").document(cUid).update("comments", FieldValue.arrayUnion(documentReference.getId())));
        adapter.addComment(0, comment);
        db.collection(USERS_COLLECTION).document(cUser).get().addOnSuccessListener(documentSnapshot -> {
            List<String> devices = (List<String>) documentSnapshot.get("deviceTokens");
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
                        Notification("Новый комментарий от @" + username, i, urlAvatar, "v/" + cUid))
                .addOnSuccessListener(documentReference ->
                        db.collection(USERS_COLLECTION).document(cUser).update("notifications", FieldValue.arrayUnion(documentReference.getId())));

        editText.setText("");
        adapter.notifyItemInserted(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        View bottomSheet = (View) getView().getParent();
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels * 3 / 4); // Устанавливаем желаемую высоту, например, 2/3 экрана
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("video-comments").whereEqualTo("pstUid", cUid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        Comment comment = documentSnapshot.toObject(Comment.class);
                        comment.setUsrNickname("@" + comment.getUsrNickname());
                        adapter.addComment(0, comment);
                        adapter.notifyItemInserted(0);
                    }
                }
            });
        }
    });
}
