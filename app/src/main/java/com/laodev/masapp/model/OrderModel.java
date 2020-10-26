package com.laodev.masapp.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

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

public class OrderModel {

    public String id = "";
    public String userid = "";
    public String doctorid = "";
    public String title = "";
    public String sub_title = "";
    public String desc = "";
    public String datetime = "";
    public String starttime = "";
    public String endtime = "";
    public String location = "";
    public List<String> imgurls = new ArrayList<>();

    public static void getAllOrdersByID(String doctorid, OrderModelCallback callback) {
        FireConstants.orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<OrderModel> orderModels = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
                    if (orderModel != null && orderModel.doctorid.equals(doctorid)) {
                        orderModels.add(orderModel);
                    }
                }
                callback.onSuccess(orderModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public static void getOrderByUserID(String userid, OrderModelCallback callback) {
        FireConstants.orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    OrderModel orderModel = dataSnapshot.getValue(OrderModel.class);
                    if (orderModel != null && orderModel.userid.equals(userid)) {
                        callback.onSuccess(orderModel);
                        return;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public static void getOrderByID(String orderid, OrderModelCallback callback) {
        FireConstants.orderRef.child(orderid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                OrderModel orderModel = snapshot.getValue(OrderModel.class);
                callback.onSuccess(orderModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public void addOrderToFB(OrderModelCallback callback) {
        id = FireConstants.orderRef.push().getKey();
        FireConstants.orderRef.child(id).setValue(this)
                .addOnSuccessListener(aVoid -> callback.onSuccess(this))
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public void updateOrderToFB(OrderModelCallback callback) {
        FireConstants.orderRef.child(id).setValue(this)
                .addOnSuccessListener(aVoid -> callback.onSuccess(this))
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public void removeOrderToFB(OrderModelCallback callback) {
        FireConstants.orderRef.child(id).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess(this))
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public void uploadProfile(Bitmap bitmap, OrderModelCallback callback) {
        String filename = randomUUID().toString() + ".jpg";

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference childRef = storageRef.child(filename);

        String storagePath = "orders/" + filename;
        final StorageReference mountainImagesRef = storageRef.child(storagePath);

        childRef.getName().equals(mountainImagesRef.getName());    // true
        childRef.getPath().equals(mountainImagesRef.getPath());    // false

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> mountainImagesRef.getDownloadUrl().addOnSuccessListener(downloadPhotoUrl -> {
            String url = downloadPhotoUrl.toString();
            FireConstants.userRef.child(id).child("imgurl").setValue(url)
                    .addOnSuccessListener(aVoid -> {
                        callback.onSuccess(url);
                    })
                    .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
        }))
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public interface OrderModelCallback {
        default void onSuccess(List<OrderModel> orderModels) {}
        default void onSuccess(OrderModel orderModel) {}
        default void onSuccess(String url) {}
        default void onFailed(String error) {}
    }

}
