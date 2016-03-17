package com.hitherejoe.pictureinpictureplayground.data;

import com.hitherejoe.pictureinpictureplayground.data.local.PreferencesHelper;
import com.hitherejoe.pictureinpictureplayground.data.remote.AndroidTvBoilerplateService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DataManager {

    private final AndroidTvBoilerplateService mTvAndroidTvBoilerplateService;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    public DataManager(PreferencesHelper preferencesHelper,
                       AndroidTvBoilerplateService androidTvBoilerplateService) {
        mPreferencesHelper = preferencesHelper;
        mTvAndroidTvBoilerplateService = androidTvBoilerplateService;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

}
