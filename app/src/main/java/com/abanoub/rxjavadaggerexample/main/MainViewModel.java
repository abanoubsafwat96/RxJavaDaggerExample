package com.abanoub.rxjavadaggerexample.main;

import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.abanoub.rxjavadaggerexample.data.model.Video;
import com.abanoub.rxjavadaggerexample.data.repository.VideosRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {

    private String TAG = "MainViewModel";
    MutableLiveData<List<Video>> videosLiveData = new MutableLiveData<List<Video>>();
    MutableLiveData<String> showMsgLiveDate = new MutableLiveData<>();
    MutableLiveData<Integer> progressLiveDate = new MutableLiveData<>();

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

    public void downloadFile(Video clickedVideo) {

        Observable<Object> observable = Observable.create(emitter -> {
            downloadHelper(clickedVideo, emitter);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observer observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NotNull Integer o) {
                progressLiveDate.postValue(o);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                showMsgLiveDate.setValue(e.getMessage());
            }

            @Override
            public void onComplete() {
                showMsgLiveDate.setValue("download completed");
            }
        };
        observable.subscribe(observer);
    }

    private void downloadHelper(Video clickedVideo, ObservableEmitter<Object> emitter) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(clickedVideo.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                emitter.onError(new Throwable("Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage()));
            } else {
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                String[] arr = clickedVideo.getUrl().split("/");
                String videoNameWithExtention = arr[arr.length - 1];

                // download the file
                input = connection.getInputStream();
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), videoNameWithExtention);
                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    int percentage;
                    if (fileLength > 0) { // only if total length is known
                        percentage = (int) (total * 100 / fileLength);
                        emitter.onNext(percentage);
                    }
                    output.write(data, 0, count);
                }
            }
        } catch (Exception e) {
            emitter.onError(new Throwable(e.getMessage()));
        } finally {
            emitter.onComplete();
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
                emitter.onError(new Throwable(ignored.getMessage()));
            }
            if (connection != null)
                connection.disconnect();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
