package com.abanoub.rxjavadaggerexample.di;

import com.abanoub.rxjavadaggerexample.data.network.Services;
import com.abanoub.rxjavadaggerexample.data.repository.VideosRepository;
import com.abanoub.rxjavadaggerexample.data.repository.VideosRepositoryImp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Singleton
    @Provides
    public VideosRepository provideVideosRepository(Services services) {
        return new VideosRepositoryImp(services);
    }
}
