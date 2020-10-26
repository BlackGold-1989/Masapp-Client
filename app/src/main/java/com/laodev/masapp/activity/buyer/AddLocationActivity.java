package com.laodev.masapp.activity.buyer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;
import com.laodev.masapp.activity.placespicker.Place;
import com.laodev.masapp.activity.placespicker.PlacesPickerActivity;
import com.laodev.masapp.model.DirectionModel;
import com.laodev.masapp.ui.CustomEditText;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.LocationUtil;
import com.sinch.gson.Gson;

import static com.laodev.masapp.util.Constants.PICK_LOCATION_REQUEST;

public class AddLocationActivity extends BaseActivity {

    private ProgressDialog dialog;
    private EditText txt_city;
    private EditText txt_department;
    private CustomEditText cet_latlng;

    private String latlng;


    public void onClickAddLocation(View view) {
        if (latlng.isEmpty()) {
            Toast.makeText(this, "Establece la latitud y la longitud.", Toast.LENGTH_SHORT).show();
            return;
        }

        String city = txt_city.getText().toString();
        if (city.isEmpty()) {
            Toast.makeText(this, R.string.toast_no_city, Toast.LENGTH_SHORT).show();
            return;
        }

        String department = txt_department.getText().toString();
        if (department.isEmpty()) {
            Toast.makeText(this, R.string.toast_no_department, Toast.LENGTH_SHORT).show();
            return;
        }

        DirectionModel directionModel = new DirectionModel();
        directionModel.title = "";
        directionModel.location = latlng;
        directionModel.userid = AppUtil.gCurrentUser.id;
        directionModel.address1 = city;
        directionModel.address2 = department;
        directionModel.isCheck = "true";

        onFinishAddLocation(directionModel);
    }

    public void onClickBackImg(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        initUIView();
    }

    private void initUIView() {
        txt_city = findViewById(R.id.txt_city);
        txt_department = findViewById(R.id.txt_department);

        cet_latlng = findViewById(R.id.cet_latlng);
        cet_latlng.setDisEditable();
        cet_latlng.setCustomEditTextCallback(new CustomEditText.CustomEditTextCallback() {
            @Override
            public void onClickEditText() {
                startActivityForResult(new Intent(AddLocationActivity.this, PlacesPickerActivity.class), PICK_LOCATION_REQUEST);
            }
        });
    }

    private void onFinishAddLocation(DirectionModel model) {
        Intent data = new Intent();
        String jsonModel = new Gson().toJson(model);
        data.putExtra("DIRECTIONINFO", jsonModel);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_LOCATION_REQUEST) {
            Place place;
            if (data != null) {
                place = data.getParcelableExtra(Place.EXTRA_PLACE);
                LatLng ll;
                if (place != null) {
                    ll = place.getLatLng();
                    latlng = String.format("%s,%s", ll.latitude, ll.longitude);
                    cet_latlng.setInputText(LocationUtil.getAddressFromLatlng(this, ll.latitude, ll.longitude));
                }
            }
        }
    }

}
