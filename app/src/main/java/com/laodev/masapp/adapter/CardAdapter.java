package com.laodev.masapp.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.laodev.masapp.R;
import com.laodev.masapp.activity.seller.CardListActivity;
import com.laodev.masapp.model.CardModel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardAdapter extends BaseAdapter {

    private final int EDIT_CARD = 5;

    private CardListActivity context;
    private List<CardModel> mCardModels;

    private CardAdapterCallback cardAdapterCallback;
    private SparseArray<Pattern> mCCPatterns = null;
    private int mCurrentDrawableResId = 0;


    public CardAdapter(CardListActivity context, List<CardModel> models) {
        this.context = context;
        mCardModels = models;

        if (mCCPatterns == null) {
            mCCPatterns = new SparseArray<>();
            mCCPatterns.put(R.drawable.visa, Pattern.compile(
                    "^4[0-9]{2,12}(?:[0-9]{3})?$"));
            mCCPatterns.put(R.drawable.mastercard, Pattern.compile(
                    "^5[1-5][0-9]{1,14}$"));
            mCCPatterns.put(R.drawable.amex, Pattern.compile(
                    "^3[47][0-9]{1,13}$"));
        }
    }

    @Override
    public int getCount() {
        return mCardModels.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "ViewHolder", "SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public View getView(int position, View containView, ViewGroup parent) {
        CardModel cardModel = mCardModels.get(position);

        containView = LayoutInflater.from(context).inflate(R.layout.ui_card_cell, null);

        TextView lbl_name = containView.findViewById(R.id.lbl_name);
        lbl_name.setText(cardModel.name);
        TextView lbl_number = containView.findViewById(R.id.lbl_number);
        lbl_number.setText(cardModel.number);
        TextView lbl_expire = containView.findViewById(R.id.lbl_expire);
        lbl_expire.setText("Expired Date : " + cardModel.expired);
        ImageView img_check = containView.findViewById(R.id.img_check);
        if (cardModel.isCheck.equals("true")) {
            img_check.setVisibility(View.VISIBLE);
        } else {
            img_check.setVisibility(View.GONE);
        }

        containView.setOnClickListener(view -> {
            if (cardAdapterCallback != null) {
                cardAdapterCallback.onCheckCardModel(cardModel);
            }
        });
        ImageView img_edit = containView.findViewById(R.id.img_edit);
        img_edit.setOnClickListener(view -> {
//            Intent intent = new Intent(context, CardEditActivity.class);
//            intent.putExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME, cardModel.name);
//            intent.putExtra(CreditCardUtils.EXTRA_CARD_NUMBER, cardModel.number);
//            intent.putExtra(CreditCardUtils.EXTRA_CARD_EXPIRY, cardModel.expired);
//            intent.putExtra(CreditCardUtils.EXTRA_CARD_SHOW_CARD_SIDE, CreditCardUtils.CARD_SIDE_BACK);
//            intent.putExtra(CreditCardUtils.EXTRA_VALIDATE_EXPIRY_DATE, true);
//
//            context.startActivityForResult(intent, EDIT_CARD);
            cardAdapterCallback.onEditCardModel(cardModel);
        });
        ImageView img_delete = containView.findViewById(R.id.img_delete);
        img_delete.setOnClickListener(view -> {
            if (cardAdapterCallback != null) {
                cardAdapterCallback.onDeleteCardModel(cardModel);
            }
        });

        ImageView img_card = containView.findViewById(R.id.img_card);
        if (cardModel.number.contains("@")) {
            img_card.setImageResource(R.drawable.cio_ic_paypal_monogram);
            img_card.setColorFilter(ContextCompat.getColor(context, R.color.blue_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else {
            int mDrawableResId = 0;
            for (int i = 0; i < mCCPatterns.size(); i++) {
                int key = mCCPatterns.keyAt(i);
                // get the object by the key.
                Pattern p = mCCPatterns.get(key);
                Matcher m = p.matcher(cardModel.number);
                if (m.find()) {
                    mDrawableResId = key;
                    break;
                }
            }
            if (mDrawableResId > 0 && mDrawableResId !=
                    mCurrentDrawableResId) {
                mCurrentDrawableResId = mDrawableResId;
            } else if (mDrawableResId == 0) {
                //default credit card image
                mCurrentDrawableResId = R.drawable.creditcard;
            }
            Drawable mCurrentDrawable = context.getResources().getDrawable(mCurrentDrawableResId);
            img_card.setImageDrawable(mCurrentDrawable);
        }

        return containView;
    }

    public void setCardAdapterCallback(CardAdapterCallback cardAdapterCallback) {
        this.cardAdapterCallback = cardAdapterCallback;
    }

    public interface CardAdapterCallback {
        void onCheckCardModel(CardModel cardModel);
        void onDeleteCardModel(CardModel cardModel);
        void onEditCardModel(CardModel cardModel);
    }

}
