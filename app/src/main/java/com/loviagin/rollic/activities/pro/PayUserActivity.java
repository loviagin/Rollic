package com.loviagin.rollic.activities.pro;

import static com.loviagin.rollic.Constants.USER_UID;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.loviagin.rollic.R;
import com.loviagin.rollic.presentation.main.MainActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.yoomoney.sdk.kassa.payments.Checkout;
import ru.yoomoney.sdk.kassa.payments.TokenizationResult;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.GooglePayParameters;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.TestParameters;

public class PayUserActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_TOKENIZE = 349874589;
    private static final int REQUEST_CODE_CONFIRM = 6786;
    private ImageButton buttonClose;
    private Button buttonPay;
    private TextView textViewNickname, textViewName;
    private ShapeableImageView imageView;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_user);

        buttonClose = findViewById(R.id.ibCloseUserPay);
        buttonPay = findViewById(R.id.bPayUserPro);
        textViewName = findViewById(R.id.tvNameUserPay);
        textViewNickname = findViewById(R.id.tvNicknameUserPay);
        imageView = findViewById(R.id.ivAvatarPayUser);

        buttonClose.setOnClickListener(view -> finish());

        Intent intent = getIntent();
        if (intent.hasExtra(USER_UID)) {
            String cUser = intent.getStringExtra(USER_UID);
            String name = intent.getStringExtra("name");
            nickname = intent.getStringExtra("nickname");
            String avatar = intent.getStringExtra("avatar") != null ? intent.getStringExtra("avatar") : "";

            textViewName.setText(name);
            textViewNickname.setText(String.format("на %s", nickname));
            Picasso.get().load(Uri.parse(avatar)).placeholder(R.drawable.user).into(imageView);

            buttonPay.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)
                    .putExtra("user", nickname)
                    .putExtra("cid", cUser)));
        }
    }
}