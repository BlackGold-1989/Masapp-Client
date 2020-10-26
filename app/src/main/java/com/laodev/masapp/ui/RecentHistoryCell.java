package com.laodev.masapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.laodev.masapp.R;
import com.laodev.masapp.model.HistoryModel;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentHistoryCell extends LinearLayout {

    private HistoryModel historyModel = new HistoryModel();

    private TextView lbl_name, lbl_date, lbl_comment;
    private List<ImageView> aryStars = new ArrayList<>();
    private CircleImageView civ_avatar;


    public RecentHistoryCell(Context context, HistoryModel historyModel) {
        super(context);

        this.historyModel = historyModel;

        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.ui_recent_history_cell, this, true);

        initView();
    }

    private void initView() {
        lbl_name = findViewById(R.id.lbl_name);
        lbl_date = findViewById(R.id.lbl_date);
        lbl_comment = findViewById(R.id.lbl_comment);
        civ_avatar = findViewById(R.id.civ_avatar);

        ImageView img_star_01 = findViewById(R.id.img_star_01);
        ImageView img_star_02 = findViewById(R.id.img_star_02);
        ImageView img_star_03 = findViewById(R.id.img_star_03);
        ImageView img_star_04 = findViewById(R.id.img_star_04);
        ImageView img_star_05 = findViewById(R.id.img_star_05);
        aryStars.add(img_star_01);
        aryStars.add(img_star_02);
        aryStars.add(img_star_03);
        aryStars.add(img_star_04);
        aryStars.add(img_star_05);

        initData();
    }

    private void initData() {
        lbl_date.setText(historyModel.orderModel.endtime.split(" ")[0]);
        lbl_comment.setText(historyModel.comment);
        for (int i = 0; i < historyModel.rating; i++) {
            ImageView img_start = aryStars.get(i);
            img_start.setImageResource(R.drawable.ic_star_fill);
        }
        String userid = historyModel.orderModel.userid;
        if (AppUtil.gCurrentUser.type.equals(Constants.USER_SELLER)) {
            userid = historyModel.orderModel.doctorid;
        }
        UserModel.getUserByID(userid, new UserModel.UserModelInterface() {
            @Override
            public void onSuccess(UserModel user) {
                lbl_name.setText(user.name);
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
    }


}
