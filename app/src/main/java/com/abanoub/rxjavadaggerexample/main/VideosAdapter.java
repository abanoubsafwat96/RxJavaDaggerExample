package com.abanoub.rxjavadaggerexample.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abanoub.rxjavadaggerexample.R;
import com.abanoub.rxjavadaggerexample.data.model.Video;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyViewHolder> {

    List<Video> videosList;
    OnItemClick itemCallback;

    public VideosAdapter(List<Video> videosList, OnItemClick itemCallback) {
        this.videosList = videosList;
        this.itemCallback = itemCallback;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.video_single_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Video item = videosList.get(position);

        holder.nameTv.setText(item.getName());
        holder.downloadBtn.setOnClickListener(v -> {
            itemCallback.onDownloadBtnClicked(item, position);
        });
        holder.progressBar.setProgress(item.getDownloadProgress());
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv;
        Button downloadBtn;
        ProgressBar progressBar;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            downloadBtn = itemView.findViewById(R.id.downloadBtn);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    interface OnItemClick {
        void onDownloadBtnClicked(Video video, int position);
    }
}