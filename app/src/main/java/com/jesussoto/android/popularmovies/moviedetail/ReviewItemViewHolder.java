package com.jesussoto.android.popularmovies.moviedetail;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jesussoto.android.popularmovies.R;
import com.jesussoto.android.popularmovies.api.model.UserReview;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Wrapper class around a review list item view to abstract its methods.
 */
public class ReviewItemViewHolder {
    @NonNull
    private View mItemView;

    @BindView(R.id.review_author)
    TextView mAuthorView;

    @BindView(R.id.review_content)
    TextView mReviewContentView;

    public ReviewItemViewHolder(View itemView) {
        ButterKnife.bind(this, itemView);
        mItemView = itemView;
    }

    public void bindReview(UserReview review) {
        mAuthorView.setText(review.getAuthor());
        mReviewContentView.setText(review.getContent());
    }

    public View getItemView() {
        return mItemView;
    }

    public static ReviewItemViewHolder create(@NonNull ViewGroup parent, @NonNull LayoutInflater inflater) {
        View itemIView = inflater.inflate(R.layout.list_item_movie_review, parent, false);
        return new ReviewItemViewHolder(itemIView);
    }
}
