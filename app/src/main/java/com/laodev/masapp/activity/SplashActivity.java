package com.laodev.masapp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;
import com.laodev.masapp.activity.seller.ReviewActivity;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;
import com.laodev.masapp.util.PermissionsUtil;

import static com.laodev.masapp.util.Constants.SPLASH_TIME_OUT;

public class SplashActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 451;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setStatusBarColor(getColor(R.color.colorBlue));

        final Handler handler = new Handler();
        handler.postDelayed(this::onShowNextView, SPLASH_TIME_OUT);
    }

    private void onShowNextView() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            onMoveToMainActivity();
        } else if (PermissionsUtil.hasPermissions(this)) {
            AppUtil.showOtherActivity(this, LoginActivity.class, 0);
            finish();
        } else {
            ActivityCompat.requestPermissions(this, PermissionsUtil.permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void onMoveToMainActivity() {
        ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.pgr_connect_server));
        UserModel.getOwnUser(new UserModel.UserModelInterface() {
            @Override
            public void onSuccess(UserModel user) {
                dialog.dismiss();
                AppUtil.gCurrentUser = user;
                if (AppUtil.gCurrentUser.type.equals(Constants.USER_SELLER) && AppUtil.gCurrentUser.status.equals(Constants.STATUS_READY)) {
                    AppUtil.showOtherActivity(SplashActivity.this, ReviewActivity.class, 0);
                    finish();
                } else {
                    AppUtil.showOtherActivity(SplashActivity.this, MainActivity.class, 0);
                    finish();
                }
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(SplashActivity.this, error, Toast.LENGTH_SHORT).show();
                existApp();
            }

            @Override
            public void onNoFound() {
                dialog.dismiss();
                Toast.makeText(SplashActivity.this, R.string.toast_no_user, Toast.LENGTH_SHORT).show();
                AppUtil.showOtherActivity(SplashActivity.this, LoginActivity.class, 0);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionsUtil.permissionsGranted(grantResults)) {
            AppUtil.showOtherActivity(this, LoginActivity.class, 0);
            finish();
        } else {
            existApp();
        }
    }

}
