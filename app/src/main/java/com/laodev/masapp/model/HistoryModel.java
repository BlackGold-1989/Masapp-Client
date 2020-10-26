package com.laodev.masapp.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;
import com.laodev.masapp.util.FireConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HistoryModel {

    public OrderModel orderModel;
    public int rating;
    public String comment;

    public static void getRecentHistories(UserModel userModel, HistoryModelCallback callback) {
        FireConstants.historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float average = 0.0f;
                List<HistoryModel> historyModels = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    HistoryModel historyModel = dataSnapshot.getValue(HistoryModel.class);
                    if (historyModel != null) {
                        if (userModel.type.equals(Constants.USER_BUYER)) {
                            if (historyModel.orderModel.userid.equals(userModel.id)) {
                                historyModels.add(historyModel);
                                average += historyModel.rating;
                            }
                        } else {
                            if (historyModel.orderModel.doctorid.equals(userModel.id)) {
                                historyModels.add(historyModel);
                                average += historyModel.rating;
                            }
                        }
                    }
                }
                Collections.sort(historyModels, (obj1, obj2) -> obj2.orderModel.endtime.compareToIgnoreCase(obj1.orderModel.endtime));

                if (historyModels.size() > 0) {
                    List<HistoryModel> results = new ArrayList<>();
                    if (historyModels.size() > 1) {
                        results.add(historyModels.get(historyModels.size() - 1));
                        results.add(historyModels.get(historyModels.size() - 2));
                    } else {
                        results.add(historyModels.get(historyModels.size() - 1));
                    }
                    callback.onSuccess(results, String.format(Locale.getDefault(), "%.1f", (float)(average / historyModels.size())));
                } else {
                    callback.onSuccess(historyModels, "No Reviews");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public void addHistory(HistoryModelCallback callback) {
        FireConstants.historyRef.child(orderModel.id).setValue(this)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public static void getHistoriesByUser(UserModel user, HistoryModelCallback callback) {
        FireConstants.historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HistoryModel> historyModels = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    HistoryModel historyModel = dataSnapshot.getValue(HistoryModel.class);
                    if (historyModel == null) {
                        continue;
                    }
                    if (user.type.equals(Constants.USER_SELLER)) {
                        if (historyModel.orderModel.doctorid.equals(user.id)) {
                            historyModels.add(historyModel);
                        }
                    } else {
                        if (historyModel.orderModel.userid.equals(user.id)) {
                            historyModels.add(historyModel);
                        }
                    }
                }
                Collections.sort(historyModels, (obj1, obj2) -> obj2.orderModel.endtime.compareToIgnoreCase(obj1.orderModel.endtime));
                callback.onSuccess(historyModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public interface HistoryModelCallback {
        default void onSuccess(List<HistoryModel> historyModels, String average) {}
        default void onSuccess(List<HistoryModel> historyModels) {}
        default void onSuccess() {}
        default void onFailed(String error){}
    }

}
