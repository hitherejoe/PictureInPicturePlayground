package com.hitherejoe.pictureinpictureplayground.ui.content;

import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.content.ContextCompat;

import com.hitherejoe.pictureinpictureplayground.R;
import com.hitherejoe.pictureinpictureplayground.data.BusEvent;
import com.hitherejoe.pictureinpictureplayground.data.model.Video;
import com.hitherejoe.pictureinpictureplayground.ui.base.BaseActivity;
import com.hitherejoe.pictureinpictureplayground.ui.common.CardPresenter;
import com.hitherejoe.pictureinpictureplayground.ui.playback.PlaybackActivity;
import com.hitherejoe.pictureinpictureplayground.util.RxEventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ContentFragment extends BrowseFragment {

    private ArrayObjectAdapter mRowsAdapter;
    private Handler mHandler;
    private Runnable mBackgroundRunnable;
    @Inject RxEventBus mEventBus;

    public static ContentFragment newInstance() {
        return new ContentFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BaseActivity) getActivity()).activityComponent().inject(this);
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        mHandler = new Handler();
        setAdapter(mRowsAdapter);
        setupUIElements();
        setupListeners();
        showVideos();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBackgroundRunnable != null) {
            mHandler.removeCallbacks(mBackgroundRunnable);
            mBackgroundRunnable = null;
        }
    }

    private void setupUIElements() {
        setBadgeDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.banner_browse));
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.primary));
        setSearchAffordanceColor(ContextCompat.getColor(getActivity(), R.color.accent));
    }

    private void setupListeners() {
        setOnItemViewClickedListener(mOnItemViewClickedListener);
    }

    private OnItemViewClickedListener mOnItemViewClickedListener = new OnItemViewClickedListener() {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            // This is currently just a quick fix to kill any current PiP windows when we start a
            // new PlaybackActivity. Ideally, the system should handle this OR we should re-use the
            // PlaybackActivity that is currently in PiP-mode, loading a new video into it and
            // bringing the PiP back into full-screen.
            mEventBus.post(new BusEvent.PlaybackActivityStarted());
            startActivity(PlaybackActivity.newStartIntent(getActivity(), (Video) item));
        }
    };

    public void showVideos() {
        List<Video> videos = new ArrayList<>();
        videos.add(new Video(0, "Lake", "Lake Time-lapse", R.drawable.lake, R.raw.lake));
        videos.add(new Video(1, "NYC", "New York City Time-lapse", R.drawable.nyc, R.raw.nyc));

        final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        listRowAdapter.addAll(0, videos);
        HeaderItem header = new HeaderItem(0, getString(R.string.header_title_videos));
        mRowsAdapter.add(new ListRow(header, listRowAdapter));
    }

}