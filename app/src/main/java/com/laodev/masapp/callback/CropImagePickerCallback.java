package com.laodev.masapp.callback;

import android.graphics.Bitmap;

public interface CropImagePickerCallback {
    void onSuccess(Bitmap bitmap);
    void onFailed(String error);
}
