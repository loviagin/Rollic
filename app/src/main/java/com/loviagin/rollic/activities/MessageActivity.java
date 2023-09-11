package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.AVATAR_URL;
import static com.loviagin.rollic.Constants.MESSAGES_STR;
import static com.loviagin.rollic.Constants.USER_UID;
import static com.loviagin.rollic.UserData.self_messages;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.urlAvatar;
import static com.loviagin.rollic.UserData.username;
import static com.loviagin.rollic.models.Objects.currentUser;
import static com.loviagin.rollic.models.Objects.preferences;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loviagin.rollic.R;
import com.loviagin.rollic.adapters.MessageAdapter;
import com.loviagin.rollic.helpers.DBHelper;
import com.loviagin.rollic.models.Chat;
import com.loviagin.rollic.models.Message;
import com.loviagin.rollic.models.Notification;
import com.loviagin.rollic.models.User;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MessageActivity extends AppCompatActivity {

    public static final String TAG = "Message_Activity_TAG";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1022;
    //    private static final String SERVER_URL = "https://us-central1-prefab-pixel-390617.cloudfunctions.net/sendNotification"; // NOTIFICATIONS
    private static final int MEDIA_REQUEST_CODE = 76578576;
    private ImageButton buttonBack, buttonMore, buttonSend, buttonMedia;
    private EditText editText;
    private ShapeableImageView imageView;
    private TextView textViewName;
    private ProgressBar progressBar;
    private MessageAdapter adapter;
    private RecyclerView recyclerView;
    private String cUser;
    private List<Message> messages;
    private String value = "";
    private boolean containsKey;
    private String nm;
    private Uri[] img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        progressBar = findViewById(R.id.pbMessage);
        progress(false);

        buttonBack = findViewById(R.id.bBackMessage);
//        buttonMore = findViewById(R.id.bMenuMessage);
        textViewName = findViewById(R.id.tvNameMessage);
        imageView = findViewById(R.id.ivAvatarMessage);
        recyclerView = findViewById(R.id.rvMessage);
        buttonSend = findViewById(R.id.ibSendMessage);
        editText = findViewById(R.id.etTextMessage);
        buttonMedia = findViewById(R.id.ibMediaMessage);

        buttonBack.setOnClickListener(view -> startActivity(new Intent(this, ChatActivity.class)));
//        buttonMore.setOnClickListener(this::showPopupMenu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        View.OnClickListener listener = view -> startActivity(new Intent(MessageActivity.this, AccountActivity.class).putExtra(USER_UID, cUser));
        imageView.setOnClickListener(listener);
        textViewName.setOnClickListener(listener);
        buttonMedia.setOnClickListener(view -> media());
        messages = new LinkedList<>();

        Intent intent = getIntent();
        FirebaseMessaging.getInstance().subscribeToTopic("notifications");

        if (intent.hasExtra(USER_UID)) {
            cUser = intent.getStringExtra(USER_UID);
            img = new Uri[1];
            if (intent.hasExtra("FIRST_NAME") && intent.hasExtra(AVATAR_URL)) {
                nm = intent.getStringExtra("FIRST_NAME");
                img[0] = Uri.parse(intent.getStringExtra(AVATAR_URL));
                textViewName.setText(nm);
                if (img[0] != null) {
                    Picasso.get().load(img[0]).into(imageView);
                }
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(cUser).get().addOnSuccessListener(documentSnapshot -> {
                    User u = documentSnapshot.toObject(User.class);
                    nm = u.getF_name();
                    textViewName.setText(nm);
                    if (u.getAvatarUrl() != null && !u.getAvatarUrl().equals("")) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference avatarRef = storageRef.child(u.getAvatarUrl());
                        avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            img[0] = uri;
                            Picasso.get().load(uri).into(imageView);
                        });
                    }
                });
            }
            containsKey = self_messages.stream().anyMatch(map -> map.containsKey(cUser));
            if (containsKey) {
                for (Map<String, String> map : self_messages) {
                    if (map.containsKey(cUser)) {
                        value = map.get(cUser);
                        break;
                    }
                }
                Log.d(TAG, value);
                continueChat();
            }
            adapter = new MessageAdapter(messages);
            recyclerView.setAdapter(adapter);
            buttonSend.setOnClickListener(view -> sendMessage());
            progress(true);
        } else {
            Toast.makeText(this, "We're sorry. The internet connection is lost", Toast.LENGTH_SHORT).show();
        }
    }

    private void media() {
        Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageIntent.setType("image/*");
        Intent videoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        videoIntent.setType("video/*");

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, imageIntent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Select Image or Video");

        Intent[] intentArray = {videoIntent};
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooser, MEDIA_REQUEST_CODE);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        startActivityForResult(Intent.createChooser(intent, "Select Image"), MEDIA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MEDIA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri contentUri = data.getData();
            String type = getContentResolver().getType(contentUri);

            if (type.startsWith("image/")) {
                Uri mediaUri = data.getData();
                uploadFileToFirebase(mediaUri);
            } else if (type.startsWith("video/")) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(this, contentUri);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time);
                if (timeInMillisec <= 15 * 1000) { // 15 seconds
                    // allow upload
                    sendVideoMessage(contentUri);
                } else {
                    Toast.makeText(this, "Подключайтесь к ПРО подписке, чтобы загружать длинные видео", Toast.LENGTH_SHORT).show();
                    // show error message to user
                }
            }
        }
    }

    private void sendVideoMessage(Uri contentUri) {
        uploadVideoToFirebase(contentUri);
    }

    private void uploadVideoToFirebase(Uri videoUri) {
        if (videoUri != null) {
            // Получите ссылку на Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            // Укажите, куда сохранить видео, например в "videos/{filename}.mp4"
            final StorageReference videoRef = storageReference.child("videos/" + System.currentTimeMillis() + ".mp4");

            // Загрузите видео
            videoRef.putFile(videoUri)
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress((int) progress);
                    })
                    .addOnSuccessListener(taskSnapshot -> {
                        videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String videoUrl = uri.toString();
                            progressBar.setVisibility(View.GONE);
                            sendMessageWithVideo(videoUrl);
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(MessageActivity.this, "Upload failed", Toast.LENGTH_SHORT).show());
        }
    }

    private void sendMessageWithVideo(String videoUrl) {
        // Сообщение должно содержать ссылку на файл и другую необходимую информацию
        Message m1 = new Message(videoUrl);
        m1.setText("VIDEO");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(MESSAGES_STR).document(value).update(MESSAGES_STR, FieldValue.arrayUnion(m1));
        Instant currentTime = Instant.now();
        db.collection(MESSAGES_STR).document(value).update("time", currentTime.toEpochMilli());
        messages.add(m1);
        databaseMedia("video");
        preferences.edit().putString(cUser, "You: [video]").apply();
        adapter.notifyItemInserted(messages.size());
        recyclerView.smoothScrollToPosition(messages.size());
        showNotificationPermissionDialog("Send media");
    }


    private void uploadFileToFirebase(Uri fileUri) {
        Uri compressedUri = compressImage(fileUri);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference imageRef = storageReference.child("images/" + System.currentTimeMillis() + ".jpeg");
        if (compressedUri != null) {
            imageRef.putFile(compressedUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String mediaUrl = uri.toString();
                                sendMessageWithMedia(mediaUrl);
                            })
                            .addOnFailureListener(e -> Toast.makeText(MessageActivity.this, "Upload failed", Toast.LENGTH_SHORT).show()));
        } else {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference fileRef = storageRef.child("media/" + UUID.randomUUID().toString());
            UploadTask uploadTask = fileRef.putFile(fileUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String mediaUrl = uri.toString();
                sendMessageWithMedia(mediaUrl);
            })).addOnFailureListener(e -> Toast.makeText(MessageActivity.this, "Upload failed", Toast.LENGTH_SHORT).show());
        }
    }

    private void sendMessageWithMedia(String mediaUrl) {
        // Сообщение должно содержать ссылку на файл и другую необходимую информацию
        Message m1 = new Message(mediaUrl);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(MESSAGES_STR).document(value).update(MESSAGES_STR, FieldValue.arrayUnion(m1));
        Instant currentTime = Instant.now();
        db.collection(MESSAGES_STR).document(value).update("time", currentTime.toEpochMilli());
        messages.add(m1);
        databaseMedia("picture");
        preferences.edit().putString(cUser, "You: [picture]").apply();
        adapter.notifyItemInserted(messages.size());
        recyclerView.smoothScrollToPosition(messages.size());
        showNotificationPermissionDialog("Send media");
    }


    private void continueChat() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection(MESSAGES_STR).document(value);

        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                Chat chat = snapshot.toObject(Chat.class);
                for (Message m : chat.getMessages()) {
                    addItem(m);
                }
                Log.d(TAG, "Current data: " + snapshot.getData());
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
    }

    private Uri compressImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            // Устанавливаем качество сжатия. 100 означает без потерь, уменьшайте для большего сжатия
            int compressionQuality = 80;

            // Создайте временный файл для хранения сжатого изображения
            File tempFile = File.createTempFile("compressed", ".jpeg", getCacheDir());
            FileOutputStream out = new FileOutputStream(tempFile);

            // Сжимаем изображение
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, out);

            out.flush();
            out.close();

            // Возвращаем Uri сжатого изображения
            return Uri.fromFile(tempFile);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void addItem(Message item) {
        if (messages.contains(item)) {
            return;
        }
        // Используем бинарный поиск для поиска позиции вставки элемента
        int position = Collections.binarySearch(messages, item, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return Long.compare(o1.getTime(), o2.getTime());
            }
        });

        // Если результат поиска отрицательный, то элемент не найден
        // Меняем знак результата поиска для получения правильной позиции вставки
        if (position < 0) {
            position = -(position + 1);
        }

        // Вставляем элемент в найденную позицию
        messages.add(position, item);
        adapter.notifyItemInserted(messages.size());
        recyclerView.smoothScrollToPosition(messages.size());
    }

    private void newChat() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        value = uid + cUser;
        Map<String, String> map = new HashMap<>();
        map.put(cUser, value);
        self_messages.add(map);
        db.collection("users").document(uid).update(MESSAGES_STR, FieldValue.arrayUnion(map));
        Map<String, String> map0 = new HashMap<>();
        map0.put(uid, value);
        db.collection("users").document(cUser).update(MESSAGES_STR, FieldValue.arrayUnion(map0));
        List<String> strings = new ArrayList<>();
        strings.add(uid);
        strings.add(cUser);
        db.collection(MESSAGES_STR).document(value).set(new Chat(strings, new LinkedList<>()));
        continueChat();
    }

    private void sendMessage() {
        if (editText.getText() != null && editText.getText().toString().trim().length() > 0) {
            if (!containsKey) { // new chat
                newChat();
            }
            String k = editText.getText().toString().trim();
            Message m1 = new Message(k, uid);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.d(TAG, value);
            db.collection(MESSAGES_STR).document(value).update(MESSAGES_STR, FieldValue.arrayUnion(m1));
            Instant currentTime = Instant.now();
            db.collection(MESSAGES_STR).document(value).update("time", currentTime.toEpochMilli());
            database();
            messages.add(m1);
            preferences.edit().putString(cUser, String.format("You: %s", k)).apply();
            editText.setText("");
            adapter.notifyItemInserted(messages.size());
            recyclerView.smoothScrollToPosition(messages.size());
            showNotificationPermissionDialog(k);
        } else {
            Toast.makeText(this, "Message must contains at least 1 symbol", Toast.LENGTH_SHORT).show();
        }
    }

    private void database() {
        DBHelper dbHelper = new DBHelper(this);
        Instant currentTime = Instant.now();

        // Для записи данных в базу данных вы используете объект SQLiteDatabase
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (!dbHelper.isUserExists(cUser)) {
            values.put(DBHelper.COLUMN_NAME, nm);
            values.put(DBHelper.COLUMN_UID, cUser);
            values.put(DBHelper.COLUMN_MESSAGE, editText.getText().toString().trim());
            values.put(DBHelper.COLUMN_AVATAR_URL, String.valueOf(img[0]));
            values.put(DBHelper.COLUMN_TIME, currentTime.toEpochMilli());

            long newRowId = db.insert(DBHelper.TABLE_NAME, null, values);
        } else {
            // Пользователь с таким именем уже существует
            values.put(DBHelper.COLUMN_MESSAGE, editText.getText().toString().trim());
            values.put(DBHelper.COLUMN_AVATAR_URL, String.valueOf(img[0]));
            values.put(DBHelper.COLUMN_TIME, currentTime.toEpochMilli());
            values.put(DBHelper.COLUMN_NAME, nm);

            String selection = DBHelper.COLUMN_UID + " = ?";
            String[] selectionArgs = {cUser}; // ID строки, которую нужно обновить

            int count = db.update(
                    DBHelper.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
    }

    private void databaseMedia(String str) {
        DBHelper dbHelper = new DBHelper(this);
        Instant currentTime = Instant.now();

        // Для записи данных в базу данных вы используете объект SQLiteDatabase
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (!dbHelper.isUserExists(cUser)) {
            values.put(DBHelper.COLUMN_NAME, nm);
            values.put(DBHelper.COLUMN_UID, cUser);
            values.put(DBHelper.COLUMN_MESSAGE, str);
            values.put(DBHelper.COLUMN_AVATAR_URL, String.valueOf(img[0]));
            values.put(DBHelper.COLUMN_TIME, currentTime.toEpochMilli());

            long newRowId = db.insert(DBHelper.TABLE_NAME, null, values);
        } else {
            // Пользователь с таким именем уже существует
            values.put(DBHelper.COLUMN_MESSAGE, str);
            values.put(DBHelper.COLUMN_AVATAR_URL, String.valueOf(img[0]));
            values.put(DBHelper.COLUMN_TIME, currentTime.toEpochMilli());
            values.put(DBHelper.COLUMN_NAME, nm);

            String selection = DBHelper.COLUMN_UID + " = ?";
            String[] selectionArgs = {cUser}; // ID строки, которую нужно обновить

            int count = db.update(
                    DBHelper.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
    }

    private void showNotificationPermissionDialog(String textMessage) {
        if (!NotificationManagerCompat.from(MessageActivity.this).areNotificationsEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
            builder.setTitle("Разрешить уведомления");
            builder.setMessage("Они позволять узнавать о новых лайках, подписках и комментах");
            builder.setPositiveButton("Да", (dialog, which) -> requestNotificationPermission());
            builder.setNegativeButton("Нет", null);
            builder.show();
        } else {
            String title = "Новое сообщение от " + nm;
            String body = textMessage;
            String data = "m/" + uid;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(cUser).get().addOnSuccessListener(documentSnapshot -> {
                User u0 = documentSnapshot.toObject(User.class);
                Log.e(TAG, "First");
                addToBase(title, body, data);
                for (String sToken : u0.getDeviceTokens()) {
                    if (sToken != null && !sToken.equals("")) {
                        Log.e(TAG, "Device " + sToken);
                        JSONObject notificationContent = null;
                        try {
                            notificationContent = new JSONObject(
                                    "{'contents': {'en':'Новое сообщение от @" + username + "'}, " +
                                            "'include_player_ids': ['" + sToken + "'], " +
                                            "'data': {'activityToBeOpened': 'NotificationActivity'}}"
                            );
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        OneSignal.postNotification(notificationContent, null);
                    }
                }
            });
        }
    }

//    private void sendNotification(String title, String body, String token, String data) {
//        // Создаем JSON-объект с данными уведомления
//        JSONObject notification = new JSONObject();
//        try {
//            notification.put("title", title);
//            notification.put("body", body);
//            notification.put("token", token);
//            notification.put("uid", data);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SERVER_URL, notification, response -> Log.e(TAG, "Third"), error -> Log.e(TAG, error.toString()));
//
//        Volley.newRequestQueue(getApplicationContext()).add(request);
//    }

    private void addToBase(String title, String body, String data) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notifications").add(new Notification(title, body, urlAvatar, data)).addOnSuccessListener(documentReference -> {
            Log.e(TAG, "Second");
            db.collection("users").document(cUser).update("notifications", FieldValue.arrayUnion(documentReference.getId()));
        });
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE);
        }
    }

