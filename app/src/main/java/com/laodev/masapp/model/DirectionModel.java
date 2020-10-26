package com.laodev.masapp.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.laodev.masapp.util.FireConstants;

import java.util.ArrayList;
import java.util.List;

public class DirectionModel {

    public String userid = "";
    public String location = "";
    public String title = "";
    public String isCheck = "";
    public String address1 = "";
    public String address2 = "";

    public static void getCheckModelByUserID(String id, DirectionModelListener callback) {
        FireConstants.locationRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    DirectionModel model = dataSnapshot.getValue(DirectionModel.class);
                    if (model == null) {
                        continue;
                    }
                    if (model.isCheck.equals("true")) {
                        callback.onSuccess(model);
                        return;
                    }
                }
                callback.onFailed("No Found");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public static void updateLocationsToFB(List<DirectionModel> directionModels, DirectionModelListener callback) {
        FireConstants.locationRef.child(directionModels.get(0).userid).setValue(directionModels)
                .addOnSuccessListener(aVoid -> callback.onSuccess(directionModels))
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public static void getAllLocationsFromFB(UserModel userModel, DirectionModelListener callback) {
        FireConstants.locationRef.child(userModel.id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DirectionModel> directionModels = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    DirectionModel model = dataSnapshot.getValue(DirectionModel.class);
                    directionModels.add(model);
                }
                callback.onSuccess(directionModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public interface DirectionModelListener {
        default void onSuccess(List<DirectionModel> models) {}
        default void onSuccess(DirectionModel model) {}
        default void onFailed(String error) {}
    }

}
