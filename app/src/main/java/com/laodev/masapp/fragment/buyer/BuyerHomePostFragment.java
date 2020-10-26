package com.laodev.masapp.fragment.buyer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.laodev.masapp.R;
import com.laodev.masapp.activity.MainActivity;
import com.laodev.masapp.activity.buyer.AddLocationActivity;
import com.laodev.masapp.callback.CropImagePickerCallback;
import com.laodev.masapp.model.DirectionModel;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.ui.OrderImageCell;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;
import com.laodev.masapp.util.TimerUtil;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;

import static com.laodev.masapp.util.Constants.ADD_DIRECTION_REQUEST;

public class BuyerHomePostFragment extends Fragment {

    private MainActivity mActivity;

    private LinearLayout llt_images;
    private EditText txt_content;

    private List<Bitmap> aryBmps = new ArrayList<>();

    private String checkLocation = "";
    private int uploadCount = 0;


    public BuyerHomePostFragment(MainActivity activity) {
        mActivity = activity;
        mActivity.setImagePickerCallback(new CropImagePickerCallback() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                aryBmps.add(bitmap);
                onRefreshOrderImages();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
            }
        });
        mActivity.setDirectionCallback(model -> {
            List<DirectionModel> directionModelList = new ArrayList<>();
            directionModelList.add(model);
            DirectionModel.updateLocationsToFB(directionModelList, new DirectionModel.DirectionModelListener() {
                @Override
                public void onSuccess(List<DirectionModel> models) {
                    checkLocation = model.location;
                }

                @Override
                public void onFailed(String error) {
                    Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_home_post, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        llt_images = view.findViewById(R.id.llt_images);
        txt_content = view.findViewById(R.id.txt_content);
        CheckBox chk_agree = view.findViewById(R.id.chk_agree);
        chk_agree.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                ProgressDialog dialog = ProgressDialog.show(mActivity, "", getString(R.string.pgr_connect_server));
                DirectionModel.getCheckModelByUserID(AppUtil.gCurrentUser.id, new DirectionModel.DirectionModelListener() {
                    @Override
                    public void onSuccess(DirectionModel model) {
                        dialog.dismiss();
                        checkLocation = model.location;
                    }

                    @Override
                    public void onFailed(String error) {
                        dialog.dismiss();
                        if (error.equals("No Found")) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                            alert.setMessage("¿Quieres registrar una ubicación ahora?");
                            alert.setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
                                AppUtil.REQUESTCODE = ADD_DIRECTION_REQUEST;
                                startActivityForResult(new Intent(mActivity, AddLocationActivity.class), ADD_DIRECTION_REQUEST);
                            });
                            alert.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> chk_agree.setChecked(false));
                            alert.show();
                        } else {
                            Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        Button btn_submit = view.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(v -> {
            if (Integer.parseInt(AppUtil.gCurrentUser.coins) == 0) {
                Toast.makeText(mActivity, R.string.toast_no_coin, Toast.LENGTH_SHORT).show();
                return;
            }
            String desc = txt_content.getText().toString();
            if (desc.length() == 0) {
                Toast.makeText(mActivity, R.string.buyer_home_no_desc, Toast.LENGTH_SHORT).show();
                return;
            }
            OrderModel orderModel = new OrderModel();
            if (!chk_agree.isChecked()) {
                orderModel.location = checkLocation;
            }
            orderModel.title = AppUtil.gCurrentUser.name;
            orderModel.desc = txt_content.getText().toString();
            orderModel.datetime = TimerUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
            orderModel.userid = AppUtil.gCurrentUser.id;
            orderModel.doctorid = "";

            ProgressDialog dialog = ProgressDialog.show(mActivity, "", getString(R.string.pgr_connect_server));
            orderModel.addOrderToFB(new OrderModel.OrderModelCallback() {
                @Override
                public void onSuccess(OrderModel orderModel) {
                    if (aryBmps.size() > 0) {
                        uploadCount = 0;
                        for (Bitmap bitmap: aryBmps) {
                            orderModel.uploadProfile(bitmap, new OrderModel.OrderModelCallback() {
                                @Override
                                public void onSuccess(String url) {
                                    uploadCount++;
                                    orderModel.imgurls.add(url);
                                    if (uploadCount == aryBmps.size()) {
                                        orderModel.updateOrderToFB(new OrderModel.OrderModelCallback() {
                                            @Override
                                            public void onSuccess(OrderModel orderModel) {
                                                dialog.dismiss();
                                                AppUtil.gCurrentUser.status = Constants.STATUS_PENDDING;
                                                AppUtil.gCurrentUser.coins = String.valueOf(Integer.parseInt(AppUtil.gCurrentUser.coins) - 1);
                                                AppUtil.gCurrentUser.addUserToFB(new UserModel.UserModelInterface() {
                                                    @Override
                                                    public void onSuccess(UserModel user) {
                                                        mActivity.bottomNavigation.setSelectedItemId(R.id.action_home);
                                                    }

                                                    @Override
                                                    public void onFailed(String error) {
                                                        mActivity.bottomNavigation.setSelectedItemId(R.id.action_home);
                                                        Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailed(String error) {
                                                dialog.dismiss();
                                                Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailed(String error) {
                                    uploadCount++;
                                }
                            });
                        }
                    } else {
                        dialog.dismiss();

                        AppUtil.gCurrentUser.status = Constants.STATUS_PENDDING;
                        AppUtil.gCurrentUser.coins = String.valueOf(Integer.parseInt(AppUtil.gCurrentUser.coins) - 1);
                        AppUtil.gCurrentUser.addUserToFB(new UserModel.UserModelInterface() {
                            @Override
                            public void onSuccess(UserModel user) {
                                mActivity.bottomNavigation.setSelectedItemId(R.id.action_home);
                            }

                            @Override
                            public void onFailed(String error) {
                                mActivity.bottomNavigation.setSelectedItemId(R.id.action_home);
                                Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailed(String error) {
                    dialog.dismiss();
                    Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
        TextView lbl_pick_iamge = view.findViewById(R.id.lbl_pick_iamge);
        lbl_pick_iamge.setOnClickListener(view1 -> CropImage.activity()
                .setMaxCropResultSize(2048, 2048)
                .setAutoZoomEnabled(false)
                .start(mActivity));

        onRefreshOrderImages();
    }

    private void onRefreshOrderImages() {
        llt_images.removeAllViews();
        if (aryBmps.size() > 0) {
            llt_images.setVisibility(View.VISIBLE);
        } else {
            llt_images.setVisibility(View.GONE);
        }
        for (int i = 0; i < aryBmps.size(); i++) {
            Bitmap bitmap = aryBmps.get(i);
            OrderImageCell imageCell = new OrderImageCell(mActivity, bitmap, i, index -> {
                aryBmps.remove(index);
                onRefreshOrderImages();
            });
            llt_images.addView(imageCell);
        }
    }

}
