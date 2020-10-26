package com.laodev.masapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.ui.AcceptOrderCell;

import java.util.List;

public class AcceptOrderAdapter extends BaseAdapter {

    private Context context;
    private List<OrderModel> mOrders;

    private AcceptOrderAdapterCallback acceptOrderAdapterCallback;

    public AcceptOrderAdapter(Context context, List<OrderModel> orders) {
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
        AcceptOrderCell acceptOrderCell = new AcceptOrderCell(context, model);
        acceptOrderCell.setAcceptOrderCellCallback((userModel) -> acceptOrderAdapterCallback.onClickCallBtn(userModel));
        return acceptOrderCell;
    }

    public void setAcceptOrderAdapterCallback(AcceptOrderAdapterCallback acceptOrderAdapterCallback) {
        this.acceptOrderAdapterCallback = acceptOrderAdapterCallback;
    }

    public interface AcceptOrderAdapterCallback {
        void onClickCallBtn(UserModel userModel);
    }

}
