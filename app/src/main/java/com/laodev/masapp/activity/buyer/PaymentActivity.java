package com.laodev.masapp.activity.buyer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.util.AppUtil;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class PaymentActivity extends BaseActivity {

    private static final String CONFIG_CLIENT_LIVEID = "AeUwS0qB0eYH4F2huCW4xIJHwbx3QA_VGYjRBEqLPIrc3lAMzANPh0s_ME-hrEAUauEeZU_UF6bfmJkp";

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
    private static final int REQUEST_CODE_PAYMENT = 10001;
    private static PayPalConfiguration configuration = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_LIVEID)
            .merchantName("Masapp")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacey"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
        startService(intent);
    }

    private void onSuccessPayment() {
        UserModel userModel = AppUtil.gCurrentUser;
        userModel.coins = String.valueOf(Integer.parseInt(AppUtil.gCurrentUser.coins) + 1);
        ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.pgr_connect_server));
        userModel.addUserToFB(new UserModel.UserModelInterface() {
            @Override
            public void onSuccess(UserModel user) {
                dialog.dismiss();

                AppUtil.gCurrentUser = user;
                onBackPressed();
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(PaymentActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickBackImg(View view) {
        onBackPressed();
    }

    public void onClickPaypalBtn(View view) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal("25"), "USD", "Masapp Post Order", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    public void onClickCardBtn(View view) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal("25"), "USD", "Masapp Post Order", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        JSONObject object = confirm.toJSONObject();
                        String response = object.getString("response_type");
                        if (response.equals("payment")) {
                            onSuccessPayment();
                        }
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject().toString(4));
                    } catch (JSONException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Pago cancelado", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
