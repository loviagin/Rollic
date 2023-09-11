package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.AVATAR_URL;
import static com.loviagin.rollic.Constants.MESSAGES_STR;
import static com.loviagin.rollic.Constants.USER_UID;
import static com.loviagin.rollic.UserData.self_messages;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.models.Objects.preferences;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.loviagin.rollic.R;
import com.loviagin.rollic.activities.pro.ProActivity;
import com.loviagin.rollic.adapters.ChatAdapter;
import com.loviagin.rollic.helpers.DBHelper;
import com.loviagin.rollic.models.ChatItem;
import com.loviagin.rollic.models.User;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "Chat_Activity_TAG";
    private ImageButton buttonBack;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private static ChatAdapter adapter;
    private static List<ChatItem> chatItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        findViewById(R.id.bMessage).setVisibility(View.GONE);
        buttonBack = findViewById(R.id.bNotifications);
        recyclerView = findViewById(R.id.rvChat);
        searchView = findViewById(R.id.svSearchChat);
        chatItems = new LinkedList<>();

        buttonBack.setImageDrawable(getResources().getDrawable(R.drawable.fi_rr_back));
        buttonBack.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        List<String> documentIds = self_messages.stream()
                .flatMap(map -> map.values().stream())
                .collect(Collectors.toList());

        adapter = new ChatAdapter(chatItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter.setOnChatItemCLickListener((cUid, name, uri) -> startActivity(new Intent(ChatActivity.this, MessageActivity.class)
                .putExtra(USER_UID, cUid).putExtra("FIRST_NAME", name).putExtra(AVATAR_URL, uri)));
        recyclerView.setAdapter(adapter);
        if (!documentIds.isEmpty()) {
            updateStore(documentIds);
        }
        threadStart.start();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Здесь вы можете обрабатывать действие, когда пользователь нажимает Enter
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Этот метод вызывается каждый раз, когда текст в SearchView меняется
                // Мы будем использовать его для фильтрации нашего списка

                List<ChatItem> filteredList = new LinkedList<>();
                for (ChatItem item : chatItems) {
                    if (item.getName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(item);
                    }
                }

                // Обновляем адаптер с отфильтрованным списком
                adapter.updateList(filteredList);
                return false;
            }
        });
    }

    private void updateStore(List<String> documentIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("messages").whereIn(FieldPath.documentId(), documentIds).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "New doc: " + dc.getDocument().getData());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DBHelper dbHelper = new DBHelper(ChatActivity.this);
                                ContentValues values = new ContentValues();
                                SQLiteDatabase sqliteDb = dbHelper.getWritableDatabase();
                                List<String> sts = (List<String>) dc.getDocument().get("uids");
                                String boble;
                                if (sts.get(0).equals(uid)) {
                                    boble = sts.get(1);
                                } else {
                                    boble = sts.get(0);
                                }

                                if (!dbHelper.isUserExists(boble)) { // new item to table
                                    DocumentReference docRef = db.collection("messages").document(dc.getDocument().getId());
                                    docRef.get().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                List<Map<String, Object>> messagesArray = (List<Map<String, Object>>) document.get("messages");

                                                // Поиск сообщения с наибольшим значением времени
                                                Map<String, Object> latestMessage = null;
                                                for (Map<String, Object> message : messagesArray) {
                                                    if (latestMessage == null || (Long) message.get("time") > (Long) latestMessage.get("time")) {
                                                        latestMessage = message;
                                                    }
                                                }
                                                String message = (String) latestMessage.get("message");
                                                // обработка сообщения
                                                // обработка сообщения
                                                db.collection("users").document(boble).get().addOnSuccessListener(documentSnapshot -> {
                                                    User u1 = documentSnapshot.toObject(User.class);
                                                    values.put(DBHelper.COLUMN_NAME, u1.getF_name());
                                                    values.put(DBHelper.COLUMN_UID, boble);
                                                    values.put(DBHelper.COLUMN_MESSAGE, message);
                                                    values.put(DBHelper.COLUMN_AVATAR_URL, u1.getAvatarUrl());
                                                    values.put(DBHelper.COLUMN_TIME, dc.getDocument().getLong("time"));

                                                    preferences.edit().putString(boble, message).apply();
                                                    long newRowId = sqliteDb.insert(DBHelper.TABLE_NAME, null, values);
                                                });
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    });
                                } else {
                                    Long newDocTime = dc.getDocument().getLong("time");
                                    String[] projection = {DBHelper.COLUMN_TIME};
                                    String selection = DBHelper.COLUMN_UID + " = ?";
                                    String[] selectionArgs = {dc.getDocument().getId()};

                                    Cursor cursor = sqliteDb.query(
                                            DBHelper.TABLE_NAME,
                                            projection,
                                            selection,
                                            selectionArgs,
                                            null,
                                            null,
                                            null
                                    );

                                    if (cursor.moveToFirst()) {
                                        int currentTime = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TIME));

                                        // Сравниваем время
                                        if (newDocTime > currentTime) {
                                            DocumentReference docRef = db.collection(MESSAGES_STR).document(dc.getDocument().getId());
                                            docRef.get().addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        List<Map<String, Object>> messagesArray = (List<Map<String, Object>>) document.get("messages");

                                                        // Поиск сообщения с наибольшим значением времени
                                                        Map<String, Object> latestMessage = null;
                                                        for (Map<String, Object> message : messagesArray) {
                                                            if (latestMessage == null || (Long) message.get("time") > (Long) latestMessage.get("time")) {
                                                                latestMessage = message;
                                                            }
                                                        }
                                                        String message = (String) latestMessage.get("message");
                                                        values.put(DBHelper.COLUMN_MESSAGE, message);
                                                        values.put(DBHelper.COLUMN_TIME, dc.getDocument().getLong("time"));

                                                        String selection2 = DBHelper.COLUMN_UID + " = ?";
                                                        String[] selectionArgs2 = {boble}; // ID строки, которую нужно обновить

                                                        int count = sqliteDb.update(
                                                                DBHelper.TABLE_NAME,
                                                                values,
                                                                selection2,
                                                                selectionArgs2);
                                                    }
                                                }

                                            });
                                        }
                                    }
                                    cursor.close();
                                }
                            }
                        }).start();
                    }
                }
            }
        });

    }

    Thread threadStart = new Thread(new Runnable() {
        @Override
        public void run() {
            DBHelper dbHelper = new DBHelper(ChatActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String[] projection = {
                    DBHelper.COLUMN_ID,
                    DBHelper.COLUMN_UID,
                    DBHelper.COLUMN_NAME,
                    DBHelper.COLUMN_MESSAGE,
                    DBHelper.COLUMN_AVATAR_URL,
                    DBHelper.COLUMN_TIME
            };

            String sortOrder = DBHelper.COLUMN_TIME + " DESC"; // Или "ASC" для сортировки от меньшего к большему

            Cursor cursor = db.query(
                    DBHelper.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME));
                String cUser = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_UID));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_MESSAGE));
                String avatarUrl = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_AVATAR_URL));
                int time = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TIME));
                chatItems.add(new ChatItem(cUser, name, avatarUrl, message, time));
                adapter.notifyItemChanged(chatItems.size());
            }
            cursor.close();
        }
    });
}