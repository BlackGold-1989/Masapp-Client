package com.laodev.masapp.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.laodev.masapp.util.FireConstants;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

public class RequestModel {

    public String orderid = "";
    public UserModel user = new UserModel();
    public String desc = "";
    public String datetime = "";
    public List<String> imgurls = new ArrayList<>();

    public void submitRequest(RequestModelCallback callback) {
        FireConstants.requestRef.child(this.orderid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RequestModel> requestModels = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    RequestModel model = dataSnapshot.getValue(RequestModel.class);
                    if (model != null) {
                        requestModels.add(model);
                    }
                }
                requestModels.add(RequestModel.this);
                FireConstants.requestRef.child(RequestModel.this.orderid).setValue(requestModels)
                        .addOnSuccessListener(aVoid -> callback.onSuccess(RequestModel.this))
                        .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public void updateRequest(RequestModelCallback callback) {
        FireConstants.requestRef.child(this.orderid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RequestModel> requestModels = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    RequestModel model = dataSnapshot.getValue(RequestModel.class);
                    if (model != null) {
                        requestModels.add(model);
                    }
                }

                for (RequestModel model: requestModels) {
                    if (model.user.id.equals(RequestModel.this.user.id)) {
                        model.imgurls.addAll(RequestModel.this.imgurls);
                    }
                }
                FireConstants.requestRef.child(RequestModel.this.orderid).setValue(requestModels)
                        .addOnSuccessListener(aVoid -> callback.onSuccess(RequestModel.this))
                        .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public static void getAllRequests(String orderid, RequestModelCallback callback) {
        FireConstants.requestRef.child(orderid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RequestModel> requestModels = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    RequestModel model = dataSnapshot.getValue(RequestModel.class);
                    if (model != null) {
                        requestModels.add(model);
                    }
                }
                callback.onSuccess(requestModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public static void removeRequest(String orderId, RequestModelCallback callback) {
        FireConstants.requestRef.child(orderId).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public static void uploadImage(Bitmap bitmap, RequestModelCallback callback) {
        String filename = randomUUID().toString() + ".jpg";

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference childRef = storageRef.child(filename);

        String storagePath = "images/" + filename;
        final StorageReference mountainImagesRef = storageRef.child(storagePath);

        childRef.getName().equals(mountainImagesRef.getName());    // true
        childRef.getPath().equals(mountainImagesRef.getPath());    // false

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> mountainImagesRef.getDownloadUrl().addOnSuccessListener(downloadPhotoUrl -> {
            String url = downloadPhotoUrl.toString();
            callback.onSuccess(url);
        }))
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public interface RequestModelCallback {
        default void onSuccess(RequestModel model) {}
        default void onSuccess(List<RequestModel> models) {}
        default void onSuccess(String url) {}
        default void onSuccess() {}
        default void onFailed(String error) {}
    }

}
