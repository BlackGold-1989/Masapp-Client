package com.laodev.masapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.laodev.masapp.R;
import com.laodev.masapp.activity.MainActivity;
import com.laodev.masapp.adapter.HistoryAdapter;
import com.laodev.masapp.model.HistoryModel;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private MainActivity mActivity;

    private TextView lbl_title, lbl_empty;
    private List<HistoryModel> historyModels = new ArrayList<>();
    private HistoryAdapter historyAdapter;

    public HistoryFragment() {

    }

    public HistoryFragment(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        lbl_title = view.findViewById(R.id.lbl_title);
        lbl_empty = view.findViewById(R.id.lbl_empty);
        ListView lst_history = view.findViewById(R.id.lst_history);
        historyAdapter = new HistoryAdapter(mActivity, historyModels);
        lst_history.setAdapter(historyAdapter);

        initData();
    }

    private void initData() {
        if (AppUtil.gCurrentUser.type.equals(Constants.USER_SELLER)) {
            lbl_title.setText("Pacientes Revisados");
        } else {
            lbl_title.setText("Historial de Médicos");
        }
        HistoryModel.getHistoriesByUser(AppUtil.gCurrentUser, new HistoryModel.HistoryModelCallback() {
            @Override
            public void onSuccess(List<HistoryModel> histories) {
                if (histories.size() > 0) {
                    lbl_empty.setVisibility(View.GONE);
                    historyModels.addAll(histories);
                }
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
