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
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends BaseActivity {

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
        setContentView(R.layout.activity_register);

        initView();
    }

    private void initView() {
        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(view -> onBackPressed());

        civ_avatar = findViewById(R.id.civ_avatar);
        ImageView img_register_add = findViewById(R.id.img_register_add);
        img_register_add.setOnClickListener(view -> CropImage.activity()
                .setMaxCropResultSize(2048, 2048)
                .setAutoZoomEnabled(false)
                .start(RegisterActivity.this));
        cet_name = findViewById(R.id.cet_name);
        cet_birth = findViewById(R.id.cet_birth);
        cet_birth.setDisEditable();
        cet_birth.setCustomEditTextCallback(new CustomEditText.CustomEditTextCallback() {
            @Override
            public void onClickEditText() {
                if (cet_birth.getInputText().length() > 0) {
                    mCalendar.setTime(TimerUtil.getDateByString("dd-MM-yyyy", cet_birth.getInputText()));
                }
                DatePickerDialog pickerDialog = new DatePickerDialog(RegisterActivity.this, R.style.DialogTheme, mDateListener, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                pickerDialog.show();
            }
        });
        cet_city = findViewById(R.id.cet_city);
        cet_province = findViewById(R.id.cet_province);
        CustomEditText cet_phone = findViewById(R.id.cet_phone);
        cet_phone.setEnabled(false);
        cet_phone.setInputText(AppUtil.gCurrentUser.phone);
        cet_email = findViewById(R.id.cet_email);
        cet_latlng = findViewById(R.id.cet_latlng);
        cet_latlng.setDisEditable();
        cet_latlng.setCustomEditTextCallback(new CustomEditText.CustomEditTextCallback() {
            @Override
            public void onClickEditText() {
                startActivityForResult(new Intent(RegisterActivity.this, PlacesPickerActivity.class), PICK_LOCATION_REQUEST);
            }
        });
        rdg_gender = findViewById(R.id.rdg_gender);
    }

    public void onClickRegisterBtn(View view) {
        if (bmpAvatar == null) {
            Toast.makeText(this, R.string.toast_no_avatar, Toast.LENGTH_SHORT).show();
            return;
        }
        String name = cet_name.getInputText();
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.toast_no_name, Toast.LENGTH_SHORT).show();
            return;
        }
        String birth = cet_birth.getInputText();
        if (birth.isEmpty()) {
            Toast.makeText(this, R.string.toast_no_birth, Toast.LENGTH_SHORT).show();
            return;
        }
        if (latlng.isEmpty()) {
            Toast.makeText(this, R.string.toast_no_location, Toast.LENGTH_SHORT).show();
            return;
        }
        String city = cet_city.getInputText();
        if (city.isEmpty()) {
            Toast.makeText(this, R.string.toast_no_city, Toast.LENGTH_SHORT).show();
            return;
        }
        String provice = cet_province.getInputText();
        if (provice.isEmpty()) {
            Toast.makeText(this, R.string.toast_no_province, Toast.LENGTH_SHORT).show();
            return;
        }
        String email = cet_email.getInputText();
        if (email.isEmpty()) {
            Toast.makeText(this, R.string.toast_no_email, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.contains("@")) {
            Toast.makeText(this, R.string.toast_email_formate, Toast.LENGTH_SHORT).show();
            return;
        }
        AppUtil.gCurrentUser.name = name;
        AppUtil.gCurrentUser.birth = birth;
        AppUtil.gCurrentUser.address1 = city;
        AppUtil.gCurrentUser.address2 = provice;
        AppUtil.gCurrentUser.email = email;
        AppUtil.gCurrentUser.location = latlng;
        AppUtil.gCurrentUser.type = Constants.USER_SELLER;
        AppUtil.gCurrentUser.status = Constants.STATUS_READY;
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
                AppUtil.gCurrentUser.uploadAvatar(bmpAvatar, new UserModel.UserModelInterface() {
                    @Override
                    public void onSuccess(UserModel user) {
                        dialog.dismiss();
                        AppUtil.gCurrentUser = user;
                        AppUtil.showOtherActivity(RegisterActivity.this, ReviewActivity.class, 0);
                        finish();
                    }

                    @Override
                    public void onFailed(String error) {
                        dialog.dismiss();
                        Toast.makeText(RegisterActivity.this, R.string.toast_no_email, Toast.LENGTH_SHORT).show();
                        existApp();
                    }
                });
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(RegisterActivity.this, R.string.toast_no_email, Toast.LENGTH_SHORT).show();
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
