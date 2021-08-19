package com.abanoub.rxjavadaggerexample.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.abanoub.rxjavadaggerexample.BuildConfig;
import com.abanoub.rxjavadaggerexample.R;
import com.abanoub.rxjavadaggerexample.data.model.Video;
import com.abanoub.rxjavadaggerexample.di.AppComponent;
import com.abanoub.rxjavadaggerexample.di.DaggerAppComponent;
import com.abanoub.rxjavadaggerexample.utils.Utils;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    RecyclerView videosRv;
    VideosAdapter adapter;

    @Inject
    MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videosRv = findViewById(R.id.videosRv);

        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.injectMainActivity(this);

        getVideosFromLocalJsonFile();
//        viewModel.getVideos();

        viewModel.videosLiveData.observe(this, videos -> {
            if (videos == null) return;

            adapter = new VideosAdapter(videos, new VideosAdapter.OnItemClick() {
                @Override
                public void onDownloadBtnClicked(Video video, int position) {

                }
            });
            videosRv.setAdapter(adapter);
        });

        viewModel.showMsgLiveDate.observe(this, msg -> {
            if (msg == null) return;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void getVideosFromLocalJsonFile() {
        try {
            InputStream is = getAssets().open("getListOfFilesResponse.json");
            viewModel.getVideos(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}