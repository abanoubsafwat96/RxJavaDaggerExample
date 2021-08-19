package com.abanoub.rxjavadaggerexample.main;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.abanoub.rxjavadaggerexample.data.model.Video;
import com.abanoub.rxjavadaggerexample.data.repository.VideosRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {

    private String TAG = "MainViewModel";
    MutableLiveData<List<Video>> videosLiveData = new MutableLiveData<List<Video>>();
    MutableLiveData<String> showMsgLiveDate = new MutableLiveData<>();

    VideosRepository repository;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public MainViewModel(VideosRepository repository) {
        this.repository = repository;
    }

    public void getVideos(InputStream is) {
        videosLiveData.setValue(parseJson(loadJSONFromAsset(is)));
    }

    public void getVideos() {
        Observable<List<Video>> observable = repository.getVideos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        compositeDisposable.add(
                observable.subscribe(
                        result -> videosLiveData.setValue((result)),
                        error -> Log.e(TAG, "getVideos: " + error)));
    }

    public String loadJSONFromAsset(InputStream is) {
        String json;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public List<Video> parseJson(String json) {
        Type reviewType = new TypeToken<List<Video>>() {
        }.getType();

        List<Video> videos = new Gson().fromJson(json, reviewType);
        return videos;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
