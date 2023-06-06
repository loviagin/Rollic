package com.loviagin.rollic.activities;

import static com.loviagin.rollic.Constants.USERS_COLLECTION;
import static com.loviagin.rollic.Constants.USER_EMAIL;
import static com.loviagin.rollic.Constants.USER_UID;
import static com.loviagin.rollic.UserData.name;
import static com.loviagin.rollic.UserData.posts;
import static com.loviagin.rollic.UserData.uid;
import static com.loviagin.rollic.UserData.username;
import static com.loviagin.rollic.models.Objects.currentUser;
import static com.loviagin.rollic.models.Objects.mAuth;
import static com.loviagin.rollic.models.Objects.preferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.loviagin.rollic.UserData;
import com.loviagin.rollic.adapters.AuthTabAdapter;
import com.loviagin.rollic.R;
import com.loviagin.rollic.models.User;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class AuthActivity extends AppCompatActivity implements AuthTabAdapter.OnAuthTabClickListener {

    public static final String TAG = "Auth_Activity_TAG";
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private ViewPager2 viewPager;
    private AuthTabAdapter adapter;
    private ImageButton buttonGoogle;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        tabLayout = findViewById(R.id.tlAuth);
        viewPager = findViewById(R.id.vpAuth);
        adapter = new AuthTabAdapter(this);
        buttonGoogle = findViewById(R.id.bGoogleAuth);
        progressBar = findViewById(R.id.pbAuth);

        viewPager.setAdapter(adapter);
        buttonGoogle.setOnClickListener(v -> googleLogin());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

    private void googleLogin() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true) // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id)) // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build()) // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(false)
                .build();
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.d(TAG, e.getLocalizedMessage());
                });
    }

    @Override
    public void OnEmailLoginClick(String email, String pass) {
        if (email.equals("") || pass.equals("") || pass.length() < 6) {
            Toast.makeText(this, getResources().getString(R.string.error_email_auth_str), Toast.LENGTH_SHORT).show();
        } else if (isValidEmail(email)) {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            emailAuth(email, pass);
        } else {
            Toast.makeText(this, getResources().getString(R.string.check_data_auth_str), Toast.LENGTH_SHORT).show();
        }
    }

    private void emailAuth(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        currentUser = mAuth.getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection(USERS_COLLECTION).whereEqualTo(USER_EMAIL, email)
                                .get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task1.getResult()) {
                                            uid = document.getId();
                                            User user = document.toObject(User.class);
                                            UserData.email = user.getEmail();
                                            username = user.getUsername();
                                            posts = user.getPosts();
                                            name = user.getF_name();
                                            preferences.edit().putString(USER_UID, uid).apply();
                                            startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task1.getException());
                                    }
                                });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(AuthActivity.this, getResources().getString(R.string.register_text), Toast.LENGTH_SHORT).show();
                        emailRegister(email, pass);
                    }
                });
    }

    private void emailRegister(String email, String pass) {
        final String email0 = email;
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        currentUser = mAuth.getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        User user = new User(email0);
                        db.collection(USERS_COLLECTION).add(user).addOnSuccessListener(documentReference -> {
                            uid = documentReference.getId();
                            UserData.email = user.getEmail();
                            username = user.getUsername();
                            posts = user.getPosts();
                            preferences.edit().putString(USER_UID, uid).apply();
                            startActivity(new Intent(AuthActivity.this, RegisterScreenActivity.class));
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(AuthActivity.this, getResources().getString(R.string.error_register_str), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        startActivity(new Intent(AuthActivity.this, AuthActivity.class));
                    }
                });
    }

    private String phoneNumber = "";

    @Override
    public void OnPhoneSendClick(String phone) {
        phoneNumber = phone;
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(AuthActivity.this, getResources().getString(R.string.too_many_requests_auth_str), Toast.LENGTH_SHORT).show();
                    // The SMS quota for the project has been exceeded
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    Toast.makeText(AuthActivity.this, getResources().getString(R.string.recaptcha_error_str), Toast.LENGTH_SHORT).show();
                    // reCAPTCHA verification attempted with null Activity
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @Override
    public void OnPhoneVerifyClick(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    public void OnPhoneResendClick() {
        resendVerificationCode(phoneNumber);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        currentUser = task.getResult().getUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Log.d(TAG, currentUser.getUid() + "");
                        DocumentReference docRef = db.collection(USERS_COLLECTION).document(currentUser.getUid());
                        docRef.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Log.d(TAG, "User exists in FireStore");
                                User user = documentSnapshot.toObject(User.class);
                                UserData.email = user.getEmail();
                                username = user.getUsername();
                                posts = user.getPosts();
                                name = user.getF_name();
                                uid = mAuth.getUid();
                                preferences.edit().putString(USER_UID, currentUser.getUid()).apply();
                                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                            } else {
                                Log.d(TAG, "User doesn't exists in FireStore" + mAuth.getCurrentUser().getEmail());
                                User user = new User(mAuth.getCurrentUser().getEmail());
                                db.collection(USERS_COLLECTION).add(user).addOnSuccessListener(documentReference -> {
                                    uid = documentReference.getId();
                                    UserData.email = user.getEmail();
                                    username = user.getUsername();
                                    posts = user.getPosts();
                                    preferences.edit().putString(USER_UID, uid).apply();
                                    startActivity(new Intent(AuthActivity.this, RegisterScreenActivity.class));
                                });
                            }
                        });
                        Log.d(TAG, "signInWithCredential:success with uId " + currentUser.getUid());
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                });
    }

    private void resendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(mResendToken)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken != null) {
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, task -> {
                                    if (task.isSuccessful()) {
                                        currentUser = mAuth.getCurrentUser();
                                        assert currentUser != null;
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        Log.e(TAG, currentUser.getUid() + "");
                                        DocumentReference docRef = db.collection(USERS_COLLECTION).document(currentUser.getUid());
                                        docRef.get().addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                Log.d(TAG, "User exists in FireStore");
                                                User user = documentSnapshot.toObject(User.class);
                                                UserData.email = user.getEmail();
                                                username = user.getUsername();
                                                posts = user.getPosts();
                                                name = user.getF_name();
                                                uid = mAuth.getUid();
                                                preferences.edit().putString(USER_UID, currentUser.getUid()).apply();
                                                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                            } else {
                                                Log.d(TAG, "User doesn't exists in FireStore" + mAuth.getCurrentUser().getEmail());
                                                User user = new User(mAuth.getCurrentUser().getEmail());
                                                db.collection(USERS_COLLECTION).add(user).addOnSuccessListener(documentReference -> {
                                                    uid = documentReference.getId();
                                                    UserData.email = user.getEmail();
                                                    username = user.getUsername();
                                                    name = mAuth.getCurrentUser().getDisplayName();
                                                    posts = user.getPosts();
                                                    preferences.edit().putString(USER_UID, uid).apply();
                                                    startActivity(new Intent(AuthActivity.this, RegisterScreenActivity.class));
                                                });
                                            }
                                            progressBar.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        });
                                        Log.d(TAG, "signInWithCredential:success with uId " + currentUser.getUid() + " and token " + idToken);
                                    } else {
                                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    }
                                });
                    }
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case CommonStatusCodes.CANCELED:
                            Log.d(TAG, "One-tap dialog was closed.");
                            // Don't re-prompt the user.
                            showOneTapUI = false;
                            break;
                        case CommonStatusCodes.NETWORK_ERROR:
                            Log.d(TAG, "One-tap encountered a network error.");
                            // Try again or just ignore.
                            break;
                        default:
                            Log.d(TAG, "Couldn't get credential from result."
                                    + e.getLocalizedMessage());
                            break;
                    }
                }
                break;
        }
    }
}