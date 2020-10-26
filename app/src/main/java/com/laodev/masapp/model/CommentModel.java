package com.laodev.masapp.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.laodev.masapp.util.FireConstants;

public class CommentModel {

    public String userid = "";
    public String email = "";
    public String content = "";
    public String regdate = "";

    public void uploadCommentToFirebase(CommentModelListener callback) {
        FireConstants.commentRef.child(userid).child(regdate).setValue(this)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public interface CommentModelListener {
        void onSuccess();
        void onFailed(String error);
    }

}
