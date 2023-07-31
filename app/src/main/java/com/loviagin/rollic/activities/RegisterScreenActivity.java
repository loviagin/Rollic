package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.AVATAR_URL;
import static com.loviagin.rollic.Constants.NICKNAME;
import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.Constants.USER_NAME;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.urlAvatar;
import static com.loviagin.rollic.UserData.username;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loviagin.rollic.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegisterScreenActivity extends AppCompatActivity {

    private Button buttonUpload, buttonSkip, buttonSave;
    private EditText editTextName, editTextNickname;
    private ImageView imageViewAvatar;
    private ProgressBar progressBar;
    private boolean isAvatarUpload = false;

    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    private static final int REQUEST_CODE_IMAGE_CROP = 200;
    private Uri selectedImageUri;

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

        imageViewAvatar.setOnClickListener(v -> selectImage());
        buttonUpload.setOnClickListener(v -> selectImage());

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
            if (editTextNickname.getText().toString().equals("") || checkNickname(editTextNickname.getText().toString())) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressBar.setVisibility(View.VISIBLE);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String iva = null;
                if (isAvatarUpload) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    iva = "avatars/" + uid + System.currentTimeMillis() + ".jpg";
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
                }
                String name0 = editTextName.getText() == null ? username : editTextName.getText().toString().trim();
                String nickname = editTextNickname.getText() == null ? username : editTextNickname.getText().toString().trim();
                String finalIva = iva;
                db.collection(USERS_COLLECTION).document(uid).update(USER_NAME, name0, NICKNAME, nickname, AVATAR_URL, iva).addOnSuccessListener(unused -> {
                    name = name0;
                    username = nickname;
                    urlAvatar = finalIva;
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(RegisterScreenActivity.this, MainActivity.class));
                });
            } else {
                Toast.makeText(this, "Проверьте корректность данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                startImageCrop(selectedImageUri);
            }
        } else if (requestCode == REQUEST_CODE_IMAGE_CROP && resultCode == RESULT_OK) {
            if (data != null) {
                Uri croppedImageUri = CropImage.getActivityResult(data).getUri();

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
                // Установка обрезанного изображения в ImageView с помощью Picasso
                isAvatarUpload = true;
                imageViewAvatar.setImageBitmap(bitmap);
            }
        }
    }

    private void startImageCrop(Uri imageUri) {
        Intent intent = CropImage.activity(imageUri)
                .getIntent(this);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_CROP);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            // Create a list of applicable intents
            List<Intent> chooserIntents = new ArrayList<>();

            // Add Yandex.Disk
            Intent yandexDiskIntent = new Intent(Intent.ACTION_PICK);
            yandexDiskIntent.setType("image/*");
            yandexDiskIntent.setPackage("ru.yandex.disk");
            if (packageManager.resolveActivity(yandexDiskIntent, 0) != null) {
                chooserIntents.add(yandexDiskIntent);
            }

            // Add Google Drive
            Intent googleDriveIntent = new Intent(Intent.ACTION_PICK);
            googleDriveIntent.setType("image/*");
            googleDriveIntent.setPackage("com.google.android.apps.docs");
            if (packageManager.resolveActivity(googleDriveIntent, 0) != null) {
                chooserIntents.add(googleDriveIntent);
            }

            Intent photoDriveIntent = new Intent(Intent.ACTION_PICK);
            photoDriveIntent.setType("image/*");
            photoDriveIntent.setPackage("com.google.android.apps.photos");
            if (packageManager.resolveActivity(photoDriveIntent, 0) != null) {
                chooserIntents.add(photoDriveIntent);
            }

            // Add the default gallery picker
            chooserIntents.add(intent);

            // Create the chooser intent
            Intent chooserIntent = Intent.createChooser(intent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, chooserIntents.toArray(new Parcelable[0]));

            // Launch the chooser activity
            startActivityForResult(chooserIntent, REQUEST_CODE_SELECT_IMAGE);
        } else {
            Toast.makeText(this, R.string.no_gallery_apps_found_on_this_device, Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean checkNickname(String s) {
        String pattern = "^[a-zA-Z0-9]*$";
        return s.matches(pattern);
    }
}