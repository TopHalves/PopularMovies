package com.example.android.popularmovies;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

class DetailHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    interface FavoriteSelectListener{
        void onFavoriteSelect(boolean selected);
    }

    private TextView mOverviewTextView;
    private TextView mReleaseYearTextView;
    private TextView mAverageScoreTextView;
    private TextView mRuntimeTextView;
    private ImageView mPosterImageView;
    private ToggleButton mFavoriteToggle;

    private FavoriteSelectListener mFavoriteSelectListener;

    DetailHeaderViewHolder(View itemView, FavoriteSelectListener favoriteSelectListener) {
        super(itemView);

        mOverviewTextView = (TextView) itemView.findViewById(R.id.overview_tv);
        mReleaseYearTextView = (TextView) itemView.findViewById(R.id.release_year_tv);
        mAverageScoreTextView = (TextView) itemView.findViewById(R.id.average_score_tv);
        mPosterImageView = (ImageView) itemView.findViewById(R.id.poster_iv);
        mRuntimeTextView = (TextView) itemView.findViewById(R.id.runtime_tv);
        mFavoriteToggle = (ToggleButton) itemView.findViewById(R.id.favorite_tb);

        mFavoriteSelectListener = favoriteSelectListener;
        mFavoriteToggle.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        mFavoriteSelectListener.onFavoriteSelect(mFavoriteToggle.isChecked());
    }

    TextView getOverviewTextView() {
        return mOverviewTextView;
    }
    TextView getReleaseYearTextView() {
        return mReleaseYearTextView;
    }
    TextView getAverageScoreTextView() {
        return mAverageScoreTextView;
    }
    ImageView getPosterImageView() {
        return mPosterImageView;
    }
    ToggleButton getFavoriteToggle() { return mFavoriteToggle; }
    TextView getRuntimeTextView() { return mRuntimeTextView; }
}
