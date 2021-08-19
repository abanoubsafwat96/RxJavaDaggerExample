package com.abanoub.rxjavadaggerexample.data.repository;

import com.abanoub.rxjavadaggerexample.data.model.Video;

import java.util.List;

import io.reactivex.Observable;

public interface VideosRepository {
    Observable<List<Video>> getVideos();
}
