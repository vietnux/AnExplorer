package com.tglt.files.explorer.directory;

import android.content.Context;
import android.view.ViewGroup;

import com.tglt.files.explorer.R;

import static com.tglt.files.explorer.BaseActivity.State.MODE_GRID;

public class LoadingHolder extends BaseHolder {

    public LoadingHolder(DocumentsAdapter.Environment environment, Context context, ViewGroup parent) {
        super(context, parent, getLayoutId(environment));
    }

    public static int getLayoutId(DocumentsAdapter.Environment environment){
        int layoutId = R.layout.item_loading_list;
        if(environment.getDisplayState().derivedMode == MODE_GRID){
            layoutId = R.layout.item_loading_grid;
        }
        return layoutId;
    }
}
