package com.laodev.masapp;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.laodev.masapp.service.SinchService;
import com.laodev.masapp.util.SharedPreferenceUtil;

import org.jetbrains.annotations.NotNull;

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {

    private SinchService.SinchServiceInterface mSinchServiceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService();
        SharedPreferenceUtil.getInstance(this);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

    @SuppressLint("HandlerLeak")
    private Messenger messenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SinchService.MESSAGE_PERMISSIONS_NEEDED) {
                Bundle bundle = msg.getData();
                String requiredPermission = bundle.getString(SinchService.REQUIRED_PERMISSION);
                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{requiredPermission}, 0);
            }
        }
    });

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, int[] grantResults) {
        boolean granted = grantResults.length > 0;
        for (int grantResult : grantResults) {
            granted &= grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (granted) {
            Toast.makeText(this, "Ahora puede hacer una llamada.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Esta aplicación necesita permiso para usar su micrófono y cámara para funcionar correctamente.", Toast.LENGTH_LONG).show();
        }
        if (mSinchServiceInterface !=  null) {
            mSinchServiceInterface.retryStartAfterPermissionGranted();
        }
    }

    private void bindService() {
        Intent serviceIntent = new Intent(this, SinchService.class);
        serviceIntent.putExtra(SinchService.MESSENGER, messenger);
        getApplicationContext().bindService(serviceIntent, this, BIND_AUTO_CREATE);
    }

    public void existApp() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}