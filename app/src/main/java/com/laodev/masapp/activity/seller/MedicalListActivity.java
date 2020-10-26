package com.laodev.masapp.activity.seller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;
import com.laodev.masapp.adapter.MedicalHistoryAdapter;
import com.laodev.masapp.model.TreatmentModel;
import com.laodev.masapp.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class MedicalListActivity extends BaseActivity {

    private TextView lbl_empty;
    private ImageView img_medical;

    private List<TreatmentModel> treatmentModels = new ArrayList<>();

    private MedicalHistoryAdapter historyAdapter;


    private void initEvents() {
        img_medical.setOnClickListener(v -> {
            AppUtil.isAddMedical = true;
            AppUtil.showOtherActivity(this, MedicalDetailActivity.class, 0);
        });
    }

    public void onClickBackImg(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_list);

        initUIView();
        initEvents();
    }

    private void initUIView() {
        ListView lst_order = findViewById(R.id.lst_search_order);
        historyAdapter = new MedicalHistoryAdapter(this, treatmentModels);
        lst_order.setAdapter(historyAdapter);

        lbl_empty = findViewById(R.id.lbl_treatment_empty);
        img_medical = findViewById(R.id.img_medical_add);
    }

    private void initWithData() {
        ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.pgr_connect_server));
        TreatmentModel.getMedicalsFromFB(AppUtil.gSelUser.id, new TreatmentModel.TreatmentModelCallback() {
            @Override
            public void onSuccess(List<TreatmentModel> models) {
                dialog.dismiss();
                if (models.size() > 0) {
                    lbl_empty.setVisibility(View.GONE);
                }
                treatmentModels.clear();
                treatmentModels.addAll(models);
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(MedicalListActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initWithData();
    }

}
