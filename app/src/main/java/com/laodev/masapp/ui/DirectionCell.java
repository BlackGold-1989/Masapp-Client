package com.laodev.masapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.laodev.masapp.R;
import com.laodev.masapp.model.DirectionModel;
import com.laodev.masapp.util.LocationUtil;

public class DirectionCell extends LinearLayout {

    private DirectionModel mModel;
    private DirectionCellCallback directionCellCallback;

    private TextView lbl_title, lbl_desc;
    private ImageView img_check;


    private void initWithEvent() {
        this.setOnClickListener(v -> directionCellCallback.onClickLocationSet(mModel));
        this.setOnLongClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setTitle(R.string.app_name);
            dialog.setMessage(getContext().getString(R.string.alt_direction));
            dialog.setPositiveButton(getResources().getString(android.R.string.yes), (dialogInterface, i) -> {
                directionCellCallback.onClickLocationDelete(mModel);
            });

            dialog.setNeutralButton(getResources().getString(android.R.string.no), (dialogInterface, i) -> { });
            dialog.show();
            return false;
        });
    }

    public DirectionCell(Context context, DirectionModel model) {
        super(context);

        mModel = model;

        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.ui_direction_cell, this, true);

        initUIView();
        initWithEvent();
    }

    private void initUIView() {
        lbl_title = findViewById(R.id.lbl_location_title);
        lbl_desc = findViewById(R.id.lbl_location_desc);

        img_check = findViewById(R.id.img_location_check);

        initWithData();
    }

    private void initWithData() {
        lbl_title.setText(mModel.address1 + " " + mModel.address2);
        double lat = Double.valueOf(mModel.location.split(",")[0]);
        double lng = Double.valueOf(mModel.location.split(",")[1]);
        lbl_desc.setText(LocationUtil.getAddressFromLatlng(getContext(), lat, lng));
        if (mModel.isCheck.equals("true")) {
            img_check.setVisibility(VISIBLE);
        } else {
            img_check.setVisibility(INVISIBLE);
        }
    }

    public void setDirectionCellCallback(DirectionCellCallback directionCellCallback) {
        this.directionCellCallback = directionCellCallback;
    }

    public interface DirectionCellCallback {
        void onClickLocationSet(DirectionModel model);
        void onClickLocationDelete(DirectionModel model);
    }

}
