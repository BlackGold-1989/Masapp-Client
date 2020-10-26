package com.laodev.masapp.activity.buyer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;
import com.laodev.masapp.dialog.AcceptRequestDialog;
import com.laodev.masapp.model.HistoryModel;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.ui.ProfileImageCell;
import com.laodev.masapp.ui.RecentHistoryCell;
import com.laodev.masapp.ui.TagCell;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;
import com.laodev.masapp.util.FireManager;
import com.laodev.masapp.util.TimerUtil;
import com.liangfeizc.flowlayout.FlowLayout;
import com.sinch.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryDetailActivity extends BaseActivity {

    private HistoryModel mHistoryModel = new HistoryModel();

    private TextView lbl_name, lbl_license, lbl_address, lbl_gender, lbl_status, lbl_rate, lbl_reviews;
    private FlowLayout flw_tag_kind;
    private CircleImageView civ_avatar;
    private LinearLayout llt_reviews, llt_profile;
    private View viw_statu;

    private List<ImageView> aryStarImgs = new ArrayList<>();

    private int acceptCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        getWindow().setStatusBarColor(getColor(R.color.colorBlue));

        Intent intent = getIntent();
        String jsonStr = intent.getStringExtra("model");
        mHistoryModel = new Gson().fromJson(jsonStr, HistoryModel.class);

        initUIView();
    }

    private void initUIView() {
        civ_avatar = findViewById(R.id.civ_avatar);
        lbl_name = findViewById(R.id.lbl_name);
        lbl_license = findViewById(R.id.lbl_license);
        lbl_address = findViewById(R.id.lbl_address);
        lbl_gender = findViewById(R.id.lbl_gender);
        lbl_status = findViewById(R.id.lbl_status);
        lbl_rate = findViewById(R.id.lbl_rate);

        ImageView img_star_01 = findViewById(R.id.img_star_01);
        ImageView img_star_02 = findViewById(R.id.img_star_02);
        ImageView img_star_03 = findViewById(R.id.img_star_03);
        ImageView img_star_04 = findViewById(R.id.img_star_04);
        ImageView img_star_05 = findViewById(R.id.img_star_05);
        aryStarImgs.add(img_star_01);
        aryStarImgs.add(img_star_02);
        aryStarImgs.add(img_star_03);
        aryStarImgs.add(img_star_04);
        aryStarImgs.add(img_star_05);

        flw_tag_kind = findViewById(R.id.flw_tag_kind);
        llt_profile = findViewById(R.id.llt_profile);

        lbl_reviews = findViewById(R.id.lbl_reviews);
        llt_reviews = findViewById(R.id.llt_reviews);

        viw_statu = findViewById(R.id.viw_statu);

        initData();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        UserModel.getUserByID(mHistoryModel.orderModel.doctorid, new UserModel.UserModelInterface() {
            @Override
            public void onSuccess(UserModel user) {
                lbl_name.setText("Dr . " + user.name);
                Picasso.get().load(user.imgurl)
                        .placeholder(R.drawable.ic_account_circle)
                        .error(R.drawable.ic_account_circle)
                        .into(civ_avatar, null);
                lbl_license.setText(user.spec2);
                lbl_address.setText(user.address1 + ", " + user.address2);
                lbl_gender.setText(user.gender);

//                for (int i = 0; i < user.images.size(); i++) {
//                    String url = user.images.get(i);
//                    Picasso.get().load(url)
//                            .placeholder(R.drawable.ic_image)
//                            .error(R.drawable.ic_image)
//                            .into(aryProfileImgs.get(i), null);
//                }
                initProfileImageData(user.images);

                flw_tag_kind.removeAllViews();
                if (user.spec1.isEmpty()) {
                    flw_tag_kind.setVisibility(View.GONE);
                } else {
                    flw_tag_kind.setVisibility(View.VISIBLE);
                    String[] specs = user.spec1.split(",");
                    for (String spec: specs) {
                        TagCell tagCell = new TagCell(HistoryDetailActivity.this);
                        tagCell.setTitle(spec);
                        flw_tag_kind.addView(tagCell);
                    }
                }

                HistoryModel.getRecentHistories(user, new HistoryModel.HistoryModelCallback() {
                    @Override
                    public void onSuccess(List<HistoryModel> historyModels, String average) {
                        lbl_rate.setText(average);
                        if (historyModels.size() == 0) {
                            lbl_reviews.setText(average);
                        } else {
                            for (int i = 0; i < (int)(Float.parseFloat(average) + 0.5); i++) {
                                ImageView img_star = aryStarImgs.get(i);
                                img_star.setImageResource(R.drawable.ic_star_fill);
                            }
                            llt_reviews.removeAllViews();
                            for (HistoryModel historyModel: historyModels) {
                                RecentHistoryCell historyCell = new RecentHistoryCell(HistoryDetailActivity.this, historyModel);
                                llt_reviews.addView(historyCell);
                            }
                        }
                    }

                    @Override
                    public void onFailed(String error) {
                        Toast.makeText(HistoryDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(HistoryDetailActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        OrderModel.getAllOrdersByID(mHistoryModel.orderModel.doctorid, new OrderModel.OrderModelCallback() {
            @Override
            public void onSuccess(List<OrderModel> orderModels) {
                if (orderModels.size() > 2) {
                    viw_statu.setBackground(ContextCompat.getDrawable(HistoryDetailActivity.this, R.drawable.back_red_circle));
                    lbl_status.setText("Unavailable");
                }
                acceptCount = orderModels.size();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(HistoryDetailActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initProfileImageData(List<String> urls) {
        llt_profile.removeAllViews();
        for (int i = 0; i < (urls.size() + 2) / 3; i++) {
            List<String> lst_urls = new ArrayList<>();
            String url = urls.get(i * 3);
            lst_urls.add(url);
            if (i * 3 + 1 < urls.size()) {
                String second_url = urls.get(i * 3 + 1);
                lst_urls.add(second_url);
            }
            if (i * 3 + 2 < urls.size()) {
                String second_url = urls.get(i * 3 + 2);
                lst_urls.add(second_url);
            }
            ProfileImageCell imageCell = new ProfileImageCell(this, lst_urls, false);
            llt_profile.addView(imageCell);
        }
    }

    private void onAcceptRequest() {
        if (Integer.parseInt(AppUtil.gCurrentUser.coins) == 0) {
            Toast.makeText(this, R.string.toast_no_coin, Toast.LENGTH_SHORT).show();
            return;
        }

        OrderModel orderModel = new OrderModel();
        orderModel.userid = AppUtil.gCurrentUser.id;
        orderModel.doctorid = mHistoryModel.orderModel.doctorid;
        orderModel.title = AppUtil.gCurrentUser.name;
        orderModel.desc = "Te contraté de nuevo para el tratamiento.";
        orderModel.datetime = TimerUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
        orderModel.starttime = TimerUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
        orderModel.location = AppUtil.gCurrentUser.location;

        ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.pgr_connect_server));
        orderModel.addOrderToFB(new OrderModel.OrderModelCallback() {
            @Override
            public void onSuccess(OrderModel orderModel) {
                dialog.dismiss();

                AppUtil.gCurrentUser.status = Constants.STATUS_ACCEPT;
                AppUtil.gCurrentUser.coins = String.valueOf(Integer.parseInt(AppUtil.gCurrentUser.coins) - 1);
                AppUtil.gCurrentUser.addUserToFB(new UserModel.UserModelInterface() {
                    @Override
                    public void onSuccess(UserModel user) {
                        FireManager.sendPushNotificationToUser(orderModel.doctorid, "Accept"
                                , AppUtil.gCurrentUser.name + " just accept your proposal."
                                , new FireManager.PushNotificationCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        onBackPressed();
                                        Toast.makeText(HistoryDetailActivity.this, result, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        Toast.makeText(HistoryDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailed(String error) {
                        Toast.makeText(HistoryDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(HistoryDetailActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickBackImg(View view) {
        onBackPressed();
    }

    public void onClickAcceptBtn(View view) {
        if (!AppUtil.gCurrentUser.status.equals(Constants.STATUS_READY)) {
            Toast.makeText(this, "Ya publicaste un pedido.", Toast.LENGTH_SHORT).show();
            return;
        }
        AcceptRequestDialog dialog = new AcceptRequestDialog(this, acceptCount);
        dialog.setAcceptRequestDialogListener(this::onAcceptRequest);
        dialog.show();
    }

}
