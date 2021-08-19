package com.abanoub.rxjavadaggerexample.di;


import com.abanoub.rxjavadaggerexample.constants.Constants;
import com.abanoub.rxjavadaggerexample.data.network.Services;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
class RetrofitModule {
    @Singleton
    @Provides
    public Services provideApiServices() {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(Services.class);
    }
}