//    private void showPopupMenu(View view) {
//        PopupMenu popupMenu = new PopupMenu(this, view);
//        popupMenu.inflate(R.menu.message_menu);
//        popupMenu.setOnMenuItemClickListener(item -> {
//            if (item.getItemId() == R.id.iProfile) {
//                startActivity(new Intent(MessageActivity.this, ProfileActivity.class).putExtra(USER_UID, cUser));
//                return true;
//            } else if (item.getItemId() == R.id.iReportChat) {
//                return true;
//            } else if (item.getItemId() == R.id.iBlockChat) {
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                if (blockedUsers.contains(cUser)) {
//                    item.setTitle("Unblock user");
//                    blockedUsers.remove(cUser);
//                    db.collection(USERS_STR).document(uid).update("blockedUsers", FieldValue.arrayRemove(cUser));
//                } else {
//                    blockedUsers.add(cUser);
//                    db.collection(USERS_STR).document(uid).update("blockedUsers", FieldValue.arrayUnion(cUser));
//                }
//                return true;
//            }
//            else if (item.getItemId() == R.id.iDeleteChat) {
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                Map<String, String> map = new HashMap<>();
//                map.put(cUser, value);
//                db.collection(USERS_STR).document(uid).update(MESSAGES_STR, FieldValue.arrayRemove(map));
//                Map<String, String> map0 = new HashMap<>();
//                map0.put(uid, value);
//                db.collection(USERS_STR).document(cUser).update(MESSAGES_STR, FieldValue.arrayRemove(map0));
//                db.collection(MESSAGES_STR).document(value).delete();
//                self_messages.remove(map);
//                finish();
//                return true;
//            }
//            else {
//                return false;
//            }
//        });
//        popupMenu.show();
//    }

    private void progress(boolean isLoad) {
        if (isLoad) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.GONE);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}