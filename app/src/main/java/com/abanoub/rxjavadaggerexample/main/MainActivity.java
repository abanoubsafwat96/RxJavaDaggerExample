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
    private Integer downloadPosition;

    @Inject
    MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videosRv = findViewById(R.id.videosRv);

        AppComponent appComponent = DaggerAppComponent.create();
        appComponent.injectMainActivity(this);

        requestPermissionsIfNotGranted();
        getVideosFromLocalJsonFile();
//        viewModel.getVideos();

        viewModel.videosLiveData.observe(this, videos -> {
            if (videos == null) return;

            adapter = new VideosAdapter(videos, new VideosAdapter.OnItemClick() {
                @Override
                public void onDownloadBtnClicked(Video video, int position) {
                    if (Utils.isNetworkAvailable(MainActivity.this)) {
                        viewModel.downloadFile(video);
                        downloadPosition = position;
                    } else
                        viewModel.showMsgLiveDate.setValue("Check your network connection");
                }
            });
            videosRv.setAdapter(adapter);
        });

        viewModel.showMsgLiveDate.observe(this, msg -> {
            if (msg == null) return;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        viewModel.progressLiveDate.observe(this, progress -> {
            if (progress == null || downloadPosition == null) return;
            Log.e("progress: ", progress + "");
            adapter.videosList.get(downloadPosition).setDownloadProgress(progress);
            adapter.notifyDataSetChanged();
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

    private void requestPermissionsIfNotGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Returns whether the calling app has All Files Access on the primary shared/external storage media.
            //Manifest.permission.MANAGE_EXTERNAL_STORAGE
            if (!Environment.isExternalStorageManager()) {
                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // user rejected the permission
                requestPermissionsIfNotGranted();
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //user allowed the permission
                getVideosFromLocalJsonFile();
            }
        }
    }
}