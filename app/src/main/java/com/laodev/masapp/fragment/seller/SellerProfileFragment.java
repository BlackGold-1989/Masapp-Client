package com.laodev.masapp.fragment.seller;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.laodev.masapp.R;
import com.laodev.masapp.activity.MainActivity;
import com.laodev.masapp.activity.buyer.CommentActivity;
import com.laodev.masapp.activity.placespicker.PlacesPickerActivity;
import com.laodev.masapp.activity.seller.CardListActivity;
import com.laodev.masapp.callback.CropImagePickerCallback;
import com.laodev.masapp.model.HistoryModel;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.ui.CustomEditText;
import com.laodev.masapp.ui.ProfileImageCell;
import com.laodev.masapp.ui.RecentHistoryCell;
import com.laodev.masapp.ui.TagCell;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.LocationUtil;
import com.laodev.masapp.util.SharedPreferenceUtil;
import com.laodev.masapp.util.TimerUtil;
import com.liangfeizc.flowlayout.FlowLayout;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.laodev.masapp.util.Constants.PICK_CARD_REQUEST;
import static com.laodev.masapp.util.Constants.PICK_LOCATION_REQUEST;
import static com.laodev.masapp.util.AppUtil.REQUESTCODE;

public class SellerProfileFragment extends Fragment {

    private MainActivity mActivity;

    private TextView lbl_name, lbl_license, lbl_email, lbl_gender, lbl_rate, lbl_reviews, lbl_birth, lbl_address, lbl_pick_iamge, lbl_comment;
    private LinearLayout llt_reviews, llt_spec, llt_profile;
    private CircleImageView civ_avatar;
    private ImageButton imb_edit;
    private Button btn_post;
    private CustomEditText cet_license, cet_birth, cet_location, cet_phone, cet_credit, cet_city, cet_province;
    private FlowLayout flw_tag_kind;
    private List<ImageView> aryReviews = new ArrayList<>();
    private List<CustomEditText> arySpecViews = new ArrayList<>();

    private boolean isEditable = false;
    private int index = 0;
    private Bitmap[] profile_imgs = new Bitmap[3];
    private boolean[] isChangeProfiles = new boolean[] {false, false, false};
    private String latlng;
    private boolean isImageUpload = false;

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

    public SellerProfileFragment(MainActivity activity) {
        mActivity = activity;
    }

    private void initEvent() {
        mActivity.setImagePickerCallback(new CropImagePickerCallback() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                AppUtil.gCurrentUser.uploadProfile(bitmap, new UserModel.UserModelInterface() {
                    @Override
                    public void onSuccess(String url) {
                        initProfileImageData();
                    }

                    @Override
                    public void onFailed(String error) {
                        Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
            }
        });
        lbl_pick_iamge.setOnClickListener(view -> {
            if (AppUtil.gCurrentUser.images.size() == 9) {
                Toast.makeText(mActivity, "El recuento de imágenes de su perfil ya era limitado.", Toast.LENGTH_LONG);
                return;
            }
            pickImage();
        });
        mActivity.setLocationPickerCallback(latlng -> {
            this.latlng = latlng;
            cet_location.setInputText(LocationUtil.getAddressFromString(mActivity, latlng));
        });
        mActivity.setCardPickerCallback(cardModel -> cet_credit.setInputText(cardModel.getPublicCardNumber()));
        imb_edit.setOnClickListener(view -> {
            isEditable = !isEditable;
            setEditableView();
        });
        cet_birth.setCustomEditTextCallback(new CustomEditText.CustomEditTextCallback() {
            @Override
            public void onClickEditText() {
                if (cet_birth.getInputText().length() > 0) {
                    mCalendar.setTime(TimerUtil.getDateByString("dd-MM-yyyy", cet_birth.getInputText()));
                }
                DatePickerDialog pickerDialog = new DatePickerDialog(mActivity, R.style.DialogTheme, mDateListener, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                pickerDialog.show();
            }
        });
        cet_location.setCustomEditTextCallback(new CustomEditText.CustomEditTextCallback() {
            @Override
            public void onClickEditText() {
                REQUESTCODE = PICK_LOCATION_REQUEST;
                startActivityForResult(new Intent(getActivity(), PlacesPickerActivity.class), PICK_LOCATION_REQUEST);
            }
        });
        cet_credit.setCustomEditTextCallback(new CustomEditText.CustomEditTextCallback() {
            @Override
            public void onClickEditText() {
                REQUESTCODE = PICK_CARD_REQUEST;
                startActivityForResult(new Intent(getActivity(), CardListActivity.class), PICK_CARD_REQUEST);
            }
        });
        btn_post.setOnClickListener(view -> {
            btn_post.setEnabled(false);
            UserModel userModel = AppUtil.gCurrentUser;
            isImageUpload = false;
            for (int i = 0; i < profile_imgs.length; i++) {
                if (isChangeProfiles[i]) {
                    isImageUpload = true;
                    Bitmap bitmap = profile_imgs[i];
                    int finalI = i;
                    userModel.uploadProfile(bitmap, new UserModel.UserModelInterface() {
                        @Override
                        public void onSuccess(String url) {
                            if (userModel.images.size() > finalI) {
                                userModel.images.set(finalI, url);
                            } else {
                                userModel.images.add(url);
                            }
                            userModel.addUserToFB(new UserModel.UserModelInterface() {
                                @Override
                                public void onSuccess(UserModel user) {
                                    btn_post.setEnabled(true);
                                    AppUtil.gCurrentUser = user;
                                    Toast.makeText(mActivity, "Success update profile", Toast.LENGTH_SHORT).show();
                                    isEditable = false;
                                    initData();
                                    setEditableView();
                                }

                                @Override
                                public void onFailed(String error) {
                                    Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailed(String error) {
                            btn_post.setEnabled(true);
                            Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            StringBuilder spec1 = new StringBuilder(arySpecViews.get(0).getInputText());
            if (spec1.length() > 0) {
                for (int i = 1; i < arySpecViews.size(); i++) {
                    CustomEditText ilt_spec = arySpecViews.get(i);
                    if (ilt_spec.getInputText().isEmpty()) {
                        continue;
                    }
                    spec1.append(",").append(ilt_spec.getInputText());
                }
                userModel.spec1 = spec1.toString();
            }

            if (!cet_license.getInputText().equals(userModel.spec2) && !cet_license.getInputText().isEmpty()) {
                userModel.spec2 = cet_license.getInputText();
            }

            if (!cet_birth.getInputText().equals(userModel.birth) && !cet_birth.getInputText().isEmpty()) {
                userModel.birth = cet_birth.getInputText();
            }

            if (!cet_city.getInputText().equals(userModel.address1) && !cet_city.getInputText().isEmpty()) {
                userModel.address1 = cet_city.getInputText();
            }

            if (!cet_province.getInputText().equals(userModel.address2) && !cet_province.getInputText().isEmpty()) {
                userModel.address2 = cet_province.getInputText();
            }

            if (!userModel.location.equals(latlng)) {
                userModel.location = latlng;
            }

            userModel.addUserToFB(new UserModel.UserModelInterface() {
                @Override
                public void onSuccess(UserModel user) {
                    btn_post.setEnabled(true);
                    if (!isImageUpload) {
                        AppUtil.gCurrentUser = user;
                        Toast.makeText(mActivity, "Success update profile", Toast.LENGTH_SHORT).show();
                        isEditable = false;
                        initData();
                        setEditableView();
                    }
                }

                @Override
                public void onFailed(String error) {
                    btn_post.setEnabled(true);
                    Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
        lbl_comment.setOnClickListener(view -> AppUtil.showOtherActivity(mActivity, CommentActivity.class, 0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_profile, container, false);
        initView(view);
        initEvent();
        return view;
    }

    private void pickImage() {
        CropImage.activity()
                .setMaxCropResultSize(2048, 2048)
                .setAutoZoomEnabled(false)
                .start(mActivity);
    }

    private void initView(View view) {
        civ_avatar = view.findViewById(R.id.civ_avatar);
        lbl_name = view.findViewById(R.id.lbl_name);
        lbl_license = view.findViewById(R.id.lbl_license);
        lbl_email = view.findViewById(R.id.lbl_email);
        lbl_gender = view.findViewById(R.id.lbl_gender);
        lbl_rate = view.findViewById(R.id.lbl_rate);

        ImageView img_star_01 = view.findViewById(R.id.img_star_01);
        ImageView img_star_02 = view.findViewById(R.id.img_star_02);
        ImageView img_star_03 = view.findViewById(R.id.img_star_03);
        ImageView img_star_04 = view.findViewById(R.id.img_star_04);
        ImageView img_star_05 = view.findViewById(R.id.img_star_05);
        aryReviews.add(img_star_01);
        aryReviews.add(img_star_02);
        aryReviews.add(img_star_03);
        aryReviews.add(img_star_04);
        aryReviews.add(img_star_05);

        lbl_reviews = view.findViewById(R.id.lbl_reviews);
        llt_reviews = view.findViewById(R.id.llt_reviews);
        llt_profile = view.findViewById(R.id.llt_profile);
        lbl_pick_iamge = view.findViewById(R.id.lbl_pick_iamge);

        lbl_birth = view.findViewById(R.id.lbl_birth);
        lbl_address = view.findViewById(R.id.lbl_address);

        ImageButton imb_spec_add = view.findViewById(R.id.imb_spec_add);
        imb_spec_add.setOnClickListener(view1 -> {
            if (!isEditable) return;
            CustomEditText cet_last = arySpecViews.get(arySpecViews.size() - 1);
            if (cet_last.getInputText().isEmpty()) return;
            if (arySpecViews.size() > 4) return;
            StringBuilder spec1 = new StringBuilder(arySpecViews.get(0).getInputText());
            for (int i = 1; i < arySpecViews.size(); i++) {
                CustomEditText cet_spec = arySpecViews.get(i);
                if (cet_spec.getInputText().isEmpty()) {
                    continue;
                }
                spec1.append(",").append(cet_spec.getInputText());
            }
            reloadSpecLayout(spec1.toString() + ", ");
        });

        llt_spec = view.findViewById(R.id.llt_spec);
        imb_edit = view.findViewById(R.id.imb_edit);
        btn_post = view.findViewById(R.id.btn_post);

        cet_license = view.findViewById(R.id.cet_license);
        cet_birth = view.findViewById(R.id.cet_birth);
        cet_birth.setDisEditable();
        cet_location = view.findViewById(R.id.cet_location);
        cet_location.setDisEditable();
        cet_phone = view.findViewById(R.id.cet_phone);
        cet_credit = view.findViewById(R.id.cet_credit);
        cet_credit.setDisEditable();
        cet_city = view.findViewById(R.id.cet_city);
        cet_province = view.findViewById(R.id.cet_province);

        flw_tag_kind = view.findViewById(R.id.flw_tag_kind);

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

        initProfileImageData();

        lbl_name.setText("Dr. " + AppUtil.gCurrentUser.name);
        lbl_license.setText(AppUtil.gCurrentUser.spec2);
        lbl_email.setText(AppUtil.gCurrentUser.email);
        lbl_gender.setText(AppUtil.gCurrentUser.gender);
        HistoryModel.getRecentHistories(AppUtil.gCurrentUser, new HistoryModel.HistoryModelCallback() {
            @Override
            public void onSuccess(List<HistoryModel> historyModels, String average) {
                lbl_rate.setText(average);
                if (historyModels.size() == 0) {
                    lbl_reviews.setText(average);
                } else {
                    for (int i = 0; i < (int)(Float.parseFloat(average) + 0.5); i++) {
                        ImageView img_star = aryReviews.get(i);
                        img_star.setImageResource(R.drawable.ic_star_fill);
                    }
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
        lbl_birth.setText(AppUtil.gCurrentUser.birth);
        lbl_address.setText(AppUtil.gCurrentUser.address1 + ", " + AppUtil.gCurrentUser.address2);

        cet_license.setInputText(AppUtil.gCurrentUser.spec2);
        cet_phone.setInputText(AppUtil.gCurrentUser.phone);
        cet_birth.setInputText(AppUtil.gCurrentUser.birth);
        cet_location.setInputText(LocationUtil.getAddressFromString(mActivity, AppUtil.gCurrentUser.location));
        cet_city.setInputText(AppUtil.gCurrentUser.address1);
        cet_province.setInputText(AppUtil.gCurrentUser.address2);
        cet_credit.setInputText(SharedPreferenceUtil.getSeletedCardModel().getPublicCardNumber());

        flw_tag_kind.removeAllViews();
        if (AppUtil.gCurrentUser.spec1.isEmpty()) {
            flw_tag_kind.setVisibility(View.GONE);
            reloadSpecLayout("");
        } else {
            flw_tag_kind.setVisibility(View.VISIBLE);
            String[] specs = AppUtil.gCurrentUser.spec1.split(",");
            for (String spec: specs) {
                TagCell tagCell = new TagCell(getContext());
                tagCell.setTitle(spec);
                flw_tag_kind.addView(tagCell);
            }
            reloadSpecLayout(AppUtil.gCurrentUser.spec1);
        }

        latlng = AppUtil.gCurrentUser.location;
    }

    private void initProfileImageData() {
        llt_profile.removeAllViews();
        for (int i = 0; i < (AppUtil.gCurrentUser.images.size() + 2) / 3; i++) {
            List<String> lst_urls = new ArrayList<>();
            String url = AppUtil.gCurrentUser.images.get(i * 3);
            lst_urls.add(url);
            if (i * 3 + 1 < AppUtil.gCurrentUser.images.size()) {
                String second_url = AppUtil.gCurrentUser.images.get(i * 3 + 1);
                lst_urls.add(second_url);
            }
            if (i * 3 + 2 < AppUtil.gCurrentUser.images.size()) {
                String second_url = AppUtil.gCurrentUser.images.get(i * 3 + 2);
                lst_urls.add(second_url);
            }
            ProfileImageCell imageCell = new ProfileImageCell(mActivity, lst_urls, true);
            imageCell.setProfileImageCellInterface(url1 -> {
                AppUtil.gCurrentUser.images.remove(url1);
                AppUtil.gCurrentUser.addUserToFB(new UserModel.UserModelInterface() {
                    @Override
                    public void onSuccess(UserModel user) {
                        initProfileImageData();
                    }

                    @Override
                    public void onFailed(String error) {
                        Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                    }
                });
            });
            llt_profile.addView(imageCell);
        }
    }

    private void reloadSpecLayout(String spec1) {
        llt_spec.removeAllViews();
        arySpecViews.clear();
        if (spec1.isEmpty()) {
            CustomEditText customEditText = new CustomEditText(getContext());
            customEditText.setResourceLeftID(R.drawable.ic_medical);
            customEditText.setHintStr("Agregar especialidad");
            customEditText.setCustomEditTextCallback(new CustomEditText.CustomEditTextCallback() {
                @Override
                public void onClickRightIcon() {
                    arySpecViews.remove(customEditText);
                    if (arySpecViews.size() == 0) {
                        reloadSpecLayout("");
                    } else {
                        StringBuilder spec1 = new StringBuilder(arySpecViews.get(0).getInputText());
                        for (int i = 1; i < arySpecViews.size(); i++) {
                            CustomEditText ilt_spec = arySpecViews.get(i);
                            if (ilt_spec.getInputText().isEmpty()) {
                                continue;
                            }
                            spec1.append(",").append(ilt_spec.getInputText());
                        }
                        reloadSpecLayout(spec1.toString());
                    }
                }
            });
            arySpecViews.add(customEditText);
            llt_spec.addView(customEditText);
        } else {
            String[] specs = spec1.split(",");
            for (String spec: specs) {
                CustomEditText customEditText = new CustomEditText(getContext());
                customEditText.setResourceLeftID(R.drawable.ic_medical);
                customEditText.setHintStr("Agregar especialidad");
                customEditText.setInputText(spec);
                customEditText.setCustomEditTextCallback(new CustomEditText.CustomEditTextCallback() {
                    @Override
                    public void onClickRightIcon() {
                        arySpecViews.remove(customEditText);
                        if (arySpecViews.size() == 0) {
                            reloadSpecLayout("");
                        } else {
                            StringBuilder spec1 = new StringBuilder(arySpecViews.get(0).getInputText());
                            for (int i = 1; i < arySpecViews.size(); i++) {
                                CustomEditText ilt_spec = arySpecViews.get(i);
                                if (ilt_spec.getInputText().isEmpty()) {
                                    continue;
                                }
                                spec1.append(",").append(ilt_spec.getInputText());
                            }
                            reloadSpecLayout(spec1.toString());
                        }
                    }
                });
                if (spec.equals(" ")) {
                    customEditText.setInputText("");
                }
                arySpecViews.add(customEditText);
                llt_spec.addView(customEditText);
            }
        }
        setEditableView();
    }

    private void setEditableView() {
        cet_license.setEnabled(isEditable);
        cet_birth.setEnabled(isEditable);
        cet_location.setEnabled(isEditable);
        cet_phone.setEnabled(!isEditable);
        cet_credit.setEnabled(isEditable);
        cet_city.setEnabled(isEditable);
        cet_province.setEnabled(isEditable);
        for (CustomEditText customEditText: arySpecViews) {
            customEditText.setEnabled(isEditable);
            customEditText.setShowRight(isEditable);
        }
        if (isEditable) {
            imb_edit.setVisibility(View.GONE);
            btn_post.setVisibility(View.VISIBLE);

        } else {
            imb_edit.setVisibility(View.VISIBLE);
            btn_post.setVisibility(View.GONE);
        }
    }

}
