package com.hitherejoe.pictureinpictureplayground.ui.content;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.hitherejoe.pictureinpictureplayground.R;
import com.hitherejoe.pictureinpictureplayground.ui.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContentActivity extends BaseActivity {

    @Bind(R.id.frame_container) FrameLayout mFragmentContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        ButterKnife.bind(this);

        getFragmentManager().beginTransaction()
                .replace(mFragmentContainer.getId(), ContentFragment.newInstance()).commit();
    }

    @Override
    public boolean onSearchRequested() {
        return true;
    }

}