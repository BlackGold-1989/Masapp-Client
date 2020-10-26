package com.laodev.masapp.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.laodev.masapp.R;
import com.laodev.masapp.model.HistoryModel;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.TimerUtil;

import java.util.ArrayList;
import java.util.List;


public class ReviewDialog extends Dialog implements View.OnClickListener {

    private List<ImageView> lst_imgs = new ArrayList<>();
    private EditText txt_comment;

    private int reviewCnt = 5;
    private ReviewDialogListener reviewDialogListener;


    private void initWithEvent() {

    }

    public ReviewDialog(@NonNull Context context) {
        super(context);

        setContentView(R.layout.dialog_review);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setTitle(null);
        setCanceledOnTouchOutside(true);

        initUIView();
        initWithEvent();
    }

    private void initUIView() {
        ImageView img_01 = findViewById(R.id.img_review_01);
        img_01.setOnClickListener(this);
        ImageView img_02 = findViewById(R.id.img_review_02);
        img_02.setOnClickListener(this);
        ImageView img_03 = findViewById(R.id.img_review_03);
        img_03.setOnClickListener(this);
        ImageView img_04 = findViewById(R.id.img_review_04);
        img_04.setOnClickListener(this);
        ImageView img_05 = findViewById(R.id.img_review_05);
        img_05.setOnClickListener(this);

        lst_imgs.add(img_01);
        lst_imgs.add(img_02);
        lst_imgs.add(img_03);
        lst_imgs.add(img_04);
        lst_imgs.add(img_05);

        txt_comment = findViewById(R.id.txt_review_content);
        Button btn_submit = findViewById(R.id.btn_review_submit);
        btn_submit.setOnClickListener(this);

        refreshStar();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_review_01:
                reviewCnt = 1;
                refreshStar();
                break;
            case R.id.img_review_02:
                reviewCnt = 2;
                refreshStar();
                break;
            case R.id.img_review_03:
                reviewCnt = 3;
                refreshStar();
                break;
            case R.id.img_review_04:
                reviewCnt = 4;
                refreshStar();
                break;
            case R.id.img_review_05:
                reviewCnt = 5;
                refreshStar();
                break;
            case R.id.btn_review_submit:
                if (txt_comment.getText().toString().length() == 0) {
                    Toast.makeText(getContext(), "No comment text.", Toast.LENGTH_SHORT).show();
                    return;
                }
                HistoryModel historyModel = new HistoryModel();
                AppUtil.gOrderModel.endtime = TimerUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
                historyModel.orderModel = AppUtil.gOrderModel;
                historyModel.comment = txt_comment.getText().toString();
                historyModel.rating = reviewCnt;

                ProgressDialog dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.pgr_connect_server));
                historyModel.addHistory(new HistoryModel.HistoryModelCallback() {
                    @Override
                    public void onSuccess() {
                        historyModel.orderModel.removeOrderToFB(new OrderModel.OrderModelCallback() {
                            @Override
                            public void onSuccess(OrderModel orderModel) {
                                dialog.dismiss();
                                reviewDialogListener.onSuccessSubmit();
                            }

                            @Override
                            public void onFailed(String error) {
                                dialog.dismiss();
                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailed(String error) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    private void refreshStar() {
        for (int i = 0; i < lst_imgs.size(); i++) {
            ImageView img_review = lst_imgs.get(i);
            if (i < reviewCnt) {
                img_review.setImageResource(R.drawable.ic_star_fill);
            } else {
                img_review.setImageResource(R.drawable.ic_star);
            }
        }
    }

    public void setReviewDialogListener(ReviewDialogListener reviewDialogListener) {
        this.reviewDialogListener = reviewDialogListener;
    }

    public interface ReviewDialogListener {
        void onSuccessSubmit();
    }

}
