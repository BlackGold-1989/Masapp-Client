package com.laodev.masapp.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireConstants {

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static final DatabaseReference mainRef = database.getReference();

    //users ref that contain user's data (name,phone,photo etc..)
    public static final DatabaseReference userRef = mainRef.child("user");

    // order
    public static final DatabaseReference orderRef = mainRef.child("order");

    // location
    public static final DatabaseReference locationRef = mainRef.child("location");

    // request
    public static final DatabaseReference requestRef = mainRef.child("request");

    // history
    public static final DatabaseReference historyRef = mainRef.child("history");

    // medical
    public static final DatabaseReference medicalRef = mainRef.child("medical");

    // card
    public static final DatabaseReference cardRef = mainRef.child("card");

    // payment
    public static final DatabaseReference paymentRef = mainRef.child("payment");

    // token
    public static final DatabaseReference tokenRef = mainRef.child("token");

    // comment
    public static final DatabaseReference commentRef = mainRef.child("comment");


}
