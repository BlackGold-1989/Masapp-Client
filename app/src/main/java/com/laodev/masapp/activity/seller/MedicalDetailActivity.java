package com.laodev.masapp.activity.seller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;
import com.laodev.masapp.model.TreatmentModel;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.TimerUtil;

import java.util.List;

public class MedicalDetailActivity extends BaseActivity {

    private ProgressDialog dialog;
    private View viw_1, viw_2;
    private LinearLayout llt_3, llt_edit;
    private TextView lbl_1, lbl_2, lbl_3;
    private ImageView img_1, img_2, img_3;
    private EditText txt_medical;
    private Button btn_add;

    private String str_dia, str_tra, str_sin;
    private int medical_type = 2;


    public void onClickTabLbl(View view) {
        switch (medical_type) {
            case 0:
                str_dia = txt_medical.getText().toString();
                break;
            case 1:
                str_tra = txt_medical.getText().toString();
                break;
            case 2:
                str_sin = txt_medical.getText().toString();
                break;
        }
        TextView lbl_tap = (TextView) view;
        switch (lbl_tap.getText().toString()) {
            case "Diagnostico":
                lbl_1.setText(getString(R.string.str_tab_3));
                lbl_2.setText(getString(R.string.str_tab_2));
                lbl_3.setText(getString(R.string.str_tab_1));

                viw_1.setBackgroundResource(R.drawable.back_tap_first_green);
                viw_2.setBackgroundResource(R.drawable.back_tap_second_blue);
                llt_3.setBackgroundResource(R.drawable.back_tap_first_yellow);

                img_1.setColorFilter(ContextCompat.getColor(this, R.color.colorGreen));
                img_2.setColorFilter(ContextCompat.getColor(this, R.color.colorBlue));
                img_3.setColorFilter(ContextCompat.getColor(this, R.color.colorYellow));

                medical_type = 0;
                txt_medical.setText(str_dia);
                break;
            case "Tratamiento":
                lbl_1.setText(getString(R.string.str_tab_1));
                lbl_2.setText(getString(R.string.str_tab_3));
                lbl_3.setText(getString(R.string.str_tab_2));

                viw_1.setBackgroundResource(R.drawable.back_tap_first_yellow);
                viw_2.setBackgroundResource(R.drawable.back_tap_second_green);
                llt_3.setBackgroundResource(R.drawable.back_tap_first_blue);

                img_1.setColorFilter(ContextCompat.getColor(this, R.color.colorYellow));
                img_2.setColorFilter(ContextCompat.getColor(this, R.color.colorGreen));
                img_3.setColorFilter(ContextCompat.getColor(this, R.color.colorBlue));

                txt_medical.setText(str_tra);
                medical_type = 1;
                break;
            case "Sintomas":
                lbl_1.setText(getString(R.string.str_tab_1));
                lbl_2.setText(getString(R.string.str_tab_2));
                lbl_3.setText(getString(R.string.str_tab_3));

                viw_1.setBackgroundResource(R.drawable.back_tap_first_yellow);
                viw_2.setBackgroundResource(R.drawable.back_tap_second_blue);
                llt_3.setBackgroundResource(R.drawable.back_tap_first_green);

                img_1.setColorFilter(ContextCompat.getColor(this, R.color.colorYellow));
                img_2.setColorFilter(ContextCompat.getColor(this, R.color.colorBlue));
                img_3.setColorFilter(ContextCompat.getColor(this, R.color.colorGreen));

                txt_medical.setText(str_sin);
                medical_type = 2;
                break;
        }
    }

    private void initWithEvent() {
        btn_add.setOnClickListener(v -> {
            switch (medical_type) {
                case 0:
                    str_dia = txt_medical.getText().toString();
                    break;
                case 1:
                    str_tra = txt_medical.getText().toString();
                    break;
                case 2:
                    str_sin = txt_medical.getText().toString();
                    break;
            }
            if (str_dia.isEmpty()) {
                Toast.makeText(this, "El diagnóstico está vacío.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (str_tra.isEmpty()) {
                Toast.makeText(this, "El Tratamiento está vacío.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (str_sin.isEmpty()) {
                Toast.makeText(this, "El Sintomas está vacío.", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.show();
            if (AppUtil.isAddMedical) {
                TreatmentModel model = new TreatmentModel();
                model.userid = AppUtil.gSelUser.id;
                model.doctorid = AppUtil.gSelUser.id;
                model.datetime = TimerUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
                model.diagnostico = str_dia;
                model.treatment = str_tra;
                model.symptoms = str_sin;
                model.addMedicalHistoryToFB(new TreatmentModel.TreatmentModelCallback() {
                    @Override
                    public void onSuccess(List<TreatmentModel> models) {
                        dialog.dismiss();
                        Toast.makeText(MedicalDetailActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }

                    @Override
                    public void onFailed(String error) {
                        dialog.dismiss();
                        Toast.makeText(MedicalDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                AppUtil.gTreatModel.diagnostico = str_dia;
                AppUtil.gTreatModel.treatment = str_tra;
                AppUtil.gTreatModel.symptoms = str_sin;
                AppUtil.gTreatModel.updateMedicalHistoryToFB(new TreatmentModel.TreatmentModelCallback() {
                    @Override
                    public void onSuccess(List<TreatmentModel> models) {
                        dialog.dismiss();
                        Toast.makeText(MedicalDetailActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }

                    @Override
                    public void onFailed(String error) {
                        dialog.dismiss();
                        Toast.makeText(MedicalDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_detail);

        getWindow().setStatusBarColor(getColor(R.color.colorBlue));

        initUIView();
        initWithEvent();
    }

    private void initUIView() {
        dialog = ProgressDialog.show(this, "", getString(R.string.pgr_connect_server));
        dialog.dismiss();

        viw_1 = findViewById(R.id.viw_medical_tap1);
        viw_2 = findViewById(R.id.viw_medical_tap2);
        llt_3 = findViewById(R.id.llt_medical_tap3);

        lbl_1 = findViewById(R.id.lbl_medical_tab1);
        lbl_2 = findViewById(R.id.lbl_medical_tab2);
        lbl_3 = findViewById(R.id.lbl_medical_tab3);

        img_1 = findViewById(R.id.img_medical_tap1);
        img_2 = findViewById(R.id.img_medical_tap2);
        img_3 = findViewById(R.id.img_medical_tap3);

        llt_edit = findViewById(R.id.llt_medical_edit);
        txt_medical = findViewById(R.id.txt_medical);

        btn_add = findViewById(R.id.btn_medical_add);

        initWithData();
    }

    private void initWithData() {
        if (AppUtil.isAddMedical) {
            llt_edit.setVisibility(View.GONE);
            txt_medical.setEnabled(true);
            btn_add.setVisibility(View.VISIBLE);

            str_dia = "";
            str_tra = "";
            str_sin = "";
        } else {
            llt_edit.setVisibility(View.VISIBLE);
            txt_medical.setEnabled(false);
            btn_add.setVisibility(View.GONE);

            str_dia = AppUtil.gTreatModel.diagnostico;
            str_tra = AppUtil.gTreatModel.treatment;
            str_sin = AppUtil.gTreatModel.symptoms;
        }
        txt_medical.setText(str_sin);
    }

    public void onClickBackImg(View view) {
        onBackPressed();
    }

    public void onClickMedicalEditLlt(View view) {
        txt_medical.setEnabled(true);
        llt_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.VISIBLE);
        btn_add.setText(getString(R.string.normal_medical_edit));
    }

}
