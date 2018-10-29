package com.jesussoto.android.popularmovies.moviedetail;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jesussoto.android.popularmovies.R;
import com.jesussoto.android.popularmovies.api.WebServiceUtils;
import com.jesussoto.android.popularmovies.api.model.UserReview;
import com.jesussoto.android.popularmovies.api.model.Video;
import com.jesussoto.android.popularmovies.db.entity.Movie;
import com.jesussoto.android.popularmovies.widget.AlwaysEnterToolbarScrollListener;
import com.jesussoto.android.popularmovies.widget.CheckableFloatingActionButton;
import com.jesussoto.android.popularmovies.widget.SynchronizedScrollView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class MovieDetailActivity extends AppCompatActivity
        implements SynchronizedScrollView.OnScrollListener {

    public static final String EXTRA_MOVIE = "extra_movie";

    public static final long ANIMATION_DURATION = 200L;

    public static void start(FragmentActivity launching, Movie movie) {
        Intent movieDetailIntent = new Intent(launching, MovieDetailActivity.class);
        movieDetailIntent.putExtra(EXTRA_MOVIE, movie);
        launching.startActivity(movieDetailIntent);
    }

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.scroll_view)
    SynchronizedScrollView mScrollView;

    @BindView(R.id.title_container)
    ViewGroup mTitleContainer;

    @BindView(R.id.title)
    TextView mTitleView;

    @BindView(R.id.original_title)
    TextView mOriginalTitleView;

    @BindView(R.id.backdrop)
    ImageView mBackdropView;

    @BindView(R.id.poster)
    ImageView mPosterView;

    @BindView(R.id.overview_label)
    TextView mOverviewLabelView;

    @BindView(R.id.videos_label)
    TextView mVideosLabelView;

    @BindView(R.id.reviews_label)
    TextView mReviewsLabelView;

    @BindView(R.id.overview)
    TextView mOverviewView;

    @BindView(R.id.rating)
    TextView mRatingView;

    @BindView(R.id.release_date)
    TextView mReleaseDateView;

    @BindView(R.id.videos_progress_bar)
    ProgressBar mVideosProgressBar;

    @BindView(R.id.recycler_view_videos)
    RecyclerView mVideosRecyclerView;

    @BindView(R.id.empty_videos)
    ViewGroup mVideosEmptyView;

    @BindView(R.id.reviews_progress_bar)
    ProgressBar mReviewsProgressBar;

    @BindView(R.id.reviews)
    LinearLayout mReviewsContainer;

    @BindView(R.id.empty_reviews)
    ViewGroup mReviewsEmptyView;

    @BindView(R.id.fab_favorite)
    CheckableFloatingActionButton mFavoriteFab;

    @Inject
    Picasso mPicasso;

    @Inject
    ViewModelProvider.Factory mFactory;

    private MovieDetailViewModel mViewModel;

    @ColorInt
    private int mToolbarColor = Color.TRANSPARENT;

    private ObjectAnimator mToolbarColorAnimator;

    private boolean mToolbarColored = false;

    private Movie mMovie;

    private MovieVideosAdapter mVideosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        mMovie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        if (mMovie == null) {
            Toast.makeText(this, "No movie provided, exiting...", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mBackdropView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            mTitleView.post(() -> {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTitleContainer
                        .getLayoutParams();
                lp.topMargin = mBackdropView.getHeight();
                mTitleContainer.setLayoutParams(lp);

                mFavoriteFab.setTranslationY(mBackdropView.getHeight()
                        + mTitleContainer.getHeight()
                        - mFavoriteFab.getHeight() / 2);
            });
        });

        setupToolbar();
        setupVideosRecyclerView();
        setupEmptyViews();

        mScrollView.addOnScrollListener(new AlwaysEnterToolbarScrollListener(mToolbar));
        mScrollView.addOnScrollListener(this);

        bindViewModel();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mScrollView.post(() -> {
            List<SynchronizedScrollView.OnScrollListener> listeners =
                    mScrollView.getOnScrollListeners();

            // This is just for restoring the state of the views getting animated by scroll changes.
            if (listeners != null) {
                for (SynchronizedScrollView.OnScrollListener listener : listeners) {
                    listener.onScrollChanged(0, 0, mScrollView.getScrollX(), mScrollView.getScrollY());
                }
            }
        });
    }

    /**
     * Bind to the view model to react to data changes.
     */
    private void bindViewModel() {
        mViewModel = ViewModelProviders.of(this, mFactory).get(MovieDetailViewModel.class);
        mViewModel.setupWithMovie(mMovie);

        mViewModel.getMovie().observe(this, this::bindMovie);

        mViewModel.getVideos().observe(this, this::updateVideosView);

        mViewModel.getUserReviews().observe(this, this::updateReviewsView);

        mViewModel.getIsFavorite().observe(this, this::updateFavorite);

        mViewModel.getSnackbarText().observe(this, this::showSnackbar);
    }

    /**
     * Setup toolbar properties.
     */
    private void setupToolbar() {
        if (mToolbarColor == Color.TRANSPARENT) {
            mToolbarColor = ContextCompat.getColor(this, R.color.colorPrimary);
        }

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.post(() -> mToolbar.setTitle(null));
        }

        // Init the color animator for the toolbar.
        mToolbarColorAnimator = ObjectAnimator.ofArgb(mToolbar, "backgroundColor", 0);
        mToolbarColorAnimator.setInterpolator(new LinearInterpolator());
        mToolbarColorAnimator.setAutoCancel(true);

        mFavoriteFab.setOnClickListener(__ -> mViewModel.toggleFavorite());
    }

    /**
     * Setup videos recycler view and adapter.
     */
    private void setupVideosRecyclerView() {
        mVideosAdapter = new MovieVideosAdapter(mPicasso);
        mVideosAdapter.setOnVideoTappedListener(this::openYoutubeVideo);

        mVideosRecyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        mVideosRecyclerView.setAdapter(mVideosAdapter);
    }

    /**
     * Assign click handlers for the empty views.
     */
    private void setupEmptyViews() {
        mVideosEmptyView.findViewById(R.id.empty_button)
                .setOnClickListener(__ -> mViewModel.retryVideos());

        mReviewsEmptyView.findViewById(R.id.empty_button)
                .setOnClickListener(__ -> mViewModel.retryReviews());
    }

    /**
     * Update view to represent the movie details.
     *
     * @param movie {@link Movie} to update the view.
     */
    private void bindMovie(@NonNull Movie movie) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM YYYY", Locale.getDefault());

        if (mMovie.getPosterPath() != null) {
            mPicasso.load(WebServiceUtils.buildMoviePosterUri(mMovie.getPosterPath()))
                    .placeholder(R.drawable.poster_image_placeholder)
                    .into(mPosterTarget);
        }

        if (mMovie.getBackdropPath() != null) {
            mPicasso.load(WebServiceUtils.buildMovieBackdropUri(mMovie.getBackdropPath()))
                    .placeholder(R.drawable.image_placeholder)
                    .into(mBackdropView);
        }

        mTitleView.setText(movie.getTitle());
        mOriginalTitleView.setText(movie.getOriginalTitle());
        mOverviewView.setText(movie.getOverview());
        mRatingView.setText(getString(R.string.rating_format, movie.getVoteAverage()));
        mReleaseDateView.setText(dateFormatter.format(movie.getReleaseDate()));
    }

    /**
     * Update videos recycler view.
     *
     * @param uiModel UI Model representing the videos state.
     */
    private void updateVideosView(MovieVideosUiModel uiModel) {
        int progressVisibility = uiModel.isProgressVisible() ? View.VISIBLE : View.GONE;
        int emptyVisibility = uiModel.isEmptyVisible() ? View.VISIBLE : View.GONE;
        int retryVisibility = uiModel.isRetryVisible() ? View.VISIBLE : View.GONE;

        mVideosAdapter.replaceData(uiModel.getVideos());
        mVideosProgressBar.setVisibility(progressVisibility);

        mVideosEmptyView.setVisibility(emptyVisibility);
        mVideosEmptyView.findViewById(R.id.empty_button).setVisibility(retryVisibility);
        if (uiModel.getEmptyMessageResId() != null) {
            ((TextView) mVideosEmptyView.findViewById(R.id.empty_msg)).setText(
                    getString(uiModel.getEmptyMessageResId()));
        }
    }

    /**
     * Update movie reviews..
     *
     * @param uiModel UI Model representing the reviews state.
     */
    private void updateReviewsView(MovieReviewsUiModel uiModel) {
        int progressVisibility = uiModel.isProgressVisible() ? View.VISIBLE : View.GONE;
        int emptyVisibility = uiModel.isEmptyVisible() ? View.VISIBLE : View.GONE;
        int retryVisibility = uiModel.isRetryVisible() ? View.VISIBLE : View.GONE;

        populateReviews(uiModel.getReviews());
        mReviewsProgressBar.setVisibility(progressVisibility);

        mReviewsEmptyView.setVisibility(emptyVisibility);
        mReviewsEmptyView.findViewById(R.id.empty_button).setVisibility(retryVisibility);
        if (uiModel.getEmptyMessageResId() != null) {
            ((TextView) mReviewsEmptyView.findViewById(R.id.empty_msg)).setText(
                    getString(uiModel.getEmptyMessageResId()));
        }
    }

    /**
     * Update movie favorite state.
     *
     * @param isFavorite whether the underlying movie is favorite or not.
     */
    private void updateFavorite(boolean isFavorite) {
        mFavoriteFab.setChecked(isFavorite);
    }

    /**
     * Shows a snackbar with the given message,
     *
     * @param messageResId the resource id of the message to show.
     */
    private void showSnackbar(@Nullable @StringRes Integer messageResId) {
        if (messageResId == null) {
            return;
        }

        Snackbar.make(mScrollView, messageResId, Snackbar.LENGTH_SHORT).show();
    }

    private void populateReviews(@Nullable List<UserReview> reviews) {
        mReviewsContainer.removeAllViews();
        if (reviews != null && !reviews.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(this);
            for (UserReview review : reviews) {
                ReviewItemViewHolder holder = ReviewItemViewHolder.create(mReviewsContainer, inflater);
                holder.bindReview(review);
                mReviewsContainer.addView(holder.getItemView());
            }
        }
    }

    /**
     * Callback invoked when the poster bitmap palette has been processed.
     * Here we tweak the UI based on the palette for a more immersive experience.
     *
     * @param palette The processed {@link Palette}
     */
    private void onPaletteGenerated(Palette palette) {
        Palette.Swatch darkMuted = palette.getDarkMutedSwatch();
        if (darkMuted != null) {
            mTitleContainer.setBackgroundColor(darkMuted.getRgb());
            mToolbarColor = darkMuted.getRgb();
            if (mToolbarColored) {
                mToolbar.post(() -> mToolbar.setBackgroundColor(mToolbarColor));
            }
        }

        Palette.Swatch lightVibrant = palette.getLightVibrantSwatch();
        if (lightVibrant != null) {
            mOverviewLabelView.setTextColor(lightVibrant.getRgb());
            mVideosLabelView.setTextColor(lightVibrant.getRgb());
            mReviewsLabelView.setTextColor(lightVibrant.getRgb());
        }

        Palette.Swatch darkVibrant = palette.getVibrantSwatch();
        if (darkVibrant  != null) {
            ColorStateList list = new ColorStateList(new int[][]{{}}, new int[]{darkVibrant.getRgb()});
            mFavoriteFab.setSupportBackgroundTintList(list);
        }
    }

    @Override
    public void onScrollChanged(int left, int top, int deltaX, int deltaY) {
        // Set to half to create a nice parallax effect!
        mBackdropView.setTranslationY(top / 2);

        float computedHeight = mBackdropView.getHeight()
                - mToolbar.getHeight()
                - mToolbar.getTranslationY();

        boolean shouldColorize = top >= computedHeight;
        animateToolbar(shouldColorize);
    }

    /**
     * Animates toolbar show or hide.
     *
     * @param show whether to show the toolbar or hide it.
     */
    private void animateToolbar(boolean show) {
        if (show != mToolbarColored) {
            int startColor = show ? Color.TRANSPARENT : mToolbarColor;
            int endColor = show ? mToolbarColor : Color.TRANSPARENT;
            long duration = show ? 0L : ANIMATION_DURATION;

            mToolbarColorAnimator.setIntValues(startColor, endColor);
            mToolbarColorAnimator.setDuration(duration);
            mToolbarColorAnimator.start();
            mToolbarColored = show;
        }
    }

    /**
     * Open YouTube video in the YouTube App or in the browser as a fallback.
     *
     * @param video the {@link Video} to open on YouTube app.
     */
    private void openYoutubeVideo(Video video) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("vnd.youtube:" + video.getKey()));

        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v="+ video.getKey()));

        if (appIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(appIntent);
            return;
        }

        if (browserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(browserIntent);
            return;
        }

        Toast.makeText(this, "There is no app to open the video", Toast.LENGTH_SHORT).show();
    }

    // Custom Picasso target to be able to receive the poster bitmap for further color processing
    // using the Palette API.
    private Target mPosterTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mPosterView.setImageBitmap(bitmap);
            Palette.from(bitmap).generate(MovieDetailActivity.this::onPaletteGenerated);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            // Intentionally ignored.
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            // Intentionally ignored.
        }
    };
}
