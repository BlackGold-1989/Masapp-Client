package com.laodev.masapp.activity.seller;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;
import com.laodev.masapp.activity.placespicker.Place;
import com.laodev.masapp.activity.placespicker.PlacesPickerActivity;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.ui.CustomEditText;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;
import com.laodev.masapp.util.LocationUtil;
import com.laodev.masapp.util.TimerUtil;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewActivity extends BaseActivity {

    private static final int PICK_LOCATION_REQUEST = 7125;

    private String latlng = "";
    private Bitmap bmpAvatar = null;
    private CircleImageView civ_avatar;
    private CustomEditText cet_name, cet_birth, cet_city, cet_province, cet_email, cet_latlng;
    private RadioGroup rdg_gender;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        initView();
    }

    private void initView() {
        civ_avatar = findViewById(R.id.civ_avatar);
        Picasso.get().load(AppUtil.gCurrentUser.imgurl).fit().centerCrop()
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .into(civ_avatar, null);

        ImageView img_register_add = findViewById(R.id.img_register_add);
        img_register_add.setOnClickListener(view -> CropImage.activity()
                .setMaxCropResultSize(2048, 2048)
                .setAutoZoomEnabled(false)
                .start(ReviewActivity.this));
        cet_name = findViewById(R.id.cet_name);
        cet_name.setInputText(AppUtil.gCurrentUser.name);
        cet_birth = findViewById(R.id.cet_birth);
        cet_birth.setInputText(AppUtil.gCurrentUser.birth);
        cet_birth.setDisEditable();
        cet_birth.setCustomEditTextCallback(new CustomEditText.CustomEditTextCallback() {
            @Override
            public void onClickEditText() {
                if (cet_birth.getInputText().length() > 0) {
                    mCalendar.setTime(TimerUtil.getDateByString("dd-MM-yyyy", cet_birth.getInputText()));
                }
                DatePickerDialog pickerDialog = new DatePickerDialog(ReviewActivity.this, R.style.DialogTheme, mDateListener, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                pickerDialog.show();
            }
        });
        cet_city = findViewById(R.id.cet_city);
        cet_city.setInputText(AppUtil.gCurrentUser.address1);
        cet_province = findViewById(R.id.cet_province);
        cet_province.setInputText(AppUtil.gCurrentUser.address2);
        CustomEditText cet_phone = findViewById(R.id.cet_phone);
        cet_phone.setEnabled(false);
        cet_phone.setInputText(AppUtil.gCurrentUser.phone);
        cet_email = findViewById(R.id.cet_email);
        cet_email.setInputText(AppUtil.gCurrentUser.email);
        cet_latlng = findViewById(R.id.cet_latlng);
        latlng = AppUtil.gCurrentUser.location;
        cet_latlng.setInputText(LocationUtil.getAddressFromString(this, AppUtil.gCurrentUser.location));
        cet_latlng.setDisEditable();
        cet_latlng.setCustomEditTextCallback(new CustomEditText.CustomEditTextCallback() {
            @Override
            public void onClickEditText() {
                startActivityForResult(new Intent(ReviewActivity.this, PlacesPickerActivity.class), PICK_LOCATION_REQUEST);
            }
        });
        rdg_gender = findViewById(R.id.rdg_gender);
        RadioButton rdb_male = findViewById(R.id.rdb_male);
        RadioButton rdb_female = findViewById(R.id.rdb_female);
        if (AppUtil.gCurrentUser.gender.equals(Constants.GENDER_MALE)) {
            rdb_male.setChecked(true);
        } else {
            rdb_female.setChecked(true);
        }
    }

    public void onClickUpdateBtn(View view) {
        String name = cet_name.getInputText();
        if (!name.isEmpty()) {
            AppUtil.gCurrentUser.name = name;
        }
        String birth = cet_birth.getInputText();
        if (!birth.isEmpty()) {
            AppUtil.gCurrentUser.birth = birth;
        }
        if (!latlng.isEmpty()) {
            AppUtil.gCurrentUser.location = latlng;
        }
        String city = cet_city.getInputText();
        if (!city.isEmpty()) {
            AppUtil.gCurrentUser.address1 = city;
        }
        String provice = cet_province.getInputText();
        if (!provice.isEmpty()) {
            AppUtil.gCurrentUser.address2 = provice;
        }
        String email = cet_email.getInputText();
        if (!email.isEmpty() && email.contains("@")) {
            AppUtil.gCurrentUser.email = email;
        }

        int selectedId = rdg_gender.getCheckedRadioButtonId();
        if (selectedId == R.id.rdb_male) {
            AppUtil.gCurrentUser.gender = Constants.GENDER_MALE;
        } else {
            AppUtil.gCurrentUser.gender = Constants.GENDER_FEMALE;
        }

        ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.pgr_connect_server));
        AppUtil.gCurrentUser.addUserToFB(new UserModel.UserModelInterface() {
            @Override
            public void onSuccess(UserModel user) {
                if (bmpAvatar != null) {
                    AppUtil.gCurrentUser.uploadAvatar(bmpAvatar, new UserModel.UserModelInterface() {
                        @Override
                        public void onSuccess(UserModel user) {
                            dialog.dismiss();
                            AppUtil.gCurrentUser = user;
                            Toast.makeText(ReviewActivity.this, R.string.toast_success_update, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed(String error) {
                            dialog.dismiss();
                            Toast.makeText(ReviewActivity.this, R.string.toast_no_email, Toast.LENGTH_SHORT).show();
                            existApp();
                        }
                    });
                } else {
                    dialog.dismiss();
                    AppUtil.gCurrentUser = user;
                    Toast.makeText(ReviewActivity.this, R.string.toast_success_update, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(ReviewActivity.this, R.string.toast_no_email, Toast.LENGTH_SHORT).show();
                existApp();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bmpAvatar = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    civ_avatar.setImageBitmap(bmpAvatar);
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (requestCode == PICK_LOCATION_REQUEST) {
            Place place;
            if (data != null) {
                place = data.getParcelableExtra(Place.EXTRA_PLACE);
                LatLng ll;
                if (place != null) {
                    ll = place.getLatLng();
                    latlng = String.format(Locale.getDefault(), "%f,%f", ll.latitude, ll.longitude);
                    cet_latlng.setInputText(LocationUtil.getAddressFromLatlng(this, ll.latitude, ll.longitude));
                }
            }
        }
    }

}
