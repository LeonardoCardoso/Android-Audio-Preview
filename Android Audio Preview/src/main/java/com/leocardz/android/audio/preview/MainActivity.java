package com.leocardz.android.audio.preview;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;


public class MainActivity extends ActionBarActivity {

    private final String URL = "https://dl.dropbox.com/s/hgcgq510urmgmed/01%20Main%20Title.mp3";

    private View board;
    private EditText urlEdittext;
    private Button actionButton, pause, stop;

    private LinearLayout buttons, loading;

    private TextView songTitle, songArtist;

    private MediaPlayer mediaPlayer;

    private boolean isAnimating, isUp, isDown, isPlaying;

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

        isAnimating = isUp = isDown = isPlaying = false;

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
    }

    private Animator.AnimatorListener slideUpAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            isAnimating = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            isUp = true;
            isDown = false;
            isAnimating = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private Animator.AnimatorListener slideDownAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            isAnimating = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            isDown = true;
            isUp = false;
            isAnimating = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private void streamSong(String url) {
        if (mediaPlayer != null && !isAnimating) {
            try {
                mediaPlayer.setDataSource(url);
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
            try {
                if (mediaPlayer != null && !isPlaying && !isAnimating) {
                    songTitle.setText("Main Theme");
                    songArtist.setText("Game of Thrones");
                    loading.setVisibility(View.VISIBLE);
                    buttons.setVisibility(View.GONE);
                    streamSong(urlEdittext.getText().toString());
                    if (!isUp) {
                        slideUp();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void slideUp() {
        ObjectAnimator songSlideUp = ObjectAnimator.ofFloat(board, "translationY", -board.getHeight());
        songSlideUp.addListener(slideUpAnimationListener);
        songSlideUp.start();
    }

    private void slideDown() {
        ObjectAnimator songSlideDown = ObjectAnimator.ofFloat(board, "translationY", board.getHeight());
        songSlideDown.addListener(slideDownAnimationListener);
        songSlideDown.start();
    }

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
                releaseMediaPlayer();
                if (!isDown) {
                    slideDown();
                }
            }
        }
    };

    private void toggleMediaPlayer() {
        if (mediaPlayer != null && !isAnimating) {

            if (!isPlaying) {
                mediaPlayer.start();
                pause.setText(R.string.pause);
            } else {
                mediaPlayer.pause();
                pause.setText(R.string.play);
            }

            isPlaying = !isPlaying;
        }
    }

    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            if (mediaPlayer != null) {
                isPlaying = true;
                mediaPlayer.start();
            }
        }
    };

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            isPlaying = false;
            pause.setText(R.string.play);
            actionButton.setEnabled(true);
        }
    };

    private MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            loading.setVisibility(View.GONE);
            buttons.setVisibility(View.VISIBLE);
        }
    };

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                if (isPlaying) {
                    mediaPlayer.stop();
                    isPlaying = false;
                }
                mediaPlayer.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        slideDown();
        releaseMediaPlayer();
        super.onStop();
    }

}
