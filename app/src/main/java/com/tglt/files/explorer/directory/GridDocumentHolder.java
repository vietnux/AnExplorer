package com.tglt.files.explorer.directory;

import android.content.Context;
import android.view.ViewGroup;

import com.tglt.files.explorer.R;
import com.tglt.files.explorer.common.RecyclerFragment.RecyclerItemClickListener.OnItemClickListener;
import com.tglt.files.explorer.directory.DocumentsAdapter.Environment;

public class GridDocumentHolder extends ListDocumentHolder {

    public GridDocumentHolder(Context context, ViewGroup parent,
                              OnItemClickListener onItemClickListener, Environment environment) {
        super(context, parent, R.layout.item_doc_grid, onItemClickListener, environment);
    }

}
