package com.laodev.masapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.laodev.masapp.R;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.model.UserModel;
import com.squareup.picasso.Picasso;

public class BuyerCell extends LinearLayout {

    private OrderModel orderModel;
    private UserModel user;

    private ImageView img_avatar;
    private TextView lbl_name;

    private BuyerCellCallback buyerCellCallback;


    public BuyerCell(Context context, OrderModel model) {
        super(context);

        orderModel = model;
        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.ui_buyer_cell, this, true);

        initUIView();
    }

    private void initUIView() {
        img_avatar = findViewById(R.id.img_order_avatar);
        lbl_name = findViewById(R.id.lbl_order_name);
        initWithData();
    }

    private void initWithData() {
        UserModel.getUserByID(orderModel.userid, new UserModel.UserModelInterface() {
            @Override
            public void onSuccess(UserModel userModel) {
                user = userModel;
                lbl_name.setText(userModel.name);
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

        this.setOnClickListener(view -> buyerCellCallback.onClickBuyer(user));
    }

    public void setBuyerCellCallback(BuyerCellCallback buyerCellCallback) {
        this.buyerCellCallback = buyerCellCallback;
    }

    public interface BuyerCellCallback {
        void onClickBuyer(UserModel user);
    }

}
