package com.abanoub.rxjavadaggerexample.di;

import android.content.Context;
import android.content.res.Resources;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {

    Context context;

    public AndroidModule(Context context) {
        this.context = context;
    }

    @Provides
    Context providesContext() {
        return context;
    }

    @Provides
    Resources providesResources() {
        return context.getResources();
    }
}