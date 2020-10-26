package com.laodev.masapp.util;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import cz.msebera.android.httpclient.client.cache.HeaderConstants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FireManager {

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static void sendPushNotificationToUser(String userid, String title, String body, PushNotificationCallback callback) {
        FireConstants.tokenRef.child(userid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String token = dataSnapshot.getValue(String.class);
                onSendFBPushnotification(token, title, body);
                callback.onSuccess("success");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed(databaseError.getMessage());
            }
        });
    }

    private static void onSendFBPushnotification(String token, String title, String sBody) {
        String json = "{\"to\":\"" + token + "\",\"notification\":{\"body\":\"" + sBody + "\",\"title\":\"" + title + "\"}}";
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .addHeader(HeaderConstants.AUTHORIZATION, Constants.SERVER_KEY)
                .addHeader("Content-Type", "application/json")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) { }
        });
    }

    public interface PushNotificationCallback {
        void onSuccess(String result);
        void onFailed(String error);
    }

}
