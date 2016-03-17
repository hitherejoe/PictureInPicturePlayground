package com.hitherejoe.pictureinpictureplayground.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.hitherejoe.pictureinpictureplayground.injection.ApplicationContext;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferencesHelper {

    private final SharedPreferences mPref;

    public static final String PREF_FILE_NAME = "tv_boilerplate_pref_file";

    @Inject
    public PreferencesHelper(@ApplicationContext Context context) {
        mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

}