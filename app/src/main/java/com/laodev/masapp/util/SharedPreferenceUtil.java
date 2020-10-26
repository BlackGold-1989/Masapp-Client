package com.laodev.masapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.laodev.masapp.model.CardModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceUtil {

    private static SharedPreferences sharedPreference;

    public static void getInstance(Context context) {
        sharedPreference = context.getSharedPreferences("com.laodev.masapp", MODE_PRIVATE);
    }

    public static boolean isSellerMode() {
        return sharedPreference.getBoolean("isseller", false);
    }

    public static void setSellerMode(boolean flag) {
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putBoolean("isseller", flag);
        edit.apply();
    }

    public static void setPaymentAccount(String info) {
        if (sharedPreference != null) {
            SharedPreferences.Editor editor = sharedPreference.edit();
            editor.putString("payment", info);
            editor.apply();
        }
    }

    public static CardModel getSeletedCardModel() {
        List<CardModel> cardModels = getPaymentAccounts();
        if (cardModels.size() == 0) {
            return new CardModel();
        }
        CardModel selCardModel = cardModels.get(0);
        for (CardModel cardModel: cardModels) {
            if (cardModel.isCheck.equals("true")) {
                selCardModel = cardModel;
                break;
            }
        }
        return  selCardModel;
    }

    public static List<CardModel> getPaymentAccounts() {
        List<CardModel> paymentAccounts = new ArrayList<>();
        String paymentsStr = sharedPreference.getString("payment", null);
        if (paymentsStr != null) {
            Type listType = new TypeToken<List<CardModel>>(){}.getType();
            paymentAccounts.addAll(new Gson().fromJson(paymentsStr, listType));
        }
        return paymentAccounts;
    }

    public static void clear() {
        sharedPreference.edit().clear();
        sharedPreference.edit().apply();
    }

}
