package com.laodev.masapp.activity.seller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.khoiron.actionsheets.ActionSheet;
import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;
import com.laodev.masapp.adapter.CardAdapter;
import com.laodev.masapp.model.CardModel;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;
import com.laodev.masapp.util.SharedPreferenceUtil;
import com.sinch.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class CardListActivity extends BaseActivity {

    private final int GET_NEW_CARD = 2;
    private final int EDIT_CARD = 5;

    private TextView lbl_empty;

    private List<CardModel> paymentAccounts = new ArrayList<>();
    private CardAdapter cardAapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        getWindow().setStatusBarColor(getColor(R.color.colorBlue));

        initUIView();
    }

    private void initUIView() {
        ListView lst_card = findViewById(R.id.lst_card);
        cardAapter = new CardAdapter(this, paymentAccounts);
        cardAapter.setCardAdapterCallback(new CardAdapter.CardAdapterCallback() {
            @Override
            public void onCheckCardModel(CardModel cardModel) {
                int index = 0;
                for (int i = 0; i < paymentAccounts.size(); i++) {
                    CardModel cardInfo = paymentAccounts.get(i);
                    if (cardInfo.number.equals(cardModel.number)) {
                        index = i;
                    }
                    paymentAccounts.get(i).isCheck = "false";
                }
                cardModel.isCheck = "true";
                paymentAccounts.set(index, cardModel);
                cardAapter.notifyDataSetChanged();

                SharedPreferenceUtil.setPaymentAccount(new Gson().toJson(paymentAccounts));
            }

            @Override
            public void onDeleteCardModel(CardModel cardModel) {
                paymentAccounts.remove(cardModel);
                cardAapter.notifyDataSetChanged();

                SharedPreferenceUtil.setPaymentAccount(new Gson().toJson(paymentAccounts));
            }

            @Override
            public void onEditCardModel(CardModel cardModel) {
                if (cardModel.expired.equals("Paypal")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(CardListActivity.this);
                    final EditText edittext = new EditText(CardListActivity.this);
                    edittext.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    edittext.setText(cardModel.number);
                    alert.setMessage("Editar cuenta paypal");
                    alert.setView(edittext);
                    alert.setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
                        String email = edittext.getText().toString();
                        if (email.contains("@") && email.contains(".")) {
                            int index = 0;
                            for (int i = 0; i < paymentAccounts.size(); i++) {
                                CardModel model = paymentAccounts.get(i);
                                if (model.number.equals(cardModel.number)) {
                                    index = i;
                                    break;
                                }
                            }

                            CardModel newCardModel = new CardModel();

                            newCardModel.name = AppUtil.gCurrentUser.name;
                            newCardModel.number = email;
                            newCardModel.expired = "Paypal";
                            newCardModel.cvv = "Paypal";
                            newCardModel.isCheck = "true";
                            newCardModel.type = Constants.CARD_TYPE_PAYPAL;

                            paymentAccounts.set(index, newCardModel);
                            SharedPreferenceUtil.setPaymentAccount(new Gson().toJson(paymentAccounts));
                            initData();
                        }
                    });
                    alert.setNegativeButton(android.R.string.cancel, null);
                    alert.show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(CardListActivity.this);
                    final EditText edittext = new EditText(CardListActivity.this);
                    edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                    edittext.setText(cardModel.number);
                    alert.setMessage("Editar cuenta bancos");
                    alert.setView(edittext);
                    alert.setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
                        String email = edittext.getText().toString();
                        if (email.length() == 16) {
                            int index = 0;
                            for (int i = 0; i < paymentAccounts.size(); i++) {
                                CardModel model = paymentAccounts.get(i);
                                if (model.number.equals(cardModel.number)) {
                                    index = i;
                                    break;
                                }
                            }

                            CardModel newCardModel = new CardModel();

                            newCardModel.name = AppUtil.gCurrentUser.name;
                            newCardModel.number = email;
                            newCardModel.expired = "Card";
                            newCardModel.cvv = "Card";
                            newCardModel.isCheck = "true";
                            newCardModel.type = Constants.CARD_TYPE_CREDIT;

                            paymentAccounts.set(index, newCardModel);
                            SharedPreferenceUtil.setPaymentAccount(new Gson().toJson(paymentAccounts));
                            initData();
                        }
                    });
                    alert.setNegativeButton(android.R.string.cancel, null);
                    alert.show();
                }
            }
        });
        lst_card.setAdapter(cardAapter);

        lbl_empty = findViewById(R.id.lbl_empty);
        Button btn_edit = findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(view -> onFinishEditCard());

        initData();
    }

    private void initData() {
        paymentAccounts.clear();
        paymentAccounts.addAll(SharedPreferenceUtil.getPaymentAccounts());
        cardAapter.notifyDataSetChanged();
        if (paymentAccounts.size() > 0) {
            lbl_empty.setVisibility(View.GONE);
        } else {
            lbl_empty.setVisibility(View.VISIBLE);
        }
    }

    public void onClickBackImg(View view) {
        onBackPressed();
    }

    private void onFinishEditCard() {
        Intent data = new Intent();
        String jsonCard = new Gson().toJson(SharedPreferenceUtil.getSeletedCardModel());
        data.putExtra("CARDINFO", jsonCard);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    public void onClickAddCard(View view) {
        ArrayList<String> data = new ArrayList<>();
        data.add("Paypal");
        data.add("Agregar cuenta de bancos");
        new ActionSheet(this,data)
                .setTitle("Seleccionar método")
                .setCancelTitle("Cancelar")
                .create((data1, position) -> {
                    switch (position){
                        case 0:{
                            // your action
                            AlertDialog.Builder alert = new AlertDialog.Builder(this);
                            final EditText edittext = new EditText(this);
                            edittext.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            alert.setMessage("Agregar cuenta paypal");
                            alert.setView(edittext);
                            alert.setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
                                String email = edittext.getText().toString();
                                if (email.contains("@") && email.contains(".")) {
                                    CardModel cardModel = new CardModel();

                                    cardModel.name = AppUtil.gCurrentUser.name;
                                    cardModel.number = email;
                                    cardModel.expired = "Paypal";
                                    cardModel.cvv = "Paypal";
                                    cardModel.isCheck = "true";
                                    cardModel.type = Constants.CARD_TYPE_PAYPAL;

                                    for (CardModel model: paymentAccounts) {
                                        model.isCheck = "false";
                                    }
                                    paymentAccounts.add(cardModel);
                                    SharedPreferenceUtil.setPaymentAccount(new Gson().toJson(paymentAccounts));
                                    initData();
                                }
                            });
                            alert.setNegativeButton(android.R.string.cancel, null);
                            alert.show(); }
                            break;
                        case 1: {
                            // your action
//                            Intent intent = new Intent(CardListActivity.this, CardEditActivity.class);
//                            startActivityForResult(intent, GET_NEW_CARD);
                            AlertDialog.Builder alert = new AlertDialog.Builder(this);
                            final EditText edittext = new EditText(this);
                            edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                            alert.setMessage("Agregar cuenta de bancos");
                            alert.setView(edittext);
                            alert.setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
                                String email = edittext.getText().toString();
                                if (email.length() != 16) {
                                    return;
                                }

                                CardModel cardModel = new CardModel();

                                cardModel.name = AppUtil.gCurrentUser.name;
                                cardModel.number = email;
                                cardModel.expired = "Card";
                                cardModel.cvv = "Card";
                                cardModel.isCheck = "true";
                                cardModel.type = Constants.CARD_TYPE_CREDIT;

                                for (CardModel model: paymentAccounts) {
                                    model.isCheck = "false";
                                }
                                paymentAccounts.add(cardModel);
                                SharedPreferenceUtil.setPaymentAccount(new Gson().toJson(paymentAccounts));
                                initData();
                            });
                            alert.setNegativeButton(android.R.string.cancel, null);
                            alert.show(); }
                            break;
                    }
                });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == GET_NEW_CARD) {
            if (resultCode == RESULT_OK) {
                CardModel cardModel = new CardModel();

                cardModel.name = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
                cardModel.number = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
                cardModel.expired = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
                cardModel.cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);
                cardModel.isCheck = "true";
                cardModel.type = Constants.CARD_TYPE_CREDIT;

                for (CardModel model: paymentAccounts) {
                    model.isCheck = "false";
                }
                paymentAccounts.add(cardModel);
                SharedPreferenceUtil.setPaymentAccount(new Gson().toJson(paymentAccounts));
                initData();
            }
        }

        if (reqCode == EDIT_CARD) {
            if (resultCode == RESULT_OK) {
                CardModel cardModel = new CardModel();

                cardModel.name = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
                cardModel.number = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
                cardModel.expired = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
                cardModel.cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);
                cardModel.isCheck = "true";
                cardModel.type = Constants.CARD_TYPE_CREDIT;

                int index = 0;
                for (int i = 0; i < paymentAccounts.size(); i++) {
                    CardModel model = paymentAccounts.get(i);
                    if (model.number.equals(AppUtil.gCardModel.number)) {
                        index = i;
                        break;
                    }
                }
                paymentAccounts.set(index, cardModel);
                SharedPreferenceUtil.setPaymentAccount(new Gson().toJson(paymentAccounts));
                initData();
            }
        }
    }

}
