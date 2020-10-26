package com.laodev.masapp.fragment.buyer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.laodev.masapp.R;
import com.laodev.masapp.activity.MainActivity;
import com.laodev.masapp.activity.buyer.AddLocationActivity;
import com.laodev.masapp.adapter.DirectionAdapter;
import com.laodev.masapp.model.DirectionModel;
import com.laodev.masapp.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

import static com.laodev.masapp.util.Constants.ADD_DIRECTION_REQUEST;

public class BuyerLocationFragment extends Fragment {

    private MainActivity mActivity;

    private LinearLayout llt_no_location;

    private DirectionAdapter directionAdapter;
    private List<DirectionModel> directionModelList = new ArrayList<>();


    public BuyerLocationFragment(MainActivity activity) {
        mActivity = activity;
        mActivity.setDirectionCallback(model -> {
            for (DirectionModel directionModel: directionModelList) {
                directionModel.isCheck = "false";
            }
            directionModelList.add(model);
            DirectionModel.updateLocationsToFB(directionModelList, new DirectionModel.DirectionModelListener() {
                @Override
                public void onSuccess(List<DirectionModel> models) {
                    directionAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailed(String error) {
                    Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_location, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        llt_no_location = view.findViewById(R.id.llt_no_location);
        ImageButton imb_add = view.findViewById(R.id.imb_add);
        imb_add.setOnClickListener(view1 -> {
            AppUtil.REQUESTCODE = ADD_DIRECTION_REQUEST;
            startActivityForResult(new Intent(mActivity, AddLocationActivity.class), ADD_DIRECTION_REQUEST);
        });

        ListView lst_location = view.findViewById(R.id.lst_location);
        directionAdapter = new DirectionAdapter(mActivity, directionModelList);
        lst_location.setAdapter(directionAdapter);

        initData();
    }

    private void initData() {
        ProgressDialog dialog = ProgressDialog.show(mActivity, "", getString(R.string.pgr_connect_server));
        DirectionModel.getAllLocationsFromFB(AppUtil.gCurrentUser, new DirectionModel.DirectionModelListener() {
            @Override
            public void onSuccess(List<DirectionModel> directionModels) {
                dialog.dismiss();
                directionModelList.addAll(directionModels);
                if (directionModelList.size() > 0) {
                    directionAdapter.notifyDataSetChanged();
                    llt_no_location.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
