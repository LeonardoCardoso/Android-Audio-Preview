package com.leocardz.android.audio.preview;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private final String URL = "https://dl.dropbox.com/s/hgcgq510urmgmed/01%20Main%20Title.mp3";

    private View board;
    private EditText urlEdittext;
    private Button actionButton, pause, stop;

    private LinearLayout buttons, loading;

    private TextView songTitle, songArtist;

    private Animation songSlideUp, songSlideDown;

    private RelativeLayout.LayoutParams params;

    private MediaPlayer mediaPlayer;

    private boolean isAnimating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpIds();

        urlEdittext.setText(URL);

        setUpListeners();

        setUpMediaPlayer();
    }

    private void setUpMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
            mediaPlayer.setOnCompletionListener(completionListener);
        }
    }

    private void setUpIds() {
        songSlideUp = AnimationUtils.loadAnimation(this, R.anim.song_slide_up);
        songSlideDown = AnimationUtils.loadAnimation(this, R.anim.song_slide_down);

        isAnimating = false;

        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        board = findViewById(R.id.board);
        urlEdittext = (EditText) findViewById(R.id.url_edittext);
        actionButton = (Button) findViewById(R.id.action_button);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);
        buttons = (LinearLayout) findViewById(R.id.buttons);
        loading = (LinearLayout) findViewById(R.id.loading);
        songTitle = (TextView) findViewById(R.id.song_title);
        songArtist = (TextView) findViewById(R.id.song_artist);
    }

    private void setUpListeners() {
        actionButton.setOnClickListener(startListener);
        pause.setOnClickListener(pauseListener);
        stop.setOnClickListener(stopListener);
        songSlideUp.setAnimationListener(slideUpAnimationListener);
        songSlideDown.setAnimationListener(slideDownAnimationListener);
    }

    private Animation.AnimationListener slideUpAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isAnimating = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isAnimating = false;
            params.setMargins(0, 0, 0,  getResources().getDimensionPixelSize(R.dimen.info_height));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            board.setLayoutParams(params);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private Animation.AnimationListener slideDownAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isAnimating = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isAnimating = false;
            params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.info_height_offset));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            board.setLayoutParams(params);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private void streamSong(String url) {
        if (mediaPlayer != null && !isAnimating) {
            try {
                mediaPlayer.setDataSource(urlEdittext.getText().toString());
                mediaPlayer.setOnPreparedListener(preparedListener);
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mediaPlayer != null && !mediaPlayer.isPlaying() && !isAnimating) {
                songTitle.setText("Main Theme");
                songArtist.setText("Game of Thrones");
                loading.setVisibility(View.VISIBLE);
                buttons.setVisibility(View.GONE);
                streamSong(URL);
                board.startAnimation(songSlideUp);
            }
        }
    };

    private View.OnClickListener pauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            toggleMediaPlayer();
        }
    };

    private View.OnClickListener stopListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mediaPlayer != null && !isAnimating) {
                mediaPlayer.stop();
                pause.setText(R.string.play);
                board.startAnimation(songSlideDown);
            }
        }
    };

    private void toggleMediaPlayer() {
        if (mediaPlayer != null && !isAnimating) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                pause.setText(R.string.pause);
            } else {
                mediaPlayer.pause();
                pause.setText(R.string.play);
            }
        }
    }

    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }
    };

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.stop();
            pause.setText(R.string.pause);
            actionButton.setEnabled(true);
            actionButton.setText(R.string.play);
        }
    };

    private MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            loading.setVisibility(View.GONE);
            buttons.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onStop();
    }

}
