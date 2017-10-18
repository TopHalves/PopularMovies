package com.example.android.popularmovies;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.utilities.MovieFavoriteUtils;
import com.example.android.popularmovies.utilities.MovieDateUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

/*
 * This RecyclerView.Adapter provides the especially arranged ViewHolders for the MovieDetails
 * RecyclerView. Three cursors are maintained, one for each of: the movie details, the trailers
 * and the reviews. Each section has a specialized ViewHolder for the appropriate view type.
 */
class DetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        TrailerViewHolder.TrailerPlayClickListener,
        TrailerViewHolder.TrailerShareClickListener,
        DetailHeaderViewHolder.FavoriteSelectListener
{

    /* There is a single details header */
    private static final int DETAIL_HEADER_COUNT = 1;

    /* The details header is always first by position */
    private static final int DETAIL_HEADER_POSITION = 0;

    /* View types */
    private static final int VIEW_TYPE_HEADER_DETAILS = 0;
    private static final int VIEW_TYPE_TRAILER = 1;
    private static final int VIEW_TYPE_REVIEW = 2;

    private final Context mContext;

    private Cursor mMovieDetailsCursor;
    private Cursor mTrailersCursor;
    private Cursor mReviewsCursor;

    /* The activity that is the source of the sharing Intent */
    private Activity mIntentLaunchAcitity;

    DetailListAdapter(@NonNull Context context, @NonNull Activity activity) {
        mContext = context;
        mIntentLaunchAcitity = activity;
    }

    /**
     * Find the trailer cursor position from the adapter position.
     * @param adapterPosition the adapter position to calculate from
     * @return the trailer cursor position
     */
    private int calculateTrailerCursorPosition(int adapterPosition) {
        return adapterPosition - DETAIL_HEADER_COUNT;
    }

    /**
     * Find the reviews cursor position from the adapter position.
     * @param adapterPosition the adapter position to calculate from
     * @return the trailer cursor position
     */
    private int calculateReviewCursorPosition(int adapterPosition) {
        return adapterPosition - DETAIL_HEADER_COUNT - mTrailersCursor.getCount();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_HEADER_DETAILS: {
                DetailHeaderViewHolder detailHolder = (DetailHeaderViewHolder) holder;
                configureDetailHeaderView(detailHolder);
                break;
            }
            case VIEW_TYPE_TRAILER: {
                TrailerViewHolder trailerHolder = (TrailerViewHolder) holder;
                configureTrailerView(trailerHolder, position);
                break;
            }
            case VIEW_TYPE_REVIEW: {
                ReviewViewHolder reviewHolder = (ReviewViewHolder) holder;
                configureReviewView(reviewHolder, position);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown view type: " + holder.getItemViewType());
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;

        if (mMovieDetailsCursor != null) count+=DETAIL_HEADER_COUNT;

        if (mTrailersCursor != null) {
            count+=mTrailersCursor.getCount();

            /* Only return review count if trailers are loaded otherwise getItemViewType
             * could attempt to access a null mTrailersCursor */
            if (mReviewsCursor != null) {
                count+=mReviewsCursor.getCount();
            }
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == DETAIL_HEADER_POSITION ) {
            return VIEW_TYPE_HEADER_DETAILS;
        } else if (position <= mTrailersCursor.getCount()) {
            return VIEW_TYPE_TRAILER;
        } else {
            return VIEW_TYPE_REVIEW;
        }
    }

    void swapMovieDetailsCursor(Cursor newCursor) {
        mMovieDetailsCursor = newCursor;
        notifyDataSetChanged();
    }

    void swapTrailerCursor(Cursor newCursor) {
        mTrailersCursor = newCursor;
        notifyDataSetChanged();
    }

    void swapReviewsCursor(Cursor newCursor) {
        mReviewsCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        RecyclerView.ViewHolder holder;

        switch (viewType){
            case VIEW_TYPE_HEADER_DETAILS: {
                view = LayoutInflater.from(mContext).inflate(R.layout.detail_header, parent, false);
                holder = new DetailHeaderViewHolder(view, this);
                break;
            }
            case VIEW_TYPE_TRAILER: {
                view = LayoutInflater.from(mContext).inflate(R.layout.trailer_list_item, parent, false);
                holder = new TrailerViewHolder(view, this, this);
                break;
            }
            case VIEW_TYPE_REVIEW: {
                view = LayoutInflater.from(mContext).inflate(R.layout.review_list_item, parent, false);
                holder = new ReviewViewHolder(view);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
            }
        }

        view.setFocusable(true);

        return holder;
    }

    /**
     * Set the detail header view with the data from the details cursor.
     *
     * @param holder The ViewHolder that is to be populated
     */
    private void configureDetailHeaderView(DetailHeaderViewHolder holder){

        /* Ensure we have a valid cursor with data */
        if (mMovieDetailsCursor == null || mMovieDetailsCursor.getCount() == 0)
            return;

        /* Set the various views up */
        String posterPath = NetworkUtils.convertRelativeImagePathToAbsolute(
                mMovieDetailsCursor.getString(DetailActivity.INDEX_MOVIE_POSTER_PATH)).toString();

        Picasso.with(mContext)
                .load(posterPath)
                .placeholder(R.color.colorPrimaryLight)
                .error(R.color.colorPrimaryLight)
                .into(holder.getPosterImageView());

        holder.getOverviewTextView().setText(mMovieDetailsCursor.getString(DetailActivity.INDEX_MOVIE_COLUMN_OVERVIEW));

        int averageVoteFormatId = R.string.average_vote_format;

        String averageVote =
                String.format(mContext.getString(averageVoteFormatId),
                        mMovieDetailsCursor.getDouble(DetailActivity.INDEX_MOVIE_AVERAGE_RATING));

        holder.getAverageScoreTextView().setText(averageVote);

        holder.getRuntimeTextView()
                .setText( mMovieDetailsCursor.getInt(DetailActivity.INDEX_MOVIE_COLUMN_RUNTIME) +
                " " + mContext.getString(R.string.runtime_minutes));

        int releaseYear = MovieDateUtils.yearFromMilliseconds(
                mMovieDetailsCursor.getLong(DetailActivity.INDEX_MOVIE_RELEASE_DATE));

        holder.getReleaseYearTextView().setText(String.valueOf(releaseYear));

        holder.getFavoriteToggle().setChecked(MovieFavoriteUtils.isFavoriteMovie(mContext,
                mMovieDetailsCursor.getInt(DetailActivity.INDEX_MOVIE_ID)));
    }

    /**
     * Set the trailer header view with the data from the trailer cursor.
     *
     * @param holder the ViewHolder to be populated
     * @param position the adapter position of the ViewHolder
     */
    private void configureTrailerView(TrailerViewHolder holder, int position) {

        mTrailersCursor.moveToPosition(calculateTrailerCursorPosition(position));

        String trailerSiteText = mContext.getString(R.string.trailer_prefix) +
                " " + mTrailersCursor.getString(DetailActivity.INDEX_TRAILER_SITE);

        holder.getTrailerTitle().setText(mTrailersCursor.getString(DetailActivity.INDEX_TRAILER_NAME));
        holder.getTrailerSite().setText(trailerSiteText);

    }

    /**
     * Set the review header view with the data from the review cursor.
     *
     * @param holder the ViewHolder to be populated
     * @param position the adapter position of the ViewHolder
     */
    private void configureReviewView(ReviewViewHolder holder, int position) {

        mReviewsCursor.moveToPosition(calculateReviewCursorPosition(position));

        String reviewAuthorText = mContext.getString(R.string.review_prefix) +
                " " + mReviewsCursor.getString(DetailActivity.INDEX_REVIEW_AUTHOR);

        holder.getReviewAuthor().setText(reviewAuthorText);
        holder.getReviewContent().setText(mReviewsCursor.getString(DetailActivity.INDEX_REVIEW_CONTENT));
    }

    /**
     * Called when a trailer play button is clicked. Launch an intent to play the trailer on youtube.
     *
     * @param position the ViewHolder position of the trailer
     */
    @Override
    public void onPlayClick(int position) {

        mTrailersCursor.moveToPosition(calculateTrailerCursorPosition(position));

        String videoKey = mTrailersCursor.getString(DetailActivity.INDEX_TRAILER_KEY);

        /* We want to launch directly in the youtube app */
        Intent vidAppIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("vnd.youtube:" + videoKey));

        /* If the user doesn't have youtube installed we fall back to the web */
        Intent vidWebIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + videoKey));

        try {
            mContext.startActivity(vidAppIntent);
        } catch ( ActivityNotFoundException e ) {
            mContext.startActivity(vidWebIntent);
        }
    }

    /**
     * Called when a trailer share button is clicked. Launch a share intent.
     *
     * @param position the ViewHolder position of the trailer
     */
    @Override
    public void onShareClick(int position) {

        mTrailersCursor.moveToPosition(calculateTrailerCursorPosition(position));

        String videoKey = mTrailersCursor.getString(DetailActivity.INDEX_TRAILER_KEY);

        Intent shareIntent = ShareCompat.IntentBuilder.from(mIntentLaunchAcitity)
                .setType("text/plain")
                .setText(Uri.parse("http://www.youtube.com/watch?v=" + videoKey).toString())
                .getIntent();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else{
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }

        mContext.startActivity(shareIntent);
    }

    /**
     * Called when the favorite selector is pressed. Make the movie a favorite.
     *
     * @param selected true if selected
     */
    @Override
    public void onFavoriteSelect(boolean selected) {
        int movieId = mMovieDetailsCursor.getInt(DetailActivity.INDEX_MOVIE_ID);

        MovieFavoriteUtils.setFavoriteMovie(mContext, movieId, selected);
    }

}
