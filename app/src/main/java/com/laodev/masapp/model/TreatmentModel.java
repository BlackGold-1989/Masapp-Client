package com.laodev.masapp.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.laodev.masapp.util.FireConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreatmentModel {

    public String userid = "";
    public String doctorid = "";
    public String datetime = "";
    public String diagnostico = "";
    public String treatment = "";
    public String symptoms = "";

    public static void getMedicalsFromFB(String userid, TreatmentModelCallback callback) {
        FireConstants.medicalRef.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<TreatmentModel> treatmentModels = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    TreatmentModel model = dataSnapshot.getValue(TreatmentModel.class);
                    treatmentModels.add(model);
                }
                callback.onSuccess(treatmentModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public void addMedicalHistoryToFB(TreatmentModelCallback callback) {
        TreatmentModel.getMedicalsFromFB(this.userid, new TreatmentModelCallback() {
            @Override
            public void onSuccess(List<TreatmentModel> models) {
                List<TreatmentModel> treatmentModels = new ArrayList<>();
                treatmentModels.addAll(models);
                Collections.sort(treatmentModels, (obj1, obj2) -> obj2.datetime.compareToIgnoreCase(obj1.datetime));
                treatmentModels.add(0, TreatmentModel.this);
                FireConstants.medicalRef.child(TreatmentModel.this.userid).setValue(treatmentModels)
                        .addOnSuccessListener(aVoid -> callback.onSuccess(treatmentModels))
                        .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
            }

            @Override
            public void onFailed(String error) {
                callback.onFailed(error);
            }
        });
    }

    public void updateMedicalHistoryToFB(TreatmentModelCallback callback) {
        TreatmentModel.getMedicalsFromFB(this.userid, new TreatmentModelCallback() {
            @Override
            public void onSuccess(List<TreatmentModel> models) {
                List<TreatmentModel> treatmentModels = new ArrayList<>();
                treatmentModels.addAll(models);
                int index = 0;
                for (int i = 0; i < treatmentModels.size(); i++) {
                    TreatmentModel treatmentModel = treatmentModels.get(i);
                    if (treatmentModel.datetime.equals(TreatmentModel.this.datetime)) {
                        index = i;
                        break;
                    }
                }
                treatmentModels.set(index, TreatmentModel.this);
                FireConstants.medicalRef.child(TreatmentModel.this.userid).setValue(treatmentModels)
                        .addOnSuccessListener(aVoid -> callback.onSuccess(treatmentModels))
                        .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
            }

            @Override
            public void onFailed(String error) {
                callback.onFailed(error);
            }
        });
    }

    public interface TreatmentModelCallback {
        default void onSuccess(List<TreatmentModel> models) {}
        default void onFailed(String error) {}
    }

}
