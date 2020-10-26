package com.laodev.masapp.ui;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.laodev.masapp.R;
import com.laodev.masapp.activity.ImageDetailActivity;

public class OrderImageCell extends LinearLayout {

    private Bitmap bitmap;
    private OrderImageCellCallback imageCellCallback;

    private ImageView img_remove, img_data;
    private TextView lbl_name;
    private int index = 0;


    private void initWithEvent() {
        img_remove.setOnClickListener(v -> imageCellCallback.onRemoveOrderImage(index));
        this.setOnClickListener(v -> {
//            Intent intent = new Intent(getContext(), ImageDetailActivity.class);
//            intent.putExtra("url", url);
//            ActivityOptions options = ActivityOptions.makeCustomAnimation(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
//            getContext().startActivity(intent, options.toBundle());
        });
    }

    public OrderImageCell(Context context, Bitmap bitmap, int index, OrderImageCellCallback imageCellCallback) {
        super(context);

        this.bitmap = bitmap;
        this.index = index;
        this.imageCellCallback = imageCellCallback;

        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.ui_order_image_cell, this, true);

        initUIView();
        initWithEvent();
    }

    @SuppressLint("SetTextI18n")
    private void initUIView() {
        img_data = findViewById(R.id.img_order_data);
        img_data.setImageBitmap(bitmap);
        img_remove = findViewById(R.id.img_order_remove);
        lbl_name = findViewById(R.id.lbl_order_name);
        lbl_name.setText("Post Image " + index);
    }

    public void setCellColor(int id) {
        lbl_name.setTextColor(getContext().getColor(id));
        img_remove.setColorFilter(ContextCompat.getColor(getContext(), id), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public interface OrderImageCellCallback {
        void onRemoveOrderImage(int index);
    }

}
