package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.AVATAR_URL;
import static com.loviagin.rollic.Constants.NICKNAME;
import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.Constants.USER_NAME;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.urlAvatar;
import static com.loviagin.rollic.UserData.username;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loviagin.rollic.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegisterScreenActivity extends AppCompatActivity {

    private Button buttonUpload, buttonSkip, buttonSave;
    private EditText editTextName, editTextNickname;
    private ImageView imageViewAvatar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        findViewById(R.id.bNotifications).setVisibility(View.GONE);
        findViewById(R.id.bMessage).setVisibility(View.GONE);
        buttonSkip = findViewById(R.id.bSkipRegister);
        buttonSave = findViewById(R.id.bSaveRegister);
        buttonUpload = findViewById(R.id.bUploadAvatarRegister);
        editTextName = findViewById(R.id.etNameRegister);
        editTextNickname = findViewById(R.id.etNicknameRegister);
        editTextNickname.setText(username);
        editTextName.setText(name);
        imageViewAvatar = findViewById(R.id.ivAvatarRegisterScreen);
        progressBar = findViewById(R.id.pbRegisterScreen);

        imageViewAvatar.setOnClickListener(v -> openImagePicker());
        buttonUpload.setOnClickListener(v -> openImagePicker());

        buttonSkip.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            db.collection(USERS_COLLECTION).document(uid).update(USER_NAME, username).addOnSuccessListener(unused -> {
                name = username;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(RegisterScreenActivity.this, MainActivity.class));
            });
        });

        buttonSave.setOnClickListener(v -> {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            String iva = "avatars/" + uid + System.currentTimeMillis() + ".jpg";
            StorageReference imagesRef = storageRef.child(iva);
            // Get the data from an ImageView as bytes
            imageViewAvatar.setDrawingCacheEnabled(true);
            imageViewAvatar.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageViewAvatar.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imagesRef.putBytes(data);
            uploadTask.addOnFailureListener(exception -> {
                // Handle unsuccessful uploads
            }).addOnSuccessListener(taskSnapshot -> {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            });
            String name0 = editTextName.getText() == null ? username : editTextName.getText().toString().trim();
            String nickname = editTextNickname.getText() == null ? username : editTextNickname.getText().toString().trim();
            db.collection(USERS_COLLECTION).document(uid).update(USER_NAME, name0, NICKNAME, nickname, AVATAR_URL, iva).addOnSuccessListener(unused -> {
                name = name0;
                username = nickname;
                urlAvatar = iva;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(RegisterScreenActivity.this, MainActivity.class));
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri croppedImageUri = result.getUri();
                // Конвертирование в формат JPEG
                File outputFile = new File(croppedImageUri.getPath());
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeFile(outputFile.getAbsolutePath());
                    FileOutputStream outStream = new FileOutputStream(outputFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageViewAvatar.setImageBitmap(bitmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                // Обработка ошибки обрезки изображения
            }
        }
    }

    private void openImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }
}