package com.laodev.masapp.activity.buyer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.laodev.masapp.R;
import com.laodev.masapp.model.CommentModel;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.TimerUtil;

public class CommentActivity extends AppCompatActivity {

    private EditText txt_email, txt_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initUIView();
    }

    private void initUIView() {
        txt_email = findViewById(R.id.txt_email);
        txt_comment = findViewById(R.id.txt_comment);
    }

    public void onClickBackImg(View view) {
        onBackPressed();
    }

    public void onClickSubmitBtn(View view) {
        String email = txt_email.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(CommentActivity.this, R.string.toast_no_email, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            Toast.makeText(CommentActivity.this, R.string.toast_email_formate, Toast.LENGTH_SHORT).show();
            return;
        }
        String comment = txt_comment.getText().toString();
        if (comment.length() < 6) {
            Toast.makeText(CommentActivity.this, R.string.toast_comment, Toast.LENGTH_SHORT).show();
            return;
        }

        CommentModel commentModel = new CommentModel();
        commentModel.userid = AppUtil.gCurrentUser.id;
        commentModel.email = email;
        commentModel.content = comment;
        commentModel.regdate = TimerUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
        commentModel.uploadCommentToFirebase(new CommentModel.CommentModelListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(CommentActivity.this, R.string.success_submit, Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(CommentActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
