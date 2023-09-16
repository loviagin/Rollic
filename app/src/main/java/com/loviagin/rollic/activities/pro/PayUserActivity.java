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

            buttonPay.setOnClickListener(view -> startTokenize());
        }
    }

    void startTokenize() {
        Set<PaymentMethodType> paymentMethodTypes = new HashSet<PaymentMethodType>(){{
            add(PaymentMethodType.SBERBANK); // selected payment method - SberPay
//            add(PaymentMethodType.YOO_MONEY); // selected payment method - YooMoney
            add(PaymentMethodType.BANK_CARD); // selected payment method - Bank card
        }};
        PaymentParameters paymentParameters = new PaymentParameters(
                new Amount(BigDecimal.valueOf(10), Currency.getInstance("RUB")),
                "Оплата подписки на пользователя @" + nickname,
                "Вы получите эксклюзивный доступ к платному контенту автора",
                "live_MjUwMDIx65a3FhMyJYmfuZ9BHJS7yvmdbzrTFFfkMe0", // key for client apps from the YooMoney Merchant Profile
                "250021", // ID of the store in the YooMoney system
                SavePaymentMethod.OFF, // flag of the disabled option to save payment methods
                paymentMethodTypes // the full list of available payment methods has been provided
//                "", // gatewayId of the store for Google Pay payments (required if payment methods include Google Pay)
//                "https://rollic.ru", //  url of the page (only https is supported) that the user should be returned to after completing 3ds. Must be used only when own Activity for the 3ds url is used.
//                "", // user's phone number for autofilling the user phone number field in SberPay. Supported data format: "+7XXXXXXXXXX"
//                new GooglePayParameters(), // settings for tokenization via GooglePay,
//                "Lq21TIHOUFFRvkpbIWELXxV_U3e6z_NrjKxPEIZzGfnSQ-sd6l4BhmQs9v25Z09a"
        );
        TestParameters testParameters = new TestParameters(
                true
        );
        Intent intent = Checkout.createTokenizeIntent(this, paymentParameters, testParameters);
        startActivityForResult(intent, REQUEST_CODE_TOKENIZE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TOKENIZE) {
            Log.e("TAG2456", "HERE");
            switch (resultCode) {
                case RESULT_OK:
                    // successful tokenization
                    TokenizationResult result = Checkout.createTokenizationResult(data);
                    switch (result.getPaymentMethodType()){
                        case BANK_CARD:
                            start3DSecure();
                            break;
                        case SBERBANK:
                            startConfirmSberPay();
                            break;
                    }
                    break;
                case RESULT_CANCELED:
                    // user canceled tokenization

                    break;
            }
        } else if (requestCode == REQUEST_CODE_CONFIRM) {
            Log.e("TAG2456", "HERE2");
            switch (resultCode) {
                case RESULT_OK:
                    // 3ds process completed
                    // No guarantee of success
                    break;
                case RESULT_CANCELED:
                    // The 3ds screen was closed
                    break;
                case Checkout.RESULT_ERROR:
                    // An error occurred during 3ds (no connection or another reason)
                    // More information can be found in data
                    // data.getIntExtra(Checkout.EXTRA_ERROR_CODE) - error code from WebViewClient.ERROR_* or Checkout.ERROR_NOT_HTTPS_URL
                    // data.getStringExtra(Checkout.EXTRA_ERROR_DESCRIPTION) - error description (may be missing)
                    // data.getStringExtra(Checkout.EXTRA_ERROR_FAILING_URL) - url where the error occurred (may be missing)
                    break;
            }
        }
    }

    void start3DSecure() {
        Intent intent = Checkout.createConfirmationIntent(this, "https://3dsurl.com/", PaymentMethodType.BANK_CARD);
        startActivityForResult(intent, REQUEST_CODE_CONFIRM);
    }

    void startConfirmSberPay() {
        Intent intent = Checkout.createConfirmationIntent(this, "rollic://invoicing/sberpay", PaymentMethodType.SBERBANK);
        startActivityForResult(intent, REQUEST_CODE_CONFIRM);
    }

//    void startYooMoneyTokenize() {
//        Set<PaymentMethodType> paymentMethodTypes = new HashSet<>();
//        PaymentParameters paymentParameters = new PaymentParameters(
//                new Amount(BigDecimal.TEN, Currency.getInstance("RUB")),
//                "Product name",
//                "Product description",
//                "live_thisKeyIsNotReal", // key for client apps from the YooMoney Merchant Profile (https://yookassa.ru/my/api-keys-settings)
//                "12345", // ID of the store in the YooMoney system
//                SavePaymentMethod.OFF, // flag of the disabled option to save payment methods
//                paymentMethodTypes.add(PaymentMethodType.YOO_MONEY), // selected payment method: YooMoney wallet,
//                null, // gatewayId of the store for Google Pay payments (required if payment methods include Google Pay)
//                null, // url of the page (only https is supported) that the user should be returned to after completing 3ds. Must be used only when own Activity for the 3ds url is used.
//                null, // user's phone number for autofilling the user phone number field in SberPay. Supported data format: "+7XXXXXXXXXX"
//                null, // settings for tokenization via GooglePay,
//                "example_authCenterClientId" // authCenterClientId: ID received upon registering the app on the https://yookassa.ru website
//        );
//        Intent intent = Checkout.createTokenizeIntent(this, paymentParameters);
//        startActivityForResult(intent, REQUEST_CODE_TOKENIZE);
//    }
}