package com.laodev.masapp.callback;

import com.laodev.masapp.model.UserModel;

public interface OnCallingListener {
    void onVoiceCallingEvent(UserModel user);
    void onVideoCallingEvent(UserModel user);
}
