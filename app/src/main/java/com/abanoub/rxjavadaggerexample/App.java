package com.abanoub.rxjavadaggerexample;

import android.app.Application;

import com.abanoub.rxjavadaggerexample.di.AndroidModule;
import com.abanoub.rxjavadaggerexample.di.DaggerAppComponent;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent.builder()
                .androidModule(new AndroidModule(getApplicationContext()))
                .build();
    }
}
