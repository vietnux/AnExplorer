package com.tglt.files.explorer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

public class LinearLayoutManagerCompat extends LinearLayoutManager {

    public LinearLayoutManagerCompat(Context context) {
        super(context);
    }

    public LinearLayoutManagerCompat(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearLayoutManagerCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
