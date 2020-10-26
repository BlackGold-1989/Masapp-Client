package com.laodev.masapp.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.laodev.masapp.R;


//Compat Class to make Vector Drawables work on Older APIs when using DrawabeLeft,Right,Top,Bottom
public class TextViewDrawableCompat extends AppCompatTextView {
    public TextViewDrawableCompat(Context context) {
        super(context);
        initAttrs(context, null);
    }

    public TextViewDrawableCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public TextViewDrawableCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributeArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.TextViewDrawableCompat);

            Drawable drawableStart;
            Drawable drawableEnd;
            Drawable drawableBottom;
            Drawable drawableTop;

            drawableStart = attributeArray.getDrawable(R.styleable.TextViewDrawableCompat_drawableStartCompat);
            drawableEnd = attributeArray.getDrawable(R.styleable.TextViewDrawableCompat_drawableEndCompat);
            drawableBottom = attributeArray.getDrawable(R.styleable.TextViewDrawableCompat_drawableBottomCompat);
            drawableTop = attributeArray.getDrawable(R.styleable.TextViewDrawableCompat_drawableTopCompat);

            int tintColor = attributeArray.getColor(R.styleable.TextViewDrawableCompat_drawableTintCompat, -1);
            if (tintColor != -1) {
                if (drawableStart != null)
                    drawableStart.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
                if (drawableEnd != null)
                    drawableEnd.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
                if (drawableTop != null)
                    drawableTop.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
                if (drawableBottom != null)
                    drawableBottom.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
            }

            // to support rtl
            setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd, drawableBottom);
            attributeArray.recycle();
        }
    }
}


