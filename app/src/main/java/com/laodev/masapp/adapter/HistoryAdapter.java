package com.laodev.masapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.laodev.masapp.model.HistoryModel;
import com.laodev.masapp.ui.HistoryCell;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {

    private Context context;
    private List<HistoryModel> mHistories;

    public HistoryAdapter(Context context, List<HistoryModel> historyModels) {
        this.context = context;
        mHistories = historyModels;
    }

    @Override
    public int getCount() {
        return mHistories.size();
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
        HistoryModel model = mHistories.get(position);
        String date = "";
        if (position > 0) {
            date = mHistories.get(position - 1).orderModel.endtime.split(" ")[0];
        }

        return new HistoryCell(context, model, date);
    }

}
