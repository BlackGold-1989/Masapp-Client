package com.laodev.masapp.fragment.buyer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.laodev.masapp.R;
import com.laodev.masapp.activity.MainActivity;
import com.laodev.masapp.activity.buyer.CommentActivity;
import com.laodev.masapp.activity.buyer.PaymentActivity;
import com.laodev.masapp.callback.CropImagePickerCallback;
import com.laodev.masapp.model.HistoryModel;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.ui.CustomEditText;
import com.laodev.masapp.ui.RecentHistoryCell;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;
import com.laodev.masapp.util.TimerUtil;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyerProfileFragment extends Fragment {

    private MainActivity mActivity;

    private CircleImageView civ_avatar;
    private ImageButton imb_add, imb_edit;
    private CustomEditText cet_name, cet_birth, cet_email;
    private Button btn_submit;
    private TextView lbl_reviews, lbl_coin, lbl_comment;
    private LinearLayout llt_reviews;
    private RadioGroup rdg_gender;

    private boolean isEditable = false;
    private Bitmap bitAvatar = null;
    private final Calendar mCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener mDateListener = (view, year, monthOfYear, dayOfMonth) -> {
        // TODO Auto-generated method stub
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        cet_birth.setInputText(sdf.format(mCalendar.getTime()));
    };


    public BuyerProfileFragment(MainActivity activity) {
        mActivity = activity;
        mActivity.setImagePickerCallback(new CropImagePickerCallback() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                bitAvatar = bitmap;
                civ_avatar.setImageBitmap(bitAvatar);
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initEvent() {
        imb_add.setOnClickListener(view -> {
            if (!isEditable) {
                Toast.makeText(mActivity, "Configure el modo editable.", Toast.LENGTH_SHORT).show();
                return;
            }
            CropImage.activity()
                    .setMaxCropResultSize(2048, 2048)
                    .setAutoZoomEnabled(false)
                    .start(mActivity);
        });
        imb_edit.setOnClickListener(view -> {
            isEditable = !isEditable;
            setEditableView();
        });
        btn_submit.setOnClickListener(view -> {
            UserModel userModel = AppUtil.gCurrentUser;
            String name = cet_name.getInputText();
            if (!name.isEmpty() && !name.equals(userModel.name)) {
                userModel.name = name;
            }
            String email = cet_email.getInputText();
            if (!email.isEmpty() && !email.equals(userModel.email) && email.contains("@")) {
                userModel.email = email;
            }
            String birth = cet_birth.getInputText();
            if (!birth.isEmpty()) {
                userModel.birth = birth;
            }
            int selectedId = rdg_gender.getCheckedRadioButtonId();
            if (selectedId == R.id.rdb_male) {
                userModel.gender = Constants.GENDER_MALE;
            } else {
                userModel.gender = Constants.GENDER_FEMALE;
            }
            ProgressDialog dialog = ProgressDialog.show(mActivity, "", getString(R.string.pgr_connect_server));
            userModel.addUserToFB(new UserModel.UserModelInterface() {
                @Override
                public void onSuccess(UserModel user) {
                    AppUtil.gCurrentUser = user;
                    if (bitAvatar != null) {
                        userModel.uploadAvatar(bitAvatar, new UserModel.UserModelInterface() {
                            @Override
                            public void onSuccess(UserModel user) {
                                dialog.dismiss();
                                Toast.makeText(mActivity, "Perfil actualizado exitoso", Toast.LENGTH_SHORT).show();

                                AppUtil.gCurrentUser = user;
                                isEditable = false;
                                initData();
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
                    dialog.dismiss();
                    Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
        cet_birth.setCustomEditTextCallback(new CustomEditText.CustomEditTextCallback() {
            @Override
            public void onClickEditText() {
                if (!isEditable) {
                    Toast.makeText(mActivity, "Configure el modo editable.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cet_birth.getInputText().length() > 0) {
                    mCalendar.setTime(TimerUtil.getDateByString("dd-MM-yyyy", cet_birth.getInputText()));
                }
                DatePickerDialog pickerDialog = new DatePickerDialog(mActivity, R.style.DialogTheme, mDateListener, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                pickerDialog.show();
            }
        });
        lbl_coin.setOnClickListener(view -> AppUtil.showOtherActivity(mActivity, PaymentActivity.class, 0));
        lbl_comment.setOnClickListener(view -> AppUtil.showOtherActivity(mActivity, CommentActivity.class, 0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_profile, container, false);
        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        lbl_coin = view.findViewById(R.id.lbl_coin);
        civ_avatar = view.findViewById(R.id.civ_avatar);
        imb_add = view.findViewById(R.id.imb_add);
        cet_name = view.findViewById(R.id.cet_name);
        cet_birth = view.findViewById(R.id.cet_birth);
        cet_birth.setDisEditable();
        cet_email = view.findViewById(R.id.cet_email);
        imb_edit = view.findViewById(R.id.imb_edit);
        btn_submit = view.findViewById(R.id.btn_submit);
        btn_submit.setVisibility(View.GONE);
        lbl_reviews = view.findViewById(R.id.lbl_reviews);
        llt_reviews = view.findViewById(R.id.llt_reviews);
        rdg_gender = view.findViewById(R.id.rdg_gender);
        lbl_comment = view.findViewById(R.id.lbl_comment);

        initData();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        Picasso.get()
                .load(AppUtil.gCurrentUser.imgurl)
                .centerCrop().fit()
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .into(civ_avatar);
        cet_name.setInputText(AppUtil.gCurrentUser.name);
        cet_birth.setInputText(AppUtil.gCurrentUser.birth);
        cet_email.setInputText(AppUtil.gCurrentUser.email);
        lbl_coin.setText("Monedas : " + AppUtil.gCurrentUser.coins);

        if (AppUtil.gCurrentUser.gender.equals(Constants.GENDER_MALE)) {
            rdg_gender.check(R.id.rdb_male);
        } else {
            rdg_gender.check(R.id.rdb_female);
        }

        HistoryModel.getRecentHistories(AppUtil.gCurrentUser, new HistoryModel.HistoryModelCallback() {
            @Override
            public void onSuccess(List<HistoryModel> historyModels, String average) {
                if (historyModels.size() == 0) {
                    lbl_reviews.setText(average);
                } else {
                    llt_reviews.removeAllViews();
                    for (HistoryModel historyModel: historyModels) {
                        RecentHistoryCell historyCell = new RecentHistoryCell(mActivity, historyModel);
                        llt_reviews.addView(historyCell);
                    }
                }
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
            }
        });

        setEditableView();
    }

    private void setEditableView() {
        cet_birth.setEnabled(isEditable);
        cet_email.setEnabled(isEditable);
        cet_name.setEnabled(isEditable);
        rdg_gender.setEnabled(isEditable);
        if (isEditable) {
            imb_edit.setVisibility(View.GONE);
            btn_submit.setVisibility(View.VISIBLE);
        } else {
            imb_edit.setVisibility(View.VISIBLE);
            btn_submit.setVisibility(View.GONE);
        }
    }

}
