package com.laodev.masapp.ui;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.laodev.masapp.R;
import com.laodev.masapp.activity.ImageDetailActivity;
import com.laodev.masapp.activity.seller.OrderDetailActivity;
import com.laodev.masapp.model.DirectionModel;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.TimerUtil;
import com.squareup.picasso.Picasso;

public class OrderCell extends LinearLayout {

    private OrderModel mModel;

    private TextView lbl_name, lbl_info, lbl_status, lbl_date, lbl_address;
    private ImageView img_avatar;
    private View viw_statu;
    private LinearLayout llt_images;
    private HorizontalScrollView sch_images;

    private void initWithEvent() {
        this.setOnClickListener(v -> {
            AppUtil.gOrderModel = mModel;
            AppUtil.showOtherActivity(getContext(), OrderDetailActivity.class, 0);
        });
    }

    public OrderCell(Context context, OrderModel model) {
        super(context);

        mModel = model;

        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.ui_order_cell, this, true);

        initUIView();
        initWithEvent();
    }

    private void initUIView() {
        lbl_name = findViewById(R.id.lbl_name);
        lbl_date = findViewById(R.id.lbl_time);

        lbl_address = findViewById(R.id.lbl_order_adderess);
        lbl_info= findViewById(R.id.lbl_order_info);
        img_avatar = findViewById(R.id.img_avatar);

        lbl_status = findViewById(R.id.lbl_order_status);
        viw_statu = findViewById(R.id.viw_order_statu);

        sch_images = findViewById(R.id.sch_order_images);
        llt_images = findViewById(R.id.llt_order_images);

        initWithData();
    }

    private void initWithData() {
        if (mModel.doctorid.length() > 0) {
            lbl_status.setText(getContext().getString(R.string.normal_pending));
            lbl_status.setTextColor(getContext().getColor(R.color.colorRed));
            viw_statu.setBackgroundResource(R.drawable.back_red_circle);
        } else {
            lbl_status.setText(getContext().getString(R.string.normal_ready));
            lbl_status.setTextColor(getContext().getColor(R.color.colorGreen));
            viw_statu.setBackgroundResource(R.drawable.back_green_circle);
        }

        lbl_date.setText(TimerUtil.getDelayTime(mModel.datetime, "yyyy-MM-dd HH:mm:ss"));

        if (mModel.imgurls.size() == 0) {
            sch_images.setVisibility(View.GONE);
        } else {
            for (String url: mModel.imgurls) {
                LayoutParams imParams =
                        new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                imParams.setMargins(8, 8, 8, 8);
                ImageView img_patient = new ImageView(getContext());
                img_patient.setLayoutParams(imParams);
                img_patient.setAdjustViewBounds(true);
                Picasso.get().load(url)
                        .placeholder(R.drawable.ic_image)
                        .error(R.drawable.ic_image)
                        .into(img_patient, null);
                img_patient.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), ImageDetailActivity.class);
                    intent.putExtra("url", url);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
                    getContext().startActivity(intent, options.toBundle());
                });
                llt_images.addView(img_patient);
            }
        }

        lbl_info.setText(mModel.desc);

        UserModel.getUserByID(mModel.userid, new UserModel.UserModelInterface() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(UserModel userModel) {
                String upperString = "Px .";
                String[] names = userModel.name.split(" ");
                for (String name: names) {
                    upperString = upperString + " " + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                }
                lbl_name.setText(upperString);
                Picasso.get().load(userModel.imgurl)
                        .placeholder(R.drawable.ic_account_circle)
                        .error(R.drawable.ic_account_circle)
                        .into(img_avatar, null);

                DirectionModel.getCheckModelByUserID(userModel.id, new DirectionModel.DirectionModelListener() {
                    @Override
                    public void onSuccess(DirectionModel model) {
                        if (model.address1.length() > 0) {
                            lbl_address.setText(model.address1);
                        } else {
                            lbl_address.setText("----------");
                        }
                    }

                    @Override
                    public void onFailed(String error) {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
