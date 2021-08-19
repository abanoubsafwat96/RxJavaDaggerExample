package com.abanoub.rxjavadaggerexample.data.network;

import com.abanoub.rxjavadaggerexample.data.model.Video;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface Services {
    @GET("movies")
    Observable<List<Video>> getVideos();
}
