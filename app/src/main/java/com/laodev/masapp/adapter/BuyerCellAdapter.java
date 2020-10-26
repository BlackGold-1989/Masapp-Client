package com.laodev.masapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.laodev.masapp.activity.seller.MedicalListActivity;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.ui.BuyerCell;
import com.laodev.masapp.util.AppUtil;

import java.util.List;

public class BuyerCellAdapter extends BaseAdapter {

    private Context context;
    private List<OrderModel> mOrders;

    public BuyerCellAdapter(Context context, List<OrderModel> orders) {
        this.context = context;
        mOrders = orders;
    }

    @Override
    public int getCount() {
        return mOrders.size();
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
        OrderModel orderModel = mOrders.get(position);
        BuyerCell cell = new BuyerCell(context, orderModel);
        cell.setBuyerCellCallback(user -> {
            AppUtil.gSelUser = user;
            AppUtil.showOtherActivity(context, MedicalListActivity.class, 0);
        });
        return cell;
    }

}
