package com.laodev.masapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.laodev.masapp.model.TreatmentModel;
import com.laodev.masapp.ui.MedicalHistoryCell;

import java.util.List;

public class MedicalHistoryAdapter extends BaseAdapter {

    private Context context;
    private List<TreatmentModel> models;

    public MedicalHistoryAdapter(Context context, List<TreatmentModel> models) {
        this.context = context;
        this.models = models;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View containView, ViewGroup parent) {
        TreatmentModel model = models.get(position);
        return new MedicalHistoryCell(context, model);
    }

}
