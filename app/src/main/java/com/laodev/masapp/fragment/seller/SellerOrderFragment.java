package com.laodev.masapp.fragment.seller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.laodev.masapp.R;
import com.laodev.masapp.activity.MainActivity;
import com.laodev.masapp.adapter.AcceptOrderAdapter;
import com.laodev.masapp.callback.OnCallingListener;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class SellerOrderFragment extends Fragment {

    private MainActivity mActivity;

    private LinearLayout llt_inactvie;
    private TextView lbl_title, lbl_sub_title;

    private AcceptOrderAdapter mOrderAdapter;
    private List<OrderModel> orderModelList = new ArrayList<>();

    private OnCallingListener callingListener;


    public SellerOrderFragment(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_order, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        llt_inactvie = view.findViewById(R.id.llt_inactvie);
        lbl_title = view.findViewById(R.id.lbl_title);
        lbl_sub_title = view.findViewById(R.id.lbl_sub_title);

        ListView lst_order = view.findViewById(R.id.lst_order);
        mOrderAdapter = new AcceptOrderAdapter(mActivity, orderModelList);
        mOrderAdapter.setAcceptOrderAdapterCallback(userModel -> callingListener.onVoiceCallingEvent(userModel));
        lst_order.setAdapter(mOrderAdapter);

        initWithData();
    }

    private void initWithData() {
        ProgressDialog dialog = ProgressDialog.show(mActivity, "", getString(R.string.pgr_connect_server));
        dialog.show();

        OrderModel.getAllOrdersByID(AppUtil.gCurrentUser.id, new OrderModel.OrderModelCallback() {
            @Override
            public void onSuccess(List<OrderModel> orderModels) {
                dialog.dismiss();

                if (orderModels.size() > 0) {
                    llt_inactvie.setVisibility(View.GONE);
                    lbl_title.setText(Html.fromHtml(getString(R.string.order_activity_title)));
                    lbl_sub_title.setText(getString(R.string.order_seller_active_sub_title));
                }

                orderModelList.clear();
                orderModelList.addAll(orderModels);
                mOrderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(String error) {
                dialog.dismiss();
                Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setOnCallingListener(OnCallingListener callingListener) {
        this.callingListener = callingListener;
    }

}
