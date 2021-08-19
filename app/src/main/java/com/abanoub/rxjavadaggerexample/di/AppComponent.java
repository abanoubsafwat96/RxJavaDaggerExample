package com.abanoub.rxjavadaggerexample.di;

import com.abanoub.rxjavadaggerexample.main.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AndroidModule.class, RetrofitModule.class, RepositoryModule.class})
public interface AppComponent {
    // Classes that can be injected by this Component
    void injectMainActivity(MainActivity activity);
}