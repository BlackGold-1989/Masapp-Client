package com.laodev.masapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.laodev.masapp.model.DirectionModel;
import com.laodev.masapp.ui.DirectionCell;

import java.util.List;

public class DirectionAdapter extends BaseAdapter {

    private Context context;
    private List<DirectionModel> mDirections;

    public DirectionAdapter(Context context, List<DirectionModel> directions) {
        this.context = context;
        mDirections = directions;
    }

    @Override
    public int getCount() {
        return mDirections.size();
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
        DirectionModel directionModel = mDirections.get(position);

        DirectionCell cell = new DirectionCell(context, directionModel);
        cell.setDirectionCellCallback(new DirectionCell.DirectionCellCallback() {
            @Override
            public void onClickLocationSet(DirectionModel model) {
                for (DirectionModel direction: mDirections) {
                    direction.isCheck = "false";
                }
                model.isCheck = "true";
                DirectionModel.updateLocationsToFB(mDirections, new DirectionModel.DirectionModelListener() {
                    @Override
                    public void onSuccess(List<DirectionModel> directionModels) {
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailed(String error) {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onClickLocationDelete(DirectionModel model) {
                mDirections.remove(model);
                if (model.isCheck.equals("true")) {
                    if (mDirections.size() > 0) {
                        mDirections.get(0).isCheck = "true";
                    }
                }
                DirectionModel.updateLocationsToFB(mDirections, new DirectionModel.DirectionModelListener() {
                    @Override
                    public void onSuccess(List<DirectionModel> directionModels) {
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailed(String error) {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return cell;
    }

}
