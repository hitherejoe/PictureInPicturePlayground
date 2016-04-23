package com.hitherejoe.pictureinpictureplayground.ui.playback;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hitherejoe.pictureinpictureplayground.R;
import com.hitherejoe.pictureinpictureplayground.data.BusEvent;
import com.hitherejoe.pictureinpictureplayground.data.model.Video;
import com.hitherejoe.pictureinpictureplayground.ui.base.BaseActivity;
import com.hitherejoe.pictureinpictureplayground.util.RxEventBus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * PlaybackActivity for video playback that loads PlaybackOverlayFragment and handles
 * the MediaSession object used to maintain the state of the media playback.
 */
public class PlaybackActivity extends BaseActivity {

    public static final String EXTRA_VIDEO = "EXTRA_VIDEO";

    @Inject RxEventBus mEventBus;

    @Bind(R.id.videoView) VideoView mVideoView;

    public enum LeanbackPlaybackState {
        PLAYING, PAUSED, IDLE
    }

    private LeanbackPlaybackState mPlaybackState;
    private MediaPlayer mMediaPlayer;
    private MediaSession mSession;
    private Video mCurrentPost;
    private Subscription mSubscription;

    private int mPosition;
    private long mStartTimeMillis;
    private long mDuration;

    public static Intent newStartIntent(Context context, Video video) {
        Intent intent = new Intent(context, PlaybackActivity.class);
        intent.putExtra(EXTRA_VIDEO, video);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlaybackState = LeanbackPlaybackState.IDLE;
        mPosition = 0;
        mDuration = -1;

        createMediaSession();
        setContentView(R.layout.activity_playback);
        ButterKnife.bind(this);

        activityComponent().inject(this);

        mCurrentPost = getIntent().getParcelableExtra(PlaybackActivity.EXTRA_VIDEO);
        if (mCurrentPost == null) {
            throw new IllegalArgumentException("PlaybackActivity requires a Video object!");
        }

        loadViews();
        playPause(true);

        // This is currently just a quick fix to kill any current PiP windows when we start a
        // new PlaybackActivity. Ideally, the system should handle this OR we should re-use the
        // PlaybackActivity that is currently in PiP-mode, loading a new video into it and
        // bringing the PiP back into full-screen.
        mSubscription = mEventBus.filteredObservable(BusEvent.PlaybackActivityStarted.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BusEvent.PlaybackActivityStarted>() {
                    @Override
                    public void call(BusEvent.PlaybackActivityStarted userSignedIn) {
                        finish();
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isInPictureInPictureMode() && mVideoView.isPlaying()) {
            if (!requestVisibleBehind(true)) {
                playPause(false);
            } else {
                requestVisibleBehind(false);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        playPause(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayback();
        mMediaPlayer.release();
        mVideoView.suspend();
        mVideoView.setVideoURI(null);
        mSession.release();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    @Override
    public void onVisibleBehindCanceled() {
        playPause(false);
        super.onVisibleBehindCanceled();
    }

    @Override
    public boolean onSearchRequested() {
        return true;
    }

    @Override
    public void onPictureInPictureModeChanged(boolean inPictureInPicture) {
        if (inPictureInPicture) {
            // Hide the controls in picture-in-picture mode.
        } else {
            // Restore the playback UI based on the playback status.
        }
    }

    private void loadViews() {
        mVideoView.setFocusable(false);
        mVideoView.setFocusableInTouchMode(false);
        setVideoPath("android.resource://" + getPackageName() + "/" + mCurrentPost.videoResource);
    }

    private void setPosition(int position) {
        if (position > mDuration) {
            mPosition = (int) mDuration;
        } else if (position < 0) {
            mPosition = 0;
        } else {
            mPosition = position;
        }
        mStartTimeMillis = System.currentTimeMillis();

    }

    private void createMediaSession() {
        if (mSession == null) {
            mSession = new MediaSession(this, getString(R.string.app_name));
            mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mSession.setActive(true);
            setMediaController(new MediaController(this, mSession.getSessionToken()));
        }
    }

    private void playPause(boolean doPlay) {
        if (mPlaybackState == LeanbackPlaybackState.IDLE) setupCallbacks();

        if (doPlay && mPlaybackState != LeanbackPlaybackState.PLAYING) {
            mPlaybackState = LeanbackPlaybackState.PLAYING;
            if (mPosition > 0) {
                mVideoView.seekTo(mPosition);
            }
            mVideoView.start();
            mStartTimeMillis = System.currentTimeMillis();
        } else {
            mPosition = mVideoView.getCurrentPosition();
            mPlaybackState = LeanbackPlaybackState.PAUSED;
            int timeElapsedSinceStart = (int) (System.currentTimeMillis() - mStartTimeMillis);
            setPosition(mPosition + timeElapsedSinceStart);
            mVideoView.pause();
        }
        updatePlaybackState();
    }

    private void updatePlaybackState() {
        PlaybackState.Builder stateBuilder =
                new PlaybackState.Builder().setActions(getAvailableActions());
        int state = PlaybackState.STATE_PLAYING;
        if (mPlaybackState == LeanbackPlaybackState.PAUSED
                || mPlaybackState == LeanbackPlaybackState.IDLE) {
            state = PlaybackState.STATE_PAUSED;
        }
        stateBuilder.setState(state, mPosition, 1.0f);
        mSession.setPlaybackState(stateBuilder.build());
    }

    private long getAvailableActions() {
        long actions = PlaybackState.ACTION_PLAY |
                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackState.ACTION_PLAY_FROM_SEARCH |
                PlaybackState.ACTION_SKIP_TO_NEXT |
                PlaybackState.ACTION_SKIP_TO_PREVIOUS |
                PlaybackState.ACTION_FAST_FORWARD |
                PlaybackState.ACTION_REWIND;

        if (mPlaybackState == LeanbackPlaybackState.PLAYING) actions |= PlaybackState.ACTION_PAUSE;
        return actions;
    }

    private void updateMetadata(Video video) {

        final MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();

        String title = video.description.replace("_", " -");
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_MEDIA_ID, video.title);
        String uri = "android.resource://" + getPackageName() + "/" + video.imageResource;
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE,
                video.title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION,
                video.description);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI,
                uri);
        metadataBuilder.putLong(MediaMetadata.METADATA_KEY_DURATION, mVideoView.getDuration());

        // And at minimum the title and artist for legacy support
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, video.description);

        Glide.with(this)
                .load(R.drawable.lake)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(500, 500) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, bitmap);
                        mSession.setMetadata(metadataBuilder.build());
                    }
                });

    }

    private void setupCallbacks() {
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mVideoView.stopPlayback();
                mPlaybackState = LeanbackPlaybackState.IDLE;
                return false;
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer = mp;
                mMediaPlayer.setLooping(true);
                updateMetadata(mCurrentPost);
            }
        });
    }

    private void stopPlayback() {
        if (mVideoView != null) mVideoView.stopPlayback();
    }

    private void setVideoPath(String videoUrl) {
        setPosition(0);
        mVideoView.setVideoPath(videoUrl);
        mStartTimeMillis = 0;
    }
}