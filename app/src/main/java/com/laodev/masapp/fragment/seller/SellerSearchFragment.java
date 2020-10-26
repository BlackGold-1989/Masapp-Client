package com.laodev.masapp.fragment.seller;

import android.app.ProgressDialog;
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
import com.laodev.masapp.adapter.BuyerCellAdapter;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class SellerSearchFragment extends Fragment {

    private MainActivity mActivity;
    private List<OrderModel> orderModels = new ArrayList<>();
    private BuyerCellAdapter buyerCellAdapter;

    private TextView lbl_empty;

    public SellerSearchFragment(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_search, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ListView lst_search_order = view.findViewById(R.id.lst_search_order);
        buyerCellAdapter = new BuyerCellAdapter(mActivity, orderModels);
        lst_search_order.setAdapter(buyerCellAdapter);

        lbl_empty = view.findViewById(R.id.lbl_empty);

        initData();
    }

    private void initData() {
        ProgressDialog dialog = ProgressDialog.show(mActivity, "", getString(R.string.pgr_connect_server));
        OrderModel.getAllOrdersByID(AppUtil.gCurrentUser.id, new OrderModel.OrderModelCallback() {
            @Override
            public void onSuccess(List<OrderModel> orders) {
                dialog.dismiss();
                if (orders.size() > 0) {
                    lbl_empty.setVisibility(View.GONE);
                }
                orderModels.addAll(orders);
                buyerCellAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
