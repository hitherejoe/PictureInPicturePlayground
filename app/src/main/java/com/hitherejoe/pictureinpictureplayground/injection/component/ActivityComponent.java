package com.hitherejoe.pictureinpictureplayground.injection.component;

import com.hitherejoe.pictureinpictureplayground.injection.PerActivity;
import com.hitherejoe.pictureinpictureplayground.injection.module.ActivityModule;
import com.hitherejoe.pictureinpictureplayground.ui.content.ContentFragment;
import com.hitherejoe.pictureinpictureplayground.ui.playback.PlaybackActivity;

import dagger.Component;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(PlaybackActivity playbackActivity);
    void inject(ContentFragment contentFragment);

}