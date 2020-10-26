package com.laodev.masapp.activity.seller;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;
import com.laodev.masapp.model.DirectionModel;
import com.laodev.masapp.util.AppUtil;


public class NavigationActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        getWindow().setStatusBarColor(getColor(R.color.colorBlue));

        initUIView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initUIView() {
        WebView wvw_navigation = findViewById(R.id.wvw_navigation);
        ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.pgr_connect_server));
        DirectionModel.getCheckModelByUserID(AppUtil.gOrderModel.userid, new DirectionModel.DirectionModelListener() {
            @Override
            public void onSuccess(DirectionModel model) {
                dialog.dismiss();
                String url = "https://www.google.com/maps/dir/?api=1&destination="
                        + model.location
                        + "&origin="
                        + AppUtil.gCurrentUser.location
                        +  "&travelmode=driving&dir_action=navigate";
                WebSettings webSettings = wvw_navigation.getSettings();
                webSettings.setJavaScriptEnabled(true);
                wvw_navigation.loadUrl(url);
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(NavigationActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickBackImg(View view) {
        onBackPressed();
    }
}
