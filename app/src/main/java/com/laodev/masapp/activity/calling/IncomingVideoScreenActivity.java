package com.laodev.masapp.activity.calling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laodev.masapp.BaseActivity;
import com.laodev.masapp.R;
import com.laodev.masapp.service.SinchService;
import com.laodev.masapp.util.AppUtil;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.video.VideoCallListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class IncomingVideoScreenActivity extends BaseActivity {

    static final String TAG = IncomingVideoScreenActivity.class.getSimpleName();
    private String mCallId;
    private AudioPlayer mAudioPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_video);

        LinearLayout answer = findViewById(R.id.answerButton);
        answer.setOnClickListener(mClickListener);
        LinearLayout decline = findViewById(R.id.declineButton);
        decline.setOnClickListener(mClickListener);

        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
    }

    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());

            TextView remoteUser = findViewById(R.id.remoteUser);
            ImageView img_user = findViewById(R.id.img_income_avatar);

            remoteUser.setText(AppUtil.gSelUser.name);
            Picasso.get().load(AppUtil.gSelUser.imgurl).fit().centerCrop()
                    .placeholder(R.drawable.ic_account_circle)
                    .error(R.drawable.ic_account_circle)
                    .into(img_user, null);

        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.answer();

            Intent intent = new Intent(this, VideoScreenActivity.class);
            intent.putExtra(SinchService.CALL_ID, mCallId);
            startActivity(intent);
        } else {
            finish();
        }
    }

    private void declineClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
    }

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            mAudioPlayer.stopRingtone();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            // Display some kind of icon showing it's a video call
            // and pass it to the CallScreenActivity via Intent and mAcceptVideo
            boolean mAcceptVideo = true;
        }
        @Override
        public void onVideoTrackPaused(Call call) {
            // Display some kind of icon showing it's a video call
        }
        @Override
        public void onVideoTrackResumed(Call call) {
            // Display some kind of icon showing it's a video call
        }
    }

    private OnClickListener mClickListener = v -> {
        switch (v.getId()) {
            case R.id.answerButton:
                answerClicked();
                break;
            case R.id.declineButton:
                declineClicked();
                break;
        }
    };
}
