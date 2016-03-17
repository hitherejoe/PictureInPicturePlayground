package com.hitherejoe.pictureinpictureplayground.injection.component;

import android.app.Application;
import android.content.Context;

import com.hitherejoe.pictureinpictureplayground.data.DataManager;
import com.hitherejoe.pictureinpictureplayground.data.local.PreferencesHelper;
import com.hitherejoe.pictureinpictureplayground.injection.ApplicationContext;
import com.hitherejoe.pictureinpictureplayground.injection.module.ApplicationModule;
import com.hitherejoe.pictureinpictureplayground.util.RxEventBus;

import javax.inject.Singleton;

import dagger.Component;
import rx.subscriptions.CompositeSubscription;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    @ApplicationContext
    Context context();
    Application application();
    PreferencesHelper preferencesHelper();
    DataManager dataManager();
    CompositeSubscription compositeSubscription();
    RxEventBus eventBus();

}