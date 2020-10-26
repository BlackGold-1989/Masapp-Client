package com.laodev.masapp.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.laodev.masapp.R;
import com.laodev.masapp.activity.ImageDetailActivity;
import com.laodev.masapp.activity.buyer.RequestDetailActivity;
import com.laodev.masapp.activity.seller.NavigationActivity;
import com.laodev.masapp.model.DirectionModel;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.LocationUtil;
import com.laodev.masapp.util.TimerUtil;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AcceptOrderCell extends LinearLayout {

    private OrderModel mModel;
    private UserModel mUserModel = new UserModel();

    private CircleImageView img_avatar;
    private TextView lbl_name, lbl_date, lbl_address, lbl_gender, lbl_birth, lbl_info;
    private LinearLayout llt_images;
    private HorizontalScrollView sch_images;
    private Button btn_call, btn_navi;

    private AcceptOrderCellCallback acceptOrderCellCallback;


    private void initWithEvent() {
        btn_call.setOnClickListener(v -> acceptOrderCellCallback.onClickCallBtn(mUserModel));
        btn_navi.setOnClickListener(v -> {
            AppUtil.gOrderModel = mModel;
            AppUtil.showOtherActivity(getContext(), NavigationActivity.class, 0);
        });
    }

    public AcceptOrderCell(Context context, OrderModel model) {
        super(context);

        mModel = model;
        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.ui_accept_order_cell, this, true);

        initUIView();
        initWithEvent();
    }

    private void initUIView() {
        img_avatar = findViewById(R.id.civ_order_avatar);

        lbl_name = findViewById(R.id.lbl_order_name);
        lbl_date = findViewById(R.id.lbl_order_time);
        lbl_address = findViewById(R.id.lbl_order_adderess);
        lbl_birth = findViewById(R.id.lbl_order_birth);
        lbl_gender = findViewById(R.id.lbl_order_gender);
        lbl_info = findViewById(R.id.lbl_order_info);

        sch_images = findViewById(R.id.sch_order_images);
        llt_images = findViewById(R.id.llt_order_images);

        btn_call = findViewById(R.id.btn_order_call);
        btn_navi = findViewById(R.id.btn_order_navi);

        initWithData();
    }

    private void initWithData() {
        lbl_date.setText(TimerUtil.getDelayTime(mModel.datetime, "yyyy-MM-dd HH:mm:ss"));
        lbl_info.setText(mModel.desc);
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

        DirectionModel.getCheckModelByUserID(mModel.userid, new DirectionModel.DirectionModelListener() {
            @Override
            public void onSuccess(DirectionModel direction) {
                lbl_address.setText(direction.address1 + ", " + direction.address2);
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        if (mModel.location.length() > 0) {
            double lat = Double.valueOf(mModel.location.split(",")[0]);
            double lng = Double.valueOf(mModel.location.split(",")[1]);
            lbl_address.setText(LocationUtil.getAddressFromLatlng(getContext(), lat, lng));
        } else {
            lbl_address.setText("----------");
        }

        UserModel.getUserByID(mModel.userid, new UserModel.UserModelInterface() {
            @Override
            public void onSuccess(UserModel userModel) {
                mUserModel = userModel;

                lbl_name.setText(userModel.name);
                lbl_gender.setText(userModel.gender);
                lbl_birth.setText(userModel.birth);
                Picasso.get().load(userModel.imgurl).fit().centerCrop()
                        .placeholder(R.drawable.ic_account_circle)
                        .error(R.drawable.ic_account_circle)
                        .into(img_avatar, null);
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setAcceptOrderCellCallback(AcceptOrderCellCallback acceptOrderCellCallback) {
        this.acceptOrderCellCallback = acceptOrderCellCallback;
    }

    public interface AcceptOrderCellCallback {
        void onClickCallBtn(UserModel userModel);
    }

}
