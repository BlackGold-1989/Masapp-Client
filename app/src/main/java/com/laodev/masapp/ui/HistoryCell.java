package com.laodev.masapp.ui;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.laodev.masapp.R;
import com.laodev.masapp.activity.buyer.HistoryDetailActivity;
import com.laodev.masapp.model.HistoryModel;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryCell extends LinearLayout {

    private HistoryModel mModel;

    private TextView lbl_name, lbl_info, lbl_review, lbl_header;
    private CircleImageView civ_avatar;
    private LinearLayout llt_header;

    private List<ImageView> lst_images = new ArrayList<>();
    private String mBeforeDate;

    private void initWithEvent() {
        this.setOnClickListener(v -> {
            if (AppUtil.gCurrentUser.type.equals(Constants.USER_BUYER)) {
                String jsonRequest = new Gson().toJson(mModel);
                Intent intent = new Intent(getContext(), HistoryDetailActivity.class);
                intent.putExtra("model", jsonRequest);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
                getContext().startActivity(intent, options.toBundle());
            }
        });
    }

    public HistoryCell(Context context, HistoryModel model, String beforeDate) {
        super(context);

        mModel = model;
        mBeforeDate = beforeDate;

        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.ui_history_cell, this, true);

        initUIView();
        initWithEvent();
    }

    private void initUIView() {
        lbl_name = findViewById(R.id.lbl_name);
        lbl_info = findViewById(R.id.lbl_order_info);
        lbl_review = findViewById(R.id.lbl_order_review);
        civ_avatar = findViewById(R.id.civ_avatar);
        llt_header = findViewById(R.id.llt_history_header);
        lbl_header = findViewById(R.id.lbl_history_header);

        ImageView img_remark1 = findViewById(R.id.img_history_01);
        ImageView img_remark2 = findViewById(R.id.img_history_02);
        ImageView img_remark3 = findViewById(R.id.img_history_03);
        ImageView img_remark4 = findViewById(R.id.img_history_04);
        ImageView img_remark5 = findViewById(R.id.img_history_05);
        lst_images.add(img_remark1);
        lst_images.add(img_remark2);
        lst_images.add(img_remark3);
        lst_images.add(img_remark4);
        lst_images.add(img_remark5);

        initWithData();
    }

    private void initWithData() {
        String date = mModel.orderModel.endtime.split(" ")[0];
        if (mBeforeDate.length() > 0) {
            if (mBeforeDate.equals(date)) {
                llt_header.setVisibility(View.GONE);
            } else {
                llt_header.setVisibility(View.VISIBLE);
                lbl_header.setText(date);
            }
        } else {
            llt_header.setVisibility(View.VISIBLE);
            lbl_header.setText(date);
        }
        lbl_info.setText(mModel.orderModel.desc);
        lbl_review.setText(mModel.comment);

        String userid = mModel.orderModel.doctorid;
        if (AppUtil.gCurrentUser.type.equals(Constants.USER_SELLER)) {
            userid = mModel.orderModel.userid;
        }
        UserModel.getUserByID(userid, new UserModel.UserModelInterface() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(UserModel user) {
                if (AppUtil.gCurrentUser.type.equals("buyer")) {
                    lbl_name.setText("Dr. " + user.name);
                } else {
                    lbl_name.setText("Px. " + user.name);
                }
                Picasso.get().load(user.imgurl).fit().centerCrop()
                        .placeholder(R.drawable.ic_account_circle)
                        .error(R.drawable.ic_account_circle)
                        .into(civ_avatar, null);
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        for (int i = 0; i < lst_images.size(); i++) {
            ImageView img_remark = lst_images.get(i);
            if (i < mModel.rating) {
                img_remark.setImageResource(R.drawable.ic_star_fill);
            } else {
                img_remark.setImageResource(R.drawable.ic_star);
            }
        }
    }

}
