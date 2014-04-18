package com.example.Grass_Cutter;

import android.os.Bundle;

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

    private int buffSize = 8;
    private int throttle = 0;

    public class SeekbarHandler implements SeekBar.OnSeekBarChangeListener {
        private void updateThrottle(int newThrottle) {
            if (newThrottle > 100) {
                newThrottle = 100;
            }
            throttle = newThrottle;
            throttleStatusView.setText("Throttle: " + Integer.toString(newThrottle));
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            updateThrottle(0);
            seekBar.setProgress(0);
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