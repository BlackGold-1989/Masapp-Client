package com.laodev.masapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;
import com.laodev.masapp.activity.calling.VoiceScreenActivity;
import com.laodev.masapp.activity.placespicker.Place;
import com.laodev.masapp.callback.AddDirectionCallback;
import com.laodev.masapp.callback.CardPickerCallback;
import com.laodev.masapp.callback.CropImagePickerCallback;
import com.laodev.masapp.callback.LocationPickerCallback;
import com.laodev.masapp.callback.OnCallingListener;
import com.laodev.masapp.fragment.HistoryFragment;
import com.laodev.masapp.fragment.buyer.BuyerHomeAcceptFragment;
import com.laodev.masapp.fragment.buyer.BuyerHomePendingFragment;
import com.laodev.masapp.fragment.buyer.BuyerHomePostFragment;
import com.laodev.masapp.fragment.buyer.BuyerLocationFragment;
import com.laodev.masapp.fragment.buyer.BuyerOrderFragment;
import com.laodev.masapp.fragment.buyer.BuyerProfileFragment;
import com.laodev.masapp.fragment.buyer.BuyerRequestFragment;
import com.laodev.masapp.fragment.seller.SellerHomeFragment;
import com.laodev.masapp.fragment.seller.SellerOrderFragment;
import com.laodev.masapp.fragment.seller.SellerProfileFragment;
import com.laodev.masapp.fragment.seller.SellerSearchFragment;
import com.laodev.masapp.model.CardModel;
import com.laodev.masapp.model.DirectionModel;
import com.laodev.masapp.model.UserModel;
import com.laodev.masapp.service.SinchService;
import com.laodev.masapp.util.AppUtil;
import com.laodev.masapp.util.Constants;
import com.laodev.masapp.util.FireConstants;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

import static com.laodev.masapp.util.AppUtil.REQUESTCODE;
import static com.laodev.masapp.util.Constants.ADD_DIRECTION_REQUEST;
import static com.laodev.masapp.util.Constants.PICK_CARD_REQUEST;
import static com.laodev.masapp.util.Constants.PICK_LOCATION_REQUEST;

public class MainActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        SinchService.StartFailedListener {

    public BottomNavigationView bottomNavigation;
    private OnCallingListener callingListener = new OnCallingListener() {
        @Override
        public void onVoiceCallingEvent(UserModel user) {
            try {
                Call call = getSinchServiceInterface().callUser(user.id);
                if (call == null) {
                    Toast.makeText(MainActivity.this, "Service is not started. Try stopping the service and starting it again before "
                            + "placing a call.", Toast.LENGTH_LONG).show();
                    return;
                }
                AppUtil.gSelUser = user;

                String callId = call.getCallId();
                Intent callScreen = new Intent(MainActivity.this, VoiceScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                startActivity(callScreen);
            } catch (MissingPermissionException e) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{e.getRequiredPermission()}, 0);
            }
        }

        @Override
        public void onVideoCallingEvent(UserModel user) {

        }
    };
    private CropImagePickerCallback imagePickerCallback;
    private LocationPickerCallback locationPickerCallback;
    private CardPickerCallback cardPickerCallback;
    private AddDirectionCallback directionCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUIView();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                FireConstants.tokenRef.child(AppUtil.gCurrentUser.id).child("token").setValue(token)
                        .addOnSuccessListener(aVoid -> {  })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            existApp();
                        });
            }
        });
    }

    private void initUIView() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.setSelectedItemId(R.id.action_home);

        if (AppUtil.gCurrentUser.type.equals(Constants.USER_SELLER)) {
            bottomNavigation.getMenu().findItem(R.id.action_location).setIcon(R.drawable.ic_search);
            bottomNavigation.getMenu().findItem(R.id.action_location).setTitle("Search");
        }
        loadFragmentByIndex(0);
    }

    private void loadFragmentByIndex(int index) {
        Fragment frg = null;
        switch (index) {
            case 0:
                if (AppUtil.gCurrentUser.type.equals(Constants.USER_BUYER)) {
                    switch (AppUtil.gCurrentUser.status) {
                        case Constants.STATUS_READY:
                            frg = new BuyerHomePostFragment(this);
                            break;
                        case Constants.STATUS_PENDDING:
                            frg = new BuyerHomePendingFragment();
                            break;
                        case Constants.STATUS_ACCEPT:
                            frg = new BuyerHomeAcceptFragment();
                            break;
                    }
                } else {
                    frg = new SellerHomeFragment(this);
                }
                break;
            case 1:
                if (AppUtil.gCurrentUser.type.equals(Constants.USER_BUYER)) {
                    frg = new BuyerLocationFragment(this);
                } else {
                    frg = new SellerSearchFragment(this);
                }
                break;
            case 2:
                if (AppUtil.gCurrentUser.type.equals(Constants.USER_BUYER)) {
                    switch (AppUtil.gCurrentUser.status) {
                        case Constants.STATUS_READY:
                            frg = new BuyerHomePendingFragment();
                            break;
                        case Constants.STATUS_PENDDING:
                            frg = new BuyerRequestFragment(this);
                            break;
                        case Constants.STATUS_ACCEPT:
                            BuyerOrderFragment orderFragment = new BuyerOrderFragment(this);
                            orderFragment.setOnCallingListener(callingListener);
                            frg = orderFragment;
                            break;
                    }
                } else {
                    SellerOrderFragment orderFragment = new SellerOrderFragment(this);
                    orderFragment.setOnCallingListener(callingListener);
                    frg = orderFragment;
                }
                break;
            case 3:
                frg = new HistoryFragment(this);
                break;
            case 4:
                if (AppUtil.gCurrentUser.type.equals(Constants.USER_BUYER)) {
                    frg = new BuyerProfileFragment(this);
                } else {
                    frg = new SellerProfileFragment(this);
                }
                break;
        }
        onLoadFragment(frg);
    }

    private void onLoadFragment(Fragment frg) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction frgTran = fm.beginTransaction();
        frgTran.replace(R.id.frg_main, frg);
        frgTran.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                loadFragmentByIndex(0);
                break;
            case R.id.action_location:
                loadFragmentByIndex(1);
                break;
            case R.id.action_order:
                loadFragmentByIndex(2);
                break;
            case R.id.action_history:
                loadFragmentByIndex(3);
                break;
            case R.id.action_setting:
                loadFragmentByIndex(4);
                break;
        }
        return true;
    }

    @Override
    public void onStartFailed(SinchError error) {

    }

    @Override
    public void onStarted() {

    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
        if (getSinchServiceInterface() != null) {
            if (!AppUtil.gCurrentUser.id.equals(getSinchServiceInterface().getUserName())) {
                getSinchServiceInterface().stopClient();
            }
            if (!getSinchServiceInterface().isStarted()) {
                getSinchServiceInterface().startClient(AppUtil.gCurrentUser.id);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    imagePickerCallback.onSuccess(bitmap);
                } catch (IOException e) {
                    imagePickerCallback.onFailed(e.getMessage());
                }
            }
        }

        if (REQUESTCODE == PICK_LOCATION_REQUEST) {
            Place place;
            if (data != null) {
                place = data.getParcelableExtra(Place.EXTRA_PLACE);
                LatLng ll;
                if (place != null) {
                    ll = place.getLatLng();
                    String latlng = String.format("%s,%s", ll.latitude, ll.longitude);
                    locationPickerCallback.onSuccess(latlng);
                }
            }
        }

        if (REQUESTCODE == PICK_CARD_REQUEST) {
            CardModel cardModel;
            if (data != null) {
                cardModel = new Gson().fromJson(data.getStringExtra("CARDINFO"), CardModel.class);
                if (cardModel != null) {
                    cardPickerCallback.onSuccess(cardModel);
                }
            }
        }

        if (REQUESTCODE == ADD_DIRECTION_REQUEST) {
            DirectionModel directionModel;
            if (data != null) {
                directionModel = new Gson().fromJson(data.getStringExtra("DIRECTIONINFO"), DirectionModel.class);
                if (directionModel != null) {
                    directionCallback.onSuccess(directionModel);
                }
            }
        }
    }

    public void setImagePickerCallback(CropImagePickerCallback imagePickerCallback) {
        this.imagePickerCallback = imagePickerCallback;
    }

    public void setLocationPickerCallback(LocationPickerCallback locationPickerCallback) {
        this.locationPickerCallback = locationPickerCallback;
    }

    public void setCardPickerCallback(CardPickerCallback cardPickerCallback) {
        this.cardPickerCallback = cardPickerCallback;
    }

    public void setDirectionCallback(AddDirectionCallback directionCallback) {
        this.directionCallback = directionCallback;
    }

}
