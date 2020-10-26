package com.laodev.masapp.activity.seller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;
import com.laodev.masapp.model.DirectionModel;
import com.laodev.masapp.model.HistoryModel;
import com.laodev.masapp.model.RequestModel;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.ui.OrderImageCell;
import com.laodev.masapp.ui.RecentHistoryCell;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;
import com.laodev.masapp.util.FireManager;
import com.laodev.masapp.util.TimerUtil;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderDetailActivity extends BaseActivity {

    private CircleImageView civ_avatar;
    private TextView lbl_name, lbl_time, lbl_status, lbl_gender, lbl_birth, lbl_address, lbl_order_rate, lbl_content, lbl_reviews;
    private EditText txt_content;
    private View viw_statu;
    private HorizontalScrollView sch_images;
    private LinearLayout llt_images, llt_post, llt_post_images, llt_reviews;
    private Button btn_post;

    private List<ImageView> aryStars = new ArrayList<>();
    private List<Bitmap> aryBmps = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        getWindow().setStatusBarColor(getColor(R.color.colorBlue));

        initView();
    }

    private void initView() {
        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(view -> onBackPressed());

        civ_avatar = findViewById(R.id.civ_avatar);
        lbl_name = findViewById(R.id.lbl_name);
        lbl_time = findViewById(R.id.lbl_time);
        lbl_status = findViewById(R.id.lbl_order_status);
        viw_statu = findViewById(R.id.viw_order_statu);
        lbl_gender = findViewById(R.id.lbl_gender);
        lbl_birth = findViewById(R.id.lbl_birth);
        lbl_address = findViewById(R.id.lbl_address);
        lbl_order_rate = findViewById(R.id.lbl_order_rate);

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

        sch_images = findViewById(R.id.sch_images);
        llt_images = findViewById(R.id.llt_images);
        txt_content = findViewById(R.id.txt_content);

        lbl_content = findViewById(R.id.lbl_content);
        lbl_reviews = findViewById(R.id.lbl_reviews);

        llt_post = findViewById(R.id.llt_post);
        llt_post.setVisibility(View.GONE);
        btn_post = findViewById(R.id.btn_post);
        btn_post.setOnClickListener(view -> {
            llt_post.setVisibility(View.VISIBLE);
            btn_post.setVisibility(View.GONE);
        });

        TextView lbl_add_images = findViewById(R.id.lbl_add_images);
        lbl_add_images.setOnClickListener(view -> CropImage.activity()
                .setMaxCropResultSize(2048, 2048)
                .setAutoZoomEnabled(false)
                .start(OrderDetailActivity.this));
        llt_post_images = findViewById(R.id.llt_post_images);

        Button btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(view -> {
            String content = txt_content.getText().toString();
            if (content.isEmpty()) {
                Toast.makeText(OrderDetailActivity.this, R.string.toast_no_content, Toast.LENGTH_SHORT).show();
                return;
            }
            RequestModel model = new RequestModel();
            model.desc = content;
            model.orderid = AppUtil.gOrderModel.id;
            model.user = AppUtil.gCurrentUser;
            model.datetime = TimerUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
            ProgressDialog dialog = ProgressDialog.show(OrderDetailActivity.this, "", getString(R.string.pgr_connect_server));
            model.submitRequest(new RequestModel.RequestModelCallback() {
                @Override
                public void onSuccess(RequestModel model) {
                    if (aryBmps.size() > 0) {
                        model.imgurls.clear();
                        for (Bitmap bitmap: aryBmps) {
                            RequestModel.uploadImage(bitmap, new RequestModel.RequestModelCallback() {
                                @Override
                                public void onSuccess(String url) {
                                    model.imgurls.add(url);
                                    if (model.imgurls.size() == aryBmps.size()) {
                                        dialog.dismiss();
                                        model.updateRequest(new RequestModel.RequestModelCallback() {
                                            @Override
                                            public void onSuccess(RequestModel model) {
                                                dialog.dismiss();
                                                onBackPressed();

                                            }

                                            @Override
                                            public void onFailed(String error) {
                                                dialog.dismiss();
                                                Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailed(String error) {
                                    dialog.dismiss();
                                    Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        FireManager.sendPushNotificationToUser(AppUtil.gOrderModel.userid, "Request"
                                , AppUtil.gCurrentUser.name + " just sent a proposal for you."
                                , new FireManager.PushNotificationCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Toast.makeText(OrderDetailActivity.this, result, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        dialog.dismiss();
                        onBackPressed();
                    }
                }

                @Override
                public void onFailed(String error) {
                    dialog.dismiss();
                    Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
        llt_reviews = findViewById(R.id.llt_reviews);

        initData();
    }

    private void initData() {
        lbl_time.setText(TimerUtil.getDelayTime(AppUtil.gOrderModel.datetime, "yyyy-MM-dd HH:mm:ss"));
        lbl_content.setText(AppUtil.gOrderModel.desc);
        if (AppUtil.gOrderModel.doctorid.length() > 0) {
            lbl_status.setText(Constants.ORDER_UNABLEED);
            lbl_status.setTextColor(getColor(R.color.colorRed));
            viw_statu.setBackgroundResource(R.drawable.back_red_circle);
            Toast.makeText(OrderDetailActivity.this, R.string.toast_alrady_accepted, Toast.LENGTH_SHORT).show();

            btn_post.setVisibility(View.GONE);
        } else {
            RequestModel.getAllRequests(AppUtil.gOrderModel.id, new RequestModel.RequestModelCallback() {
                @Override
                public void onSuccess(List<RequestModel> models) {
                    boolean isBidded = false;
                    for (RequestModel model: models) {
                        if (model.user.id.equals(AppUtil.gCurrentUser.id)) {
                            isBidded = true;
                            break;
                        }
                    }
                    if (isBidded) {
                        lbl_status.setText(Constants.ORDER_UNABLEED);
                        lbl_status.setTextColor(getColor(R.color.colorRed));
                        viw_statu.setBackgroundResource(R.drawable.back_red_circle);
                        btn_post.setVisibility(View.GONE);

                        Toast.makeText(OrderDetailActivity.this, R.string.toast_alrady_submit, Toast.LENGTH_SHORT).show();
                    } else {
                        lbl_status.setText(Constants.ORDER_AVAILABLE);
                        lbl_status.setTextColor(getColor(R.color.colorGreen));
                        viw_statu.setBackgroundResource(R.drawable.back_green_circle);
                    }
                }

                @Override
                public void onFailed(String error) {
                    Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (AppUtil.gOrderModel.imgurls.size() == 0) {
            sch_images.setVisibility(View.GONE);
        } else {
            for (String url: AppUtil.gOrderModel.imgurls) {
                LinearLayout.LayoutParams imParams =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                imParams.setMargins(8, 8, 8, 8);
                ImageView img_patient = new ImageView(this);
                img_patient.setLayoutParams(imParams);
                img_patient.setAdjustViewBounds(true);
                Picasso.get().load(url)
                        .placeholder(R.drawable.ic_image)
                        .error(R.drawable.ic_image)
                        .into(img_patient, null);
                img_patient.setOnClickListener(v -> {
//                    AppUtil.gSelImageUrl = url;
//                    AppUtil.showOtherActivity(getContext(), ImageDetailActivity.class, 0);
                });
                llt_images.addView(img_patient);
            }
        }

        ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.pgr_connect_server));
        UserModel.getUserByID(AppUtil.gOrderModel.userid, new UserModel.UserModelInterface() {
            @Override
            public void onSuccess(UserModel user) {
                dialog.dismiss();

                Picasso.get().load(user.imgurl).fit().centerCrop()
                        .placeholder(R.drawable.ic_account_circle)
                        .error(R.drawable.ic_account_circle)
                        .into(civ_avatar, null);

                String upperString = "Px .";
                String[] names = user.name.split(" ");
                for (String name: names) {
                    upperString = upperString + " " + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                }
                lbl_name.setText(upperString);

                lbl_gender.setText(user.gender);
                lbl_birth.setText(user.birth);

                DirectionModel.getCheckModelByUserID(user.id, new DirectionModel.DirectionModelListener() {
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
                        Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });

                HistoryModel.getRecentHistories(user, new HistoryModel.HistoryModelCallback() {
                    @Override
                    public void onSuccess(List<HistoryModel> historyModels, String average) {
                        lbl_order_rate.setText(average);
                        if (historyModels.size() == 0) {
                            lbl_reviews.setText(average);
                        } else {
                            for (int i = 0; i < (int)(Float.parseFloat(average) + 0.5); i++) {
                                ImageView img_star = aryStars.get(i);
                                img_star.setImageResource(R.drawable.ic_star_fill);
                            }
                            llt_reviews.removeAllViews();
                            for (HistoryModel historyModel: historyModels) {
                                RecentHistoryCell historyCell = new RecentHistoryCell(OrderDetailActivity.this, historyModel);
                                llt_reviews.addView(historyCell);
                            }
                        }
                    }

                    @Override
                    public void onFailed(String error) {
                        Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(OrderDetailActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onRefreshOrderImages() {
        llt_post_images.removeAllViews();
        for (int i = 0; i < aryBmps.size(); i++) {
            Bitmap bitmap = aryBmps.get(i);
            OrderImageCell imageCell = new OrderImageCell(this, bitmap, i, index -> {
                aryBmps.remove(index);
                onRefreshOrderImages();
            });
            llt_post_images.addView(imageCell);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    aryBmps.add(bitmap);
                    onRefreshOrderImages();
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
