package com.tglt.files.explorer.directory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tglt.files.explorer.BaseActivity;
import com.tglt.files.explorer.R;
import com.tglt.files.explorer.directory.DocumentsAdapter.Environment;

import static com.tglt.files.explorer.BaseActivity.State.MODE_GRID;
import static com.tglt.files.explorer.BaseActivity.State.MODE_LIST;

public class LoadingFooter extends Footer {

    public LoadingFooter(Environment environment, int type) {
        super(type);
        mEnv = environment;
        mIcon = 0;
        mMessage = "";
    }
}