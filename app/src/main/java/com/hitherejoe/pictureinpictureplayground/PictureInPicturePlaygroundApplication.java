package com.hitherejoe.pictureinpictureplayground;

import android.app.Application;
import android.content.Context;

import com.hitherejoe.pictureinpictureplayground.injection.component.ApplicationComponent;
import com.hitherejoe.pictureinpictureplayground.injection.component.DaggerApplicationComponent;
import com.hitherejoe.pictureinpictureplayground.injection.module.ApplicationModule;

import timber.log.Timber;

public class PictureInPicturePlaygroundApplication extends Application {

    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public static PictureInPicturePlaygroundApplication get(Context context) {
        return (PictureInPicturePlaygroundApplication) context.getApplicationContext();
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

}
