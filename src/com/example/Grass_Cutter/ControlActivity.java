package com.example.Grass_Cutter;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

public class ControlActivity extends SensorProcessorActivity {
    private String path = "rtmp://192.168.2.1/live/v2r";
    private VideoView mVideoView;
    private EditText buffSizeView;
    private ToggleButton toggleVideoButton;
    private SeekBar seekBarWidget;
    private TextView throttleStatusView;
    private TextView gyroStatusView;

    private int buffSize = 3;
    private int throttle = 0;

    public class SeekbarHandler implements SeekBar.OnSeekBarChangeListener {
        private int startPoint = 254;
        private int maxDelta = 0;

        private void updateThrottle(int newThrottle) {
            if (startPoint == 254) {
                startPoint = newThrottle;
                Log.d("Throttle", "Set startPoint: " + newThrottle + " " + startPoint);
            }

            Log.d("Throttle", "startPoint: " + startPoint);

            if (newThrottle > startPoint) {
                throttle = Math.min(newThrottle - startPoint, 100);
            }

            if (newThrottle < startPoint) {
                throttle = Math.max(newThrottle - startPoint, -100);
            }

            throttleStatusView.setText("Throttle: " + Integer.toString(throttle));
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            startPoint = 254;

            throttle = 0;
            throttleStatusView.setText("Throttle: " + Integer.toString(0));
            seekBar.setProgress(seekBar.getMax() / 2);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateThrottle(progress);
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (!LibsChecker.checkVitamioLibs(this))
            return;

        buffSizeView = (EditText) findViewById(R.id.buffEditText);
        buffSizeView.setText(Integer.toString(buffSize));

        seekBarWidget = (SeekBar) findViewById(R.id.seekBar);
        toggleVideoButton = (ToggleButton) findViewById(R.id.toggleButton);
        throttleStatusView = (TextView) findViewById(R.id.throttleStatus);
        gyroStatusView = (TextView) findViewById(R.id.gyroStatus);

        seekBarWidget.setOnSeekBarChangeListener(new SeekbarHandler());

        mVideoView = (VideoView) findViewById(R.id.videoView);
        mVideoView.setVideoPath(path);
        mVideoView.requestFocus();
        mVideoView.setBufferSize(buffSize);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
    }

    public void toggleVideo(View view) {
        if (toggleVideoButton.isChecked()) {
            buffSize = Integer.parseInt(buffSizeView.getText().toString());

            mVideoView.setVideoPath(path);
            mVideoView.requestFocus();
            mVideoView.setBufferSize(buffSize);
        } else {
            mVideoView.stopPlayback();
        }
    }

    public void updateOrientationDisplay() {
        gyroStatusView.setText("Angle: " + d.format(gyroOrientation[1] * 180 / Math.PI));
    }
}