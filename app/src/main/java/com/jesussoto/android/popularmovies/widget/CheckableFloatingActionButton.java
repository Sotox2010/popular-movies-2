package com.jesussoto.android.popularmovies.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

public class CheckableFloatingActionButton extends FloatingActionButton implements Checkable {

    public interface OnCheckedChangeListener {
        void onCheckedChanged(View checkableView, boolean isChecked);
    }

    public static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

    private boolean mIsChecked;

    @Nullable
    private OnCheckedChangeListener mCheckedChangeListener;

    public CheckableFloatingActionButton(Context context) {
        super(context);
    }

    public CheckableFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mIsChecked == checked) {
            return;
        }

        mIsChecked = checked;
        refreshDrawableState();

        if (mCheckedChangeListener != null) {
            mCheckedChangeListener.onCheckedChanged(this, checked);
        }
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }

        return drawableState;
    }

    public void setOnCheckedChangeListener(@Nullable OnCheckedChangeListener listener) {
        mCheckedChangeListener = listener;
    }
}
