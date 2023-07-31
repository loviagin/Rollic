package com.loviagin.rollic.workers;

import static com.loviagin.rollic.models.Objects.preferences;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loviagin.rollic.R;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadVideoWorker extends Worker {
    public UploadVideoWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String videoUri = getInputData().getString("video_uri");
        String uid = getInputData().getString("uid");
        String description = getInputData().getString("description");
        String tags = getInputData().getString("tags");

        String videoPath = "videos/" + uid + "/" + videoUri;
        StorageReference videoRef = storageRef.child(videoPath);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getApplicationContext(), Uri.parse(videoUri));  // videoUri - это URI вашего видео
        Bitmap bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST); // получить кадр в 1 секунду
        try {
            retriever.release();  // не забывайте освободить ресурсы
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // получить NotificationManagerNotificationManager
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // для версий Android Oreo и выше требуется канал уведомлений
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("uploadChannel", "Upload", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        // создаем уведомление
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "uploadChannel").setContentTitle("Загрузка видео")
                .setContentText("Загрузка в процессе. Не закрывайте приложение").setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW).setOngoing(true)
                .setOnlyAlertOnce(true).setProgress(100, 0, false);
        notificationManager.notify(1, notification.build());
        // теперь, когда вы загружаете файл, вы можете обновить прогресс в уведомлении
        UploadTask uploadTask = videoRef.putFile(Uri.parse(videoUri));
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            notification.setProgress(100, (int) progress, false);
            notificationManager.notify(1, notification.build());
        });
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            notification.setContentText("Загрузка завершена")
                    .setProgress(0, 0, false).setOngoing(false);
            notificationManager.notify(1, notification.build());

            // Создание файла в каталоге кэша приложения
            File thumbnailFile = new File(getApplicationContext().getCacheDir(), "thumb" + System.currentTimeMillis() + ".jpg");
            // преобразование bitmap в файл для загрузки в Firebase
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(thumbnailFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // загрузка в Firebase
            StorageReference thumbnailRef = storageRef.child("thumbnails/" + uid + "/" + thumbnailFile.getName());
            UploadTask uploadThumbnailTask = thumbnailRef.putFile(Uri.fromFile(thumbnailFile));

            Map<String, Object> videoData = new HashMap<>();
            videoData.put("description", description);
            videoData.put("tags", tags);
            videoData.put("uri", videoPath);

            uploadThumbnailTask.addOnFailureListener(exception -> {
                Toast.makeText(getApplicationContext(), "Возникли проблемы с нашей стороны. Возможно стоит попробовать попозже", Toast.LENGTH_SHORT).show();// Handle unsuccessful uploads
            }).addOnSuccessListener(taskSnapshot1 -> {    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                thumbnailRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String thumbnailUrl = uri.toString();
                    // добавление thumbnailUrl в videoData
                    videoData.put("captureUrl", thumbnailUrl);        // сохранение videoData в базе данных
                });
            });

            db.collection("video-posts").add(videoData).addOnSuccessListener(documentReference -> {
                db.collection("users").document(uid).update("videoposts", FieldValue.arrayUnion(documentReference.getId()));
                db.collection("video-posts").document(documentReference.getId()).update("uid", documentReference.getId());

                try {
                    if (!preferences.getString("player", "").equals("")) {
                        OneSignal.postNotification(new JSONObject("{'contents': {'en':'Video upload complete'}, 'include_player_ids': ['"+ preferences.getString("player", "") +"']}"), null);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        uploadTask.addOnFailureListener(taskSnapshot -> {
            notification.setContentText("Ошибка загрузки").setProgress(0, 0, false)
                    .setOngoing(false);
            notificationManager.notify(1, notification.build());
        });

        return Result.success();
    }
}