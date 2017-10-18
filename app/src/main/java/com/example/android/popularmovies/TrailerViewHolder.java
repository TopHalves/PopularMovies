package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

class TrailerViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener{

    interface TrailerPlayClickListener {
        void onPlayClick(int position);
    }

    interface TrailerShareClickListener {
        void onShareClick(int position);
    }

    private TextView mTrailerTitle;
    private TextView mTrailerSite;

    private TrailerPlayClickListener mTrailerPlayClickListener;
    private TrailerShareClickListener mTrailerShareClickListener;

    TrailerViewHolder(View itemView, TrailerShareClickListener trailerShareClickListener,
                             TrailerPlayClickListener trailerPlayClickListener) {
        super(itemView);

        ImageButton mTrailerPlayImageButton = (ImageButton) itemView.findViewById(R.id.play_ib);
        ImageButton mTrailerShareImageButton = (ImageButton) itemView.findViewById(R.id.share_ib);

        mTrailerTitle = (TextView) itemView.findViewById(R.id.trailer_title_tv);
        mTrailerSite = (TextView) itemView.findViewById(R.id.trailer_site_tv);

        mTrailerShareClickListener = trailerShareClickListener;
        mTrailerPlayClickListener = trailerPlayClickListener;

        mTrailerPlayImageButton.setOnClickListener(this);
        mTrailerShareImageButton.setOnClickListener(this);
    }

    TextView getTrailerTitle() {
        return mTrailerTitle;
    }
    TextView getTrailerSite() {
        return mTrailerSite;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.play_ib: {
                mTrailerPlayClickListener.onPlayClick(getAdapterPosition());
                break;
            }
            case R.id.share_ib: {
                mTrailerShareClickListener.onShareClick(getAdapterPosition());
                break;
            }
        }
    }
}
