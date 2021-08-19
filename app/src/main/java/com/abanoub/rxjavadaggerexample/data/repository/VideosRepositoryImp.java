package com.abanoub.rxjavadaggerexample.data.repository;

import com.abanoub.rxjavadaggerexample.data.model.Video;
import com.abanoub.rxjavadaggerexample.data.network.Services;

import java.util.List;

import io.reactivex.Observable;

public class VideosRepositoryImp implements VideosRepository{

    Services services;

    public VideosRepositoryImp(Services services) {
        this.services = services;
    }

    @Override
    public Observable<List<Video>> getVideos() {
        return services.getVideos();
    }
}
