package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.EDIT_POST;
import static com.loviagin.rollic.Constants.POSTS_STR;
import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.urlAvatar;
import static com.loviagin.rollic.UserData.username;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loviagin.rollic.R;
import com.loviagin.rollic.models.Post;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditPostActivity extends AppCompatActivity {

    private ImageView imageView1;
    private EditText editTextTitle, editTextDescription, editTextTags;
    private Button buttonCancel, buttonSave;
    private ProgressBar progressBar;

    private String pstUid;
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    private static final int REQUEST_CODE_IMAGE_CROP = 200;
    private Uri selectedImageUri;
    private boolean isImageUploaded = false;
    private boolean isPost = true;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        findViewById(R.id.bMessage).setVisibility(View.INVISIBLE);
        findViewById(R.id.bNotifications).setVisibility(View.INVISIBLE);
        imageView1 = findViewById(R.id.ivEditPost);
        editTextTitle = findViewById(R.id.etTitleEditPost);
        editTextDescription = findViewById(R.id.etDescriptionEditPost);
        editTextTags = findViewById(R.id.etTagsEditPost);
        buttonCancel = findViewById(R.id.bCancelEditPost);
        buttonSave = findViewById(R.id.bSaveEditPost);
        progressBar = findViewById(R.id.pbEditPost);

        buttonCancel.setOnClickListener(v -> finish());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if (intent.hasExtra(EDIT_POST)) {
            pstUid = intent.getStringExtra(EDIT_POST);
        } else {
            Toast.makeText(this, "Упс...ошибочка", Toast.LENGTH_SHORT).show();
            finish();
        }

        infoLoad();

        imageView1.setOnClickListener(v -> {
            isImageUploaded = true;
            selectImage();
        });
        buttonSave.setOnClickListener(v -> saveInfo());
    }

    private void infoLoad() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        db.collection(POSTS_STR).document(pstUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        post = document.toObject(Post.class);
                        if (post.getImagesUrls() != null) { // Photo post
                            isPost = false;
                            storageRef.child(post.getImagesUrls().get(0)).getDownloadUrl().addOnSuccessListener(uri -> {
                                Picasso.get().load(uri).into(imageView1);
                            });
                            editTextTitle.setVisibility(View.GONE);
                        } else { // text post
                            imageView1.setVisibility(View.GONE);
                            editTextTitle.setText(post.getTitle());
                        }
                        editTextDescription.setText(post.getDescription());
                        editTextTags.setText(post.getTags());
                    } else {
                        Toast.makeText(EditPostActivity.this, "Пост не найден. Возможно, вы его уже удалили", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditPostActivity.this, "Ошибка подключения к интернету", Toast.LENGTH_SHORT).show();
                }
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void saveInfo() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (isPost && !editTextTitle.getText().toString().equals("") && !editTextDescription.getText().toString().equals("")) {
            db.collection(POSTS_STR).document(pstUid).update("title", editTextTitle.getText().toString().trim(),
                    "description", editTextDescription.getText().toString().trim(),
                    "tags", editTextTags.getText().toString().trim());
        } else if (!editTextDescription.getText().toString().equals("")) {
            String iva = null;
            if (isImageUploaded) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                storageRef.child(post.getImagesUrls().get(0)).delete();
                iva = "post-images/" + uid + System.currentTimeMillis() + ".jpg";
                StorageReference imagesRef = storageRef.child(iva);
                imageView1.setDrawingCacheEnabled(true);
                imageView1.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imageView1.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = imagesRef.putBytes(data);
                String finalIva = iva;
                uploadTask.addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                }).addOnSuccessListener(taskSnapshot -> {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    db.collection(POSTS_STR).document(pstUid).update(
                            "imagesUrls", FieldValue.arrayRemove(post.getImagesUrls().get(0)),
                            "imagesUrls", FieldValue.arrayUnion(finalIva));
                });
            }
            db.collection(POSTS_STR).document(pstUid).update("description", editTextDescription.getText().toString().trim(),
                    "tags", editTextTags.getText().toString().trim());
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.GONE);
            startActivity(new Intent(this, AccountActivity.class));
        }
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
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                isImageUploaded = true;
                imageView1.setImageBitmap(bitmap);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void selectImage() {
        progressBar.setVisibility(View.VISIBLE);
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
            progressBar.setVisibility(View.GONE);
        }
    }

    private void startImageCrop(Uri imageUri) {
        Intent intent = CropImage.activity(imageUri)
                .getIntent(this);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_CROP);
    }
}