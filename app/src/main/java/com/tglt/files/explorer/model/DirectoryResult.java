package com.tglt.files.explorer.model;

import android.content.ContentProviderClient;
import android.database.Cursor;

import java.io.Closeable;

import com.tglt.files.explorer.libcore.io.IoUtils;
import com.tglt.files.explorer.misc.ContentProviderClientCompat;

import static com.tglt.files.explorer.BaseActivity.State.MODE_UNKNOWN;
import static com.tglt.files.explorer.BaseActivity.State.SORT_ORDER_UNKNOWN;

public class DirectoryResult implements Closeable {
	public ContentProviderClient client;
    public Cursor cursor;
    public Exception exception;

    public int mode = MODE_UNKNOWN;
    public int sortOrder = SORT_ORDER_UNKNOWN;

    @Override
    public void close() {
        IoUtils.closeQuietly(cursor);
        ContentProviderClientCompat.releaseQuietly(client);
        cursor = null;
        client = null;
    }
}