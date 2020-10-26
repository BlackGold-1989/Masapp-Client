package com.laodev.masapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.ui.OrderCell;

import java.util.List;

public class OrderAdapter extends BaseAdapter {

    private Context context;
    private List<OrderModel> mOrders;

    public OrderAdapter(Context context, List<OrderModel> orders) {
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
        OrderModel model = mOrders.get(position);
        containView = new OrderCell(context, model);
        return containView;
    }

}
