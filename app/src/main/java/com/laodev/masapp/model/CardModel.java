package com.laodev.masapp.model;

import com.laodev.masapp.util.Constants;

public class CardModel {

    public String type = "";
    public String isCheck = "";

    public String number = "";
    public String name = "";
    public String cvv = "";
    public String expired = "";

    public String getPublicCardNumber() {
        if (number.isEmpty()) {
            return "Por favor agregue el pago";
        }
        if (type.equals(Constants.CARD_TYPE_PAYPAL)) {
            return number;
        }
        String sub = number.substring(12);
        return "**** **** **** " + sub;
    }

}
