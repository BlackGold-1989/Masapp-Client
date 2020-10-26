package com.laodev.masapp.fragment.buyer;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.laodev.masapp.R;
import com.laodev.masapp.activity.ImageDetailActivity;
import com.laodev.masapp.activity.MainActivity;
import com.laodev.masapp.activity.buyer.RequestDetailActivity;
import com.laodev.masapp.callback.OnCallingListener;
import com.laodev.masapp.dialog.ReviewDialog;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;
import com.laodev.masapp.util.FireManager;
import com.laodev.masapp.util.Helper;
import com.laodev.masapp.util.LocationUtil;
import com.laodev.masapp.util.TimerUtil;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyerOrderFragment extends Fragment {

    private MainActivity mActivity;

    private CircleImageView civ_avatar;
    private TextView lbl_name, lbl_location, lbl_time, lbl_content;
    private HorizontalScrollView sch_images;
    private LinearLayout llt_images;
    private Button btn_call, btn_complete;

    private UserModel sellerUser = new UserModel();
    private OnCallingListener onCallingListener;


    public BuyerOrderFragment(MainActivity activity) {
        mActivity = activity;
    }

    private void initEvent() {
        btn_call.setOnClickListener(view -> onCallingListener.onVoiceCallingEvent(sellerUser));
        btn_complete.setOnClickListener(view -> {
            ReviewDialog dialog = new ReviewDialog(mActivity);
            dialog.setReviewDialogListener(() -> {
                dialog.dismiss();

                AppUtil.gCurrentUser.status = Constants.STATUS_READY;
                AppUtil.gCurrentUser.addUserToFB(new UserModel.UserModelInterface() {
                    @Override
                    public void onSuccess(UserModel user) {
                        Helper.openPlayStore(mActivity);
                        mActivity.bottomNavigation.setSelectedItemId(R.id.action_home);
                    }

                    @Override
                    public void onFailed(String error) {

                    }
                });

                FireManager.sendPushNotificationToUser(sellerUser.id, "Review"
                        , AppUtil.gCurrentUser.name + " left some review for you."
                        , new FireManager.PushNotificationCallback() {
                            @Override
                            public void onSuccess(String result) {
                                Toast.makeText(mActivity, result, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailed(String error) {
                                Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                            }
                        });
            });
            dialog.show();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_order, container, false);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        civ_avatar = view.findViewById(R.id.civ_avatar);
        lbl_name = view.findViewById(R.id.lbl_name);
        lbl_location = view.findViewById(R.id.lbl_location);
        lbl_time = view.findViewById(R.id.lbl_time);
        lbl_content = view.findViewById(R.id.lbl_content);
        sch_images = view.findViewById(R.id.sch_images);
        llt_images = view.findViewById(R.id.llt_images);
        btn_call = view.findViewById(R.id.btn_call);
        btn_complete = view.findViewById(R.id.btn_complete);

        initData();
    }

    private void initData() {
        ProgressDialog dialog = ProgressDialog.show(mActivity, "", getString(R.string.pgr_connect_server));
        OrderModel.getOrderByUserID(AppUtil.gCurrentUser.id, new OrderModel.OrderModelCallback() {
            @Override
            public void onSuccess(OrderModel orderModel) {
                AppUtil.gOrderModel = orderModel;
                UserModel.getUserByID(orderModel.doctorid, new UserModel.UserModelInterface() {
                    @Override
                    public void onSuccess(UserModel user) {
                        dialog.dismiss();

                        setSellerData(user);
                    }

                    @Override
                    public void onFailed(String error) {
                        dialog.dismiss();
                        Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                    }
                });

                if (orderModel.imgurls.size() == 0) {
                    sch_images.setVisibility(View.GONE);
                } else {
                    for (String url: orderModel.imgurls) {
                        LinearLayout.LayoutParams imParams =
                                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        imParams.setMargins(8, 8, 8, 8);
                        ImageView img_request = new ImageView(mActivity);
                        img_request.setLayoutParams(imParams);
                        img_request.setAdjustViewBounds(true);
                        Picasso.get().load(url)
                                .placeholder(R.drawable.ic_image)
                                .error(R.drawable.ic_image)
                                .into(img_request, null);
                        img_request.setOnClickListener(v -> {
                            Intent intent = new Intent(mActivity, ImageDetailActivity.class);
                            intent.putExtra("url", url);
                            ActivityOptions options = ActivityOptions.makeCustomAnimation(mActivity, R.anim.slide_in_right, R.anim.slide_out_left);
                            startActivity(intent, options.toBundle());
                        });
                        llt_images.addView(img_request);
                    }
                }
                lbl_time.setText(TimerUtil.getDelayTime(orderModel.datetime, "yyyy-MM-dd HH:mm:ss"));
                lbl_content.setText(orderModel.desc);
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void setSellerData(UserModel user) {
        sellerUser = user;
        Picasso.get().load(sellerUser.imgurl)
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .into(civ_avatar, null);
        lbl_name.setText("Dr. " + sellerUser.name);
        lbl_location.setText(LocationUtil.getAddressFromString(mActivity, sellerUser.location));
    }

    public void setOnCallingListener(OnCallingListener onCallingListener) {
        this.onCallingListener = onCallingListener;
    }

}
