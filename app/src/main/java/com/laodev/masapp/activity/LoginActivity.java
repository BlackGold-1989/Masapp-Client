package com.laodev.masapp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;
import com.laodev.masapp.activity.seller.RegisterActivity;
import com.laodev.masapp.activity.seller.ReviewActivity;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.ui.CustomEditText;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;
import com.laodev.masapp.util.SharedPreferenceUtil;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;


public class LoginActivity extends BaseActivity {
    private FirebaseAuth mAuth;

    private LinearLayout contain, llt_login_send, llt_verify_result;
    private TextView lbl_login_seller, lbl_verify_title, lbl_verify_detail, lbl_time_counter, lbl_verify_resend, lbl_privacy;
    private CustomEditText cet_phone;
    private ProgressDialog dialog;

    private String verificationId;
    private boolean isResult;

    private OTPListener otpListener = new OTPListener() {
        @Override
        public void onInteractionListener() {

        }

        @Override
        public void onOTPComplete(String otp) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
            onCompleteVerify(credential);
        }
    };
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verifyPhoneNumberCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            onCompleteVerify(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            Toast.makeText(LoginActivity.this, R.string.toast_sent_code, Toast.LENGTH_SHORT).show();

            verificationId = s;
            isResult = true;
            setResultView();

            lbl_time_counter.setVisibility(View.VISIBLE);

            final int[] count = {59};
            Timer T=new Timer();
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        lbl_time_counter.setText(String.format(Locale.US, "00 : %02d", count[0]));
                        count[0]--;
                        if(count[0] == 0){
                            lbl_time_counter.setVisibility(View.GONE);
                            isResult = false;
                            setResultView();                            }
                    });
                }
            }, 1000, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        setStatusBar();
    }

    private void setStatusBar() {
        if (SharedPreferenceUtil.isSellerMode()) {
            getWindow().setStatusBarColor(getColor(R.color.colorBackground));
            contain.setBackgroundColor(getColor(R.color.colorBackground));
            lbl_login_seller.setVisibility(View.VISIBLE);
            lbl_verify_title.setTextColor(getColor(R.color.colorGray));
            lbl_verify_detail.setTextColor(getColor(R.color.colorGray));
            lbl_time_counter.setTextColor(getColor(R.color.colorGray));
            lbl_verify_resend.setTextColor(getColor(R.color.colorGray));
            lbl_privacy.setTextColor(getColor(R.color.colorGray));
        } else {
            getWindow().setStatusBarColor(getColor(R.color.colorBlue));
            contain.setBackground(ContextCompat.getDrawable(this, R.drawable.back_main_blue));
            lbl_login_seller.setVisibility(View.GONE);
            lbl_verify_title.setTextColor(getColor(R.color.colorWhite));
            lbl_verify_detail.setTextColor(getColor(R.color.colorWhite));
            lbl_time_counter.setTextColor(getColor(R.color.colorWhite));
            lbl_verify_resend.setTextColor(getColor(R.color.colorWhite));
            lbl_privacy.setTextColor(getColor(R.color.colorWhite));
        }
    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();

        contain = findViewById(R.id.contain);
        lbl_login_seller = findViewById(R.id.lbl_login_seller);
        lbl_verify_title = findViewById(R.id.lbl_verify_title);
        lbl_verify_detail = findViewById(R.id.lbl_verify_detail);
        lbl_time_counter = findViewById(R.id.lbl_time_counter);
        lbl_verify_resend = findViewById(R.id.lbl_verify_resend);
        lbl_privacy = findViewById(R.id.lbl_privacy);
        OtpTextView otp_verify = findViewById(R.id.otp_verify);
        otp_verify.setOtpListener(otpListener);
        cet_phone = findViewById(R.id.cet_phone);

        llt_login_send = findViewById(R.id.llt_login_send);
        llt_verify_result = findViewById(R.id.llt_verify_result);
        llt_verify_result.setVisibility(View.GONE);
    }

    private void setResultView() {
        if (isResult) {
            llt_login_send.setVisibility(View.GONE);
            llt_verify_result.setVisibility(View.VISIBLE);
        } else {
            llt_login_send.setVisibility(View.VISIBLE);
            llt_verify_result.setVisibility(View.GONE);
        }
    }

    private void onCompleteVerify(PhoneAuthCredential credential) {
        dialog = ProgressDialog.show(this, "", "");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        onNextEvent(task.getResult().getUser());
                    } else {
                        dialog.dismiss();
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        existApp();
                    }
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    existApp();
                });
    }

    private void onNextEvent(FirebaseUser user) {
        UserModel.getUserByID(user.getUid(), new UserModel.UserModelInterface() {
            @Override
            public void onSuccess(UserModel userModel) {
                dialog.dismiss();

                AppUtil.gCurrentUser = userModel;
                if (userModel.type.equals(Constants.USER_SELLER) && SharedPreferenceUtil.isSellerMode()) {
                    if (userModel.status.equals(Constants.STATUS_READY)) {
                        AppUtil.showOtherActivity(LoginActivity.this, ReviewActivity.class, 0);
                    } else {
                        AppUtil.showOtherActivity(LoginActivity.this, MainActivity.class, 0);
                    }
                    finish();
                } else if (userModel.type.equals(Constants.USER_BUYER) && !SharedPreferenceUtil.isSellerMode()) {
                    AppUtil.showOtherActivity(LoginActivity.this, MainActivity.class, 0);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.toast_user_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                existApp();
            }

            @Override
            public void onNoFound() {
                dialog.dismiss();

                AppUtil.gCurrentUser = new UserModel();
                AppUtil.gCurrentUser.id = user.getUid();
                AppUtil.gCurrentUser.phone = user.getPhoneNumber();
                if (SharedPreferenceUtil.isSellerMode()) {
                    AppUtil.gCurrentUser.type = Constants.USER_SELLER;
                    AppUtil.showOtherActivity(LoginActivity.this, RegisterActivity.class, 0);
                    finish();
                } else {
                    AppUtil.gCurrentUser.type = Constants.USER_BUYER;
                    AppUtil.gCurrentUser.status = Constants.STATUS_READY;
                    AppUtil.gCurrentUser.coins = "0";
                    AppUtil.gCurrentUser.addUserToFB(new UserModel.UserModelInterface() {
                        @Override
                        public void onFailed(String error) {
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                            existApp();
                        }

                        @Override
                        public void onSuccess(UserModel user) {
                            AppUtil.showOtherActivity(LoginActivity.this, MainActivity.class, 0);
                            finish();
                        }
                    });
                }
            }
        });
    }

    public void onClickChangeUser(View view) {
        SharedPreferenceUtil.setSellerMode(!SharedPreferenceUtil.isSellerMode());
        setStatusBar();
    }

    public void onClickLblPrivacy(View view) {
        AppUtil.showOtherActivity(LoginActivity.this, PrivacyActivity.class, 0);
    }

    @SuppressLint("SetTextI18n")
    public void onClickVerifyBtn(View view) {
        if (cet_phone.getInputText().length() == 0) {
            Toast.makeText(LoginActivity.this, R.string.toast_phone_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        lbl_verify_detail.setText(getString(R.string.login_verify_detail) + " " + cet_phone.getFullPhoneNumber());
        String str_phone = cet_phone.getFullPhoneNumber();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                str_phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verifyPhoneNumberCallback);
    }

}
