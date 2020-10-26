package com.laodev.masapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import static com.laodev.masapp.util.Constants.SPLASH_TIME_OUT;

public class ImageDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        ImageView img_detail = findViewById(R.id.img_detail);
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(img_detail);
    }

    public void onClickBackImg(View view) {
        onBackPressed();
    }

}
