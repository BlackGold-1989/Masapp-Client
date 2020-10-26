package com.laodev.masapp.fragment.seller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.laodev.masapp.R;
import com.laodev.masapp.activity.MainActivity;
import com.laodev.masapp.adapter.OrderAdapter;
import com.laodev.masapp.model.DirectionModel;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.FireConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SellerHomeFragment extends Fragment {

    private MainActivity mActivity;

    private List<OrderModel> orderModelList = new ArrayList<>();
    private OrderAdapter mOrderAdapter;


    public SellerHomeFragment() {
        // doesn't do anything special
    }

    public SellerHomeFragment(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ListView lst_order = view.findViewById(R.id.lst_order);
        mOrderAdapter = new OrderAdapter(mActivity, orderModelList);
        lst_order.setAdapter(mOrderAdapter);
        initData();
    }

    private void initData() {
        FireConstants.orderRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                if (dataSnapshot.getValue() != null ) {
                    OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
                    if (orderModel == null) {
                        return;
                    }
                    if (orderModelList != null && orderModelList.size() > 0) {
                        boolean isContain = false;
                        for (int i=0; i< orderModelList.size(); i++ ) {
                            if(orderModelList.get(i).id.equals(orderModel.id)) {
                                isContain = true;
                                break;
                            }
                        }
                        if( !isContain ) {
                            DirectionModel.getCheckModelByUserID(orderModel.userid, new DirectionModel.DirectionModelListener() {
                                @Override
                                public void onSuccess(DirectionModel model) {
                                    if (model.address1.equals(AppUtil.gCurrentUser.address1)) {
                                        orderModelList.add(orderModel);
                                        sortOrderByDate();
                                    }
                                }

                                @Override
                                public void onFailed(String error) {
                                    Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        DirectionModel.getCheckModelByUserID(orderModel.userid, new DirectionModel.DirectionModelListener() {
                            @Override
                            public void onSuccess(DirectionModel model) {
                                if (model.address1.equals(AppUtil.gCurrentUser.address1)) {
                                    orderModelList.add(orderModel);
                                    sortOrderByDate();
                                }
                            }

                            @Override
                            public void onFailed(String error) {
                                Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
                if (orderModel == null) {
                    return;
                }
                if (orderModelList != null && orderModelList.size() > 0) {
                    for (int i=0; i< orderModelList.size(); i++ ) {
                        if (orderModelList.get(i).id.equals(orderModel.id)) {
                            orderModelList.set(i, orderModel);
                            break;
                        }
                    }
                    sortOrderByDate();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
                if (orderModel == null) {
                    return;
                }
                if (orderModelList != null && orderModelList.size() > 0) {
                    int selIndex = 0;
                    for( int i=0; i< orderModelList.size(); i++ ) {
                        if (orderModelList.get(i).id.equals(orderModel.id)) {
                            selIndex = i;
                            break;
                        }
                    }
                    orderModelList.remove(selIndex);
                }
                sortOrderByDate();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sortOrderByDate() {
        Collections.sort(orderModelList, (obj1, obj2) -> obj2.datetime.compareToIgnoreCase(obj1.datetime));
        mOrderAdapter.notifyDataSetChanged();
    }

}
