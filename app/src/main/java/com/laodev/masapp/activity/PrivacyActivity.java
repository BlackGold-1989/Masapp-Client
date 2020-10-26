package com.laodev.masapp.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;

public class PrivacyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        initView();
    }

    private void initView() {
        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(view -> onBackPressed());
    }

}
