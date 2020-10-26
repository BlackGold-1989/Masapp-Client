package com.laodev.masapp.ui;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.laodev.masapp.R;
import com.laodev.masapp.activity.ImageDetailActivity;
import com.laodev.masapp.activity.buyer.RequestDetailActivity;
import com.laodev.masapp.model.HistoryModel;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.model.RequestModel;
import com.laodev.masapp.util.TimerUtil;
import com.liangfeizc.flowlayout.FlowLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RequestCell extends LinearLayout {

    private RequestModel mModel;

    private TextView lbl_name, lbl_info, lbl_date, lbl_remark, lbl_review, lbl_address;
    private ImageView img_avatar;
    private LinearLayout llt_images;
    private HorizontalScrollView sch_images;
    private FlowLayout flw_cate;
    private List<ImageView> lst_stars = new ArrayList<>();

    public RequestCell(Context context, RequestModel model) {
        super(context);

        mModel = model;

        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.ui_request_cell, this, true);

        initUIView();
        initEvent();
    }

    private void initEvent() {
        this.setOnClickListener(view -> {
            String jsonRequest = new Gson().toJson(mModel);
            Intent intent = new Intent(getContext(), RequestDetailActivity.class);
            intent.putExtra("model", jsonRequest);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
            getContext().startActivity(intent, options.toBundle());
        });
    }

    private void initUIView() {
        lbl_name = findViewById(R.id.lbl_request_name);
        lbl_date = findViewById(R.id.lbl_request_time);

        lbl_info= findViewById(R.id.lbl_request_info);
        img_avatar = findViewById(R.id.img_request_avatar);

        sch_images = findViewById(R.id.sch_request_images);
        llt_images = findViewById(R.id.llt_request_images);

        lbl_remark = findViewById(R.id.lbl_request_remark);
        lbl_review = findViewById(R.id.lbl_request_review);

        flw_cate = findViewById(R.id.flw_tag_kind);

        lbl_address = findViewById(R.id.lbl_request_address);

        ImageView img_star_01 = findViewById(R.id.img_request_01);
        ImageView img_star_02 = findViewById(R.id.img_request_02);
        ImageView img_star_03 = findViewById(R.id.img_request_03);
        ImageView img_star_04 = findViewById(R.id.img_request_04);
        ImageView img_star_05 = findViewById(R.id.img_request_05);
        lst_stars.add(img_star_01);
        lst_stars.add(img_star_02);
        lst_stars.add(img_star_03);
        lst_stars.add(img_star_04);
        lst_stars.add(img_star_05);

        initWithData();
    }

    @SuppressLint("SetTextI18n")
    private void initWithData() {
        lbl_name.setText("Dr . " + mModel.user.name);
        Picasso.get().load(mModel.user.imgurl)
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .into(img_avatar, null);
        lbl_address.setText(mModel.user.address1 + " " + mModel.user.address2);
        lbl_info.setText(mModel.desc);
        lbl_date.setText(TimerUtil.getDelayTime(mModel.datetime, "yyyy-MM-dd HH:mm:ss"));
        flw_cate.removeAllViews();
        if (mModel.user.spec1.isEmpty()) {
            flw_cate.setVisibility(View.GONE);
        } else {
            flw_cate.setVisibility(View.VISIBLE);
            String[] specs = mModel.user.spec1.split(",");
            for (String spec: specs) {
                TagCell tagCell = new TagCell(getContext());
                tagCell.setTitle(spec);
                flw_cate.addView(tagCell);
            }
        }

        if (mModel.imgurls.size() == 0) {
            sch_images.setVisibility(View.GONE);
        } else {
            for (String url: mModel.imgurls) {
                LayoutParams imParams =
                        new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                imParams.setMargins(8, 8, 8, 8);
                ImageView img_request = new ImageView(getContext());
                img_request.setLayoutParams(imParams);
                img_request.setAdjustViewBounds(true);
                Picasso.get().load(url)
                        .placeholder(R.drawable.ic_image)
                        .error(R.drawable.ic_image)
                        .into(img_request, null);
                img_request.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), ImageDetailActivity.class);
                    intent.putExtra("url", url);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
                    getContext().startActivity(intent, options.toBundle());
                });
                llt_images.addView(img_request);
            }
        }

        HistoryModel.getRecentHistories(mModel.user, new HistoryModel.HistoryModelCallback() {
            @Override
            public void onSuccess(List<HistoryModel> historyModels, String average) {
                lbl_remark.setText(average);
                if (historyModels.size() > 0) {
                    for (int i = 0; i < (int)(Float.parseFloat(average) + 0.5); i++) {
                        ImageView img_star = lst_stars.get(i);
                        img_star.setImageResource(R.drawable.ic_star_fill);
                    }
                }
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        OrderModel.getAllOrdersByID(mModel.user.id, new OrderModel.OrderModelCallback() {
            @Override
            public void onSuccess(List<OrderModel> orderModels) {
                if (orderModels.size() > 0) {
                    lbl_review.setText(String.format(Locale.getDefault(), "Available after %d patients", orderModels.size()));
                    if (orderModels.size() > 2) {
                        lbl_review.setTextColor(Color.RED);
                    }
                } else {
                    lbl_review.setText("Available");
                }
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
