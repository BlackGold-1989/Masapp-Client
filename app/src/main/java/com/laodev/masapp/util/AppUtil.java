package com.laodev.masapp.util;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;

import com.laodev.masapp.R;
import com.laodev.masapp.model.CardModel;
import com.laodev.masapp.model.OrderModel;
import com.laodev.masapp.model.TreatmentModel;
import com.laodev.masapp.model.UserModel;

public class AppUtil {

    public static int REQUESTCODE = 0;

    public static UserModel gCurrentUser = new UserModel();
    public static UserModel gSelUser = new UserModel();
    public static OrderModel gOrderModel = new OrderModel();
    public static CardModel gCardModel = new CardModel();
    public static boolean isAddMedical = false;
    public static TreatmentModel gTreatModel = new TreatmentModel();

    public static void showOtherActivity (Context context, Class<?> cls, int direction) {
        Intent myIntent = new Intent(context, cls);
        ActivityOptions options;
        switch (direction) {
            case 0:
                options = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left);
                context.startActivity(myIntent, options.toBundle());
                break;
            case 1:
                options = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_left, R.anim.slide_out_right);
                context.startActivity(myIntent, options.toBundle());
                break;
            default:
                context.startActivity(myIntent);
                break;
        }
    }

}
