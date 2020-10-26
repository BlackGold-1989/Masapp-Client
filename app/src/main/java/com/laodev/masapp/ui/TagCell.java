package com.laodev.masapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laodev.masapp.R;

public class TagCell extends LinearLayout {

    private TextView lbl_tag;

    public TagCell(Context context) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.item_tag_model, this, true);

        lbl_tag = findViewById(R.id.lbl_tag_title);
    }

    public void setTitle(String title) {
        lbl_tag.setText(title);
    }

}
