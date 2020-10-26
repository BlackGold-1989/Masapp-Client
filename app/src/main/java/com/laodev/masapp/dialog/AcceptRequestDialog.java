package com.laodev.masapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.laodev.masapp.R;

import java.util.Locale;


public class AcceptRequestDialog extends Dialog {

    private int count = 0;
    private AcceptRequestDialogListener acceptRequestDialogListener;


    public AcceptRequestDialog(@NonNull Context context, int count) {
        super(context);
        this.count = count;

        setContentView(R.layout.dialog_accept_request);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setTitle(null);
        setCanceledOnTouchOutside(true);

        initUIView();
    }

    private void initUIView() {
        TextView lbl_detail = findViewById(R.id.lbl_detail);
        lbl_detail.setText(String.format(Locale.getDefault(), getContext().getString(R.string.dia_accept_content), count));

        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> dismiss());

        Button btn_accept = findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(v -> {
            dismiss();
            acceptRequestDialogListener.onClick();
        });
    }

    public void setAcceptRequestDialogListener(AcceptRequestDialogListener acceptRequestDialogListener) {
        this.acceptRequestDialogListener = acceptRequestDialogListener;
    }

    public interface AcceptRequestDialogListener {
        void onClick();
    }

}
