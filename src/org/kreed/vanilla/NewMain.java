package org.kreed.vanilla;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: pcoder
 * Date: 28/04/13
 * Time: 10:38
 *
 */
public class NewMain extends PlaybackActivity
        implements SeekBar.OnSeekBarChangeListener {



    /**
     * Update the seekbar progress with the current song progress. This must be
     * called on the UI Handler.
     */
    private static final int MSG_UPDATE_PROGRESS = 10;
    /**
     * Save the hidden_controls preference to storage.
     */
    private static final int MSG_SAVE_CONTROLS = 14;
    /**
     * Call {@link #loadExtraInfo()}.
     */
    private static final int MSG_LOAD_EXTRA_INFO = 15;
    /**
     * Pass obj to mExtraInfo.setText()
     */
    private static final int MSG_COMMIT_INFO = 16;
    /**
     * Calls {@link #updateQueuePosition()}.
     */
    private static final int MSG_UPDATE_POSITION = 17;

    /**
     * True if the controls are visible (play, next, seek bar, etc).
     */
    private boolean mControlsVisible;

    private SeekBar mSeekBar;
    /**
     * Current song duration in milliseconds.
     */
    private TextView mDurationView;
    private boolean mSeekBarTracking;
    private boolean mPaused;

    private TextView mElapsedView;

    /**
     * Cached StringBuilder for formatting track position.
     */
    private final StringBuilder mTimeBuilder = new StringBuilder();
    /**
     * Current song duration in milliseconds.
     */
    private long mDuration;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //startActivity(new Intent(this, FullPlaybackActivity.class));
        Toast.makeText(getApplicationContext(), "This is a test", Toast.LENGTH_LONG).show();

        setTitle(R.string.playback_view);

        SharedPreferences settings = PlaybackService.getSettings(this);
        //int layout = R.layout.full_playback;
        setContentView(R.layout.full_playback_alt);

        mSeekBar = (SeekBar)findViewById(R.id.seek_bar);
        mSeekBar.setMax(1000);
        mSeekBar.setOnSeekBarChangeListener(this);


        mElapsedView = (TextView)findViewById(R.id.elapsed);
        mShuffleButton = (ImageButton)findViewById(R.id.shuffle);
        mShuffleButton.setOnClickListener(this);
        registerForContextMenu(mShuffleButton);
        mEndButton = (ImageButton)findViewById(R.id.end_action);
        mDurationView = (TextView)findViewById(R.id.duration);
        mEndButton.setOnClickListener(this);
        registerForContextMenu(mEndButton);

        setControlsVisible(settings.getBoolean("visible_controls", true));
        setExtraInfoVisible(settings.getBoolean("visible_extra_info", false));
        setDuration(0);

    }
    /**
     * Update the current song duration fields.
     *
     * @param duration The new duration, in milliseconds.
     */
    private void setDuration(long duration)
    {
        mDuration = duration;
        mDurationView.setText(DateUtils.formatElapsedTime(mTimeBuilder, duration / 1000));
    }

    /**
     * Set the visibility of the extra metadata view.
     *
     * @param visible True to show, false to hide
     */
    private void setExtraInfoVisible(boolean visible)
    {
    }

    /**
     * Set the visibility of the controls views.
     *
     * @param visible True to show, false to hide
     */
    private void setControlsVisible(boolean visible)
    {
        int mode = visible ? View.VISIBLE : View.GONE;
        //mControlsTop.setVisibility(mode);
        //mControlsBottom.setVisibility(mode);
        //mControlsVisible = visible;

        if (visible) {
            //mPlayPauseButton.requestFocus();
            updateElapsedTime();
        }
    }

    /**
     * Update seek bar progress and schedule another update in one second
     */
    private void updateElapsedTime()
    {
        long position = PlaybackService.hasInstance() ? PlaybackService.get(this).getPosition() : 0;

        if (!mSeekBarTracking) {
            long duration = mDuration;
            mSeekBar.setProgress(duration == 0 ? 0 : (int)(1000 * position / duration));
        }

        mElapsedView.setText(DateUtils.formatElapsedTime(mTimeBuilder, position / 1000));

        if (!mPaused && mControlsVisible && (mState & PlaybackService.FLAG_PLAYING) != 0) {
            // Try to update right after the duration increases by one second
            long next = 1050 - position % 1000;
            mUiHandler.removeMessages(MSG_UPDATE_PROGRESS);
            mUiHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, next);
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mElapsedView.setText(DateUtils.formatElapsedTime(mTimeBuilder, progress * mDuration / 1000000));
            PlaybackService.get(this).seekToProgress(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}