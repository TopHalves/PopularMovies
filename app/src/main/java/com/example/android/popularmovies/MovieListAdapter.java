package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListAdapterViewHolder> {

    private final Context mContext;

    final private MovieListAdapterOnClickHandler mClickHandler;

    interface MovieListAdapterOnClickHandler {
        void onClick(long movieId);
    }

    private Cursor mCursor;

    MovieListAdapter(@NonNull Context context, MovieListAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;

    }

    @Override
    public void onBindViewHolder(MovieListAdapterViewHolder holder, int position) {

        mCursor.moveToPosition(position);

        String movieUrl = mCursor.getString(MainActivity.INDEX_MOVIE_POSTER_PATH);
        String movieTitle = mCursor.getString(MainActivity.INDEX_MOVIE_TITLE);

        // Get the actual URL of the poster image
        URL imageURL = NetworkUtils.convertRelativeImagePathToAbsolute(
                movieUrl);

        Picasso.with(mContext)
                .load(imageURL.toString())
                .placeholder(R.color.colorPrimaryLight)
                .error(R.color.colorPrimaryLight)
                .into(holder.mImagePoster);

        // Provide the title for accessible navigation of the main activity
        holder.mImagePoster.setContentDescription(movieTitle);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public MovieListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false);

        view.setFocusable(true);

        return new MovieListAdapterViewHolder(view);
    }

    class MovieListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView mImagePoster;

        MovieListAdapterViewHolder(View view) {
            super(view);

            mImagePoster = (ImageView) view.findViewById(R.id.movie_poster_iv);

            mImagePoster.setAdjustViewBounds(true);
            mImagePoster.setScaleType(ImageView.ScaleType.FIT_XY);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long movieId = mCursor.getLong(MainActivity.INDEX_MOVIE_ID);

            mClickHandler.onClick(movieId);
        }
    }
}
