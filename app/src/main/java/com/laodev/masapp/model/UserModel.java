package com.laodev.masapp.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.FireConstants;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

public class UserModel {

    public String id = "";
    public String name = "";
    public String birth = "";
    public String gender = "";
    public String imgurl = "";
    public String email = "";
    public String phone = "";
    public String address1 = "";
    public String address2 = "";
    public String location = "";
    public String coins = "";
    public String card = "";
    public String type = "";
    public String status = "";
    public String spec1 = "";
    public String spec2 = "";
    public List<String> images = new ArrayList<>();


    public static void getOwnUser(UserModelInterface callback) {
        FireConstants.userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                if (userModel == null) {
                    callback.onNoFound();
                } else {
                    callback.onSuccess(userModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public static void getUserByID(String id, UserModelInterface callback) {
        FireConstants.userRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                if (userModel == null) {
                    callback.onNoFound();
                } else {
                    callback.onSuccess(userModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public void addUserToFB(UserModelInterface callback) {
        FireConstants.userRef.child(id).setValue(this)
                .addOnSuccessListener(aVoid -> callback.onSuccess(UserModel.this))
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public void uploadAvatar(Bitmap bitmap, UserModelInterface callback) {
        String filename = randomUUID().toString() + ".jpg";

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference childRef = storageRef.child(filename);

        String storagePath = "avatar/" + filename;
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
                        UserModel.this.imgurl = url;
                        callback.onSuccess(UserModel.this);
                    })
                    .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
        }))
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public void uploadProfile(Bitmap bitmap, UserModelInterface callback) {
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
            images.add(url);
            FireConstants.userRef.child(id).child("images").setValue(images)
                    .addOnSuccessListener(aVoid -> {
                        callback.onSuccess(url);
                    })
                    .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
        }))
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public interface UserModelInterface {
        default void onSuccess(UserModel user){}
        default void onSuccess(String url){}
        default void onSuccess(List<UserModel> users){}
        default void onFailed(String error){}
        default void onNoFound(){}
    }

}
