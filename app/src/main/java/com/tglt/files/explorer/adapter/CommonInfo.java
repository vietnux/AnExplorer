package com.tglt.files.explorer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.File;

import androidx.core.content.ContextCompat;
import com.tglt.files.explorer.misc.IconUtils;
import com.tglt.files.explorer.model.DocumentInfo;
import com.tglt.files.explorer.model.RootInfo;

import static com.tglt.files.explorer.adapter.HomeAdapter.TYPE_RECENT;

public class CommonInfo {

    public int type;
    public DocumentInfo documentInfo;
    public RootInfo rootInfo;

    public static CommonInfo from(RootInfo rootInfo, int type){
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.type = type;
        commonInfo.rootInfo = rootInfo;
        return commonInfo;
    }

    public static CommonInfo from(DocumentInfo documentInfo, int type){
        CommonInfo commonInfo = new CommonInfo();
        commonInfo.type = type;
        commonInfo.documentInfo = documentInfo;
        return commonInfo;
    }

    public static CommonInfo from(Cursor cursor){
        DocumentInfo documentInfo = DocumentInfo.fromDirectoryCursor(cursor);
        CommonInfo commonInfo = from(documentInfo, TYPE_RECENT);
        return commonInfo;
    }
}
