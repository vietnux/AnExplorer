package com.tglt.files.explorer.directory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tglt.files.explorer.BaseActivity;
import com.tglt.files.explorer.R;
import com.tglt.files.explorer.directory.DocumentsAdapter.Environment;

import static com.tglt.files.explorer.BaseActivity.State.MODE_GRID;
import static com.tglt.files.explorer.BaseActivity.State.MODE_LIST;

public class MessageFooter extends Footer {

    public MessageFooter(Environment environment, int itemViewType, int icon, String message) {
        super(itemViewType);
        mIcon = icon;
        mMessage = message;
        mEnv = environment;
    }
}