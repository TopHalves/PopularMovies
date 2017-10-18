package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class ReviewViewHolder extends RecyclerView.ViewHolder {

    TextView mReviewAuthor;
    TextView mReviewContent;

    ReviewViewHolder(View itemView) {
        super(itemView);

        mReviewAuthor = (TextView) itemView.findViewById(R.id.review_author_tv);
        mReviewContent = (TextView) itemView.findViewById(R.id.review_content_tv);
    }

    TextView getReviewAuthor() {
        return mReviewAuthor;
    }
    TextView getReviewContent() {
        return mReviewContent;
    }
}
