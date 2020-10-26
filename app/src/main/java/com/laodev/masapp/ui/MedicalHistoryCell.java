package com.laodev.masapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laodev.masapp.R;
import com.laodev.masapp.activity.seller.MedicalDetailActivity;
import com.laodev.masapp.model.TreatmentModel;
import com.laodev.masapp.util.AppUtil;


public class MedicalHistoryCell extends LinearLayout {

    private TreatmentModel model;

    private void initWithEvent() {
        this.setOnClickListener(v -> {
            AppUtil.isAddMedical = false;
            AppUtil.gTreatModel = model;
            AppUtil.showOtherActivity(getContext(), MedicalDetailActivity.class, 0);
        });
    }

    public MedicalHistoryCell(Context context, TreatmentModel model) {
        super(context);

        this.model = model;

        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.ui_medical_history_cell, this, true);

        initUIView();
        initWithEvent();
    }

    private void initUIView() {
        TextView lbl_date = findViewById(R.id.lbl_medical_date);
        lbl_date.setText(model.datetime);
    }

}
