package com.jesussoto.android.popularmovies.moviedetail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jesussoto.android.popularmovies.R;
import com.jesussoto.android.popularmovies.api.WebServiceUtils;
import com.jesussoto.android.popularmovies.api.model.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.ViewHolder> {

    public interface OnVideoTappedListener {
        void onVideoTapped(Video video);
    }

    @NonNull
    private Picasso mPicasso;

    @Nullable
    private List<Video> mVideos;

    @Nullable
    private OnVideoTappedListener mVideoTappedListener;

    public MovieVideosAdapter(@NonNull Picasso picasso) {
        mPicasso = picasso;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = ViewHolder.create(parent, mPicasso);

        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && mVideoTappedListener != null) {
                mVideoTappedListener.onVideoTapped(getItem(position));
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mVideos == null) {
            return;
        }

        holder.bindVideo(mVideos.get(position));
    }

    @Override
    public int getItemCount() {
        return mVideos != null ? mVideos.size() : 0;
    }

    @Nullable
    private Video getItem(int position) {
        return mVideos != null ? mVideos.get(position) : null;
    }

    public void replaceData(@Nullable List<Video> videos) {
        mVideos = videos;
        notifyDataSetChanged();
    }

    public void setOnVideoTappedListener(@Nullable OnVideoTappedListener listener) {
        mVideoTappedListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.video_thumb)
        ImageView mVideoThumbView;

        @BindView(R.id.video_title)
        TextView mVideoTitleView;

        @NonNull
        private Picasso mPicasso;

        ViewHolder(@NonNull View itemView, @NonNull Picasso picasso) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mPicasso = picasso;
        }

        void bindVideo(@NonNull Video video) {
            mPicasso.load(WebServiceUtils.buildYoutubeThumbUri(video.getKey()))
                    .placeholder(R.drawable.image_placeholder)
                    .into(mVideoThumbView);

            mVideoTitleView.setText(video.getName());
        }

        static ViewHolder create(@NonNull ViewGroup parent, @NonNull Picasso picasso) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_item_movie_video, parent, false);

            return new ViewHolder(itemView, picasso);
        }
    }
}
