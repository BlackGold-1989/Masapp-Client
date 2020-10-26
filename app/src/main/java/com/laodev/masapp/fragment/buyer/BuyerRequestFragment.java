package com.laodev.masapp.fragment.buyer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.laodev.masapp.R;
import com.laodev.masapp.activity.MainActivity;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.model.RequestModel;
import com.laodev.masapp.ui.RequestCell;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.FireConstants;

import java.util.ArrayList;
import java.util.List;

public class BuyerRequestFragment extends Fragment {

    private MainActivity mActivity;

    private LinearLayout llt_request;
    private OrderModel selOrderModel = new OrderModel();


    public BuyerRequestFragment(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_request, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        llt_request = view.findViewById(R.id.llt_request);

        initData();
    }

    private void initData() {
        FireConstants.orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
                    if (orderModel != null && orderModel.userid.equals(AppUtil.gCurrentUser.id)) {
                        selOrderModel = orderModel;
                        break;
                    }
                }
                RequestModel.getAllRequests(selOrderModel.id, new RequestModel.RequestModelCallback() {
                    @Override
                    public void onSuccess(List<RequestModel> models) {
                        onRefreshRequestView(models);
                    }

                    @Override
                    public void onFailed(String error) {
                        Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onRefreshRequestView(List<RequestModel> models) {
        llt_request.removeAllViews();
        for (RequestModel requestModel: models) {
            RequestCell requestCell = new RequestCell(mActivity, requestModel);
            llt_request.addView(requestCell);
        }
    }

}
