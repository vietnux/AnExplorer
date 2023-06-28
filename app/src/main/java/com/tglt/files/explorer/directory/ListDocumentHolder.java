package com.tglt.files.explorer.directory;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.collection.ArrayMap;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tglt.files.explorer.BaseActivity;
import com.tglt.files.explorer.DocumentsApplication;
import com.tglt.files.explorer.R;
import com.tglt.files.explorer.common.RecyclerFragment.RecyclerItemClickListener.OnItemClickListener;
import com.tglt.files.explorer.cursor.RootCursorWrapper;
import com.tglt.files.explorer.misc.IconColorUtils;
import com.tglt.files.explorer.misc.IconHelper;
import com.tglt.files.explorer.misc.IconUtils;
import com.tglt.files.explorer.misc.MimePredicate;
import com.tglt.files.explorer.misc.ProviderExecutor;
import com.tglt.files.explorer.misc.RootsCache;
import com.tglt.files.explorer.misc.ThumbnailCache;
import com.tglt.files.explorer.misc.Utils;
import com.tglt.files.explorer.model.DocumentInfo;
import com.tglt.files.explorer.model.DocumentsContract;
import com.tglt.files.explorer.model.RootInfo;
import com.tglt.files.explorer.directory.DocumentsAdapter.Environment;

import static com.tglt.files.explorer.BaseActivity.State.ACTION_BROWSE;
import static com.tglt.files.explorer.BaseActivity.State.MODE_GRID;
import static com.tglt.files.explorer.BaseActivity.State.MODE_LIST;
import static com.tglt.files.explorer.DocumentsApplication.isTelevision;
import static com.tglt.files.explorer.DocumentsApplication.isWatch;
import static com.tglt.files.explorer.fragment.DirectoryFragment.TYPE_RECENT_OPEN;
import static com.tglt.files.explorer.model.DocumentInfo.getCursorInt;
import static com.tglt.files.explorer.model.DocumentInfo.getCursorLong;
import static com.tglt.files.explorer.model.DocumentInfo.getCursorString;

public class ListDocumentHolder extends DocumentHolder {

    public ListDocumentHolder(Context context, ViewGroup parent, int layout, OnItemClickListener onItemClickListener,
                              Environment environment) {
        super(context, parent, layout, onItemClickListener, environment);
    }

    public ListDocumentHolder(Context context, ViewGroup parent, OnItemClickListener onItemClickListener,
                              Environment environment) {
        this(context, parent,
                getLayoutId(environment),
                onItemClickListener, environment);
    }

    public static int getLayoutId(Environment environment){
        int layoutId = R.layout.item_doc_list;
        if(environment.isApp()){
            layoutId = environment.getRoot().isAppProcess() ? R.layout.item_doc_process_list : R.layout.item_doc_app_list;
        }
        return layoutId;
    }

    @Override
    public void setData(Cursor cursor, int position) {
        super.setData(cursor, position);
        final Context context = mContext;
        final BaseActivity.State state = mEnv.getDisplayState();
        final RootsCache roots = DocumentsApplication.getRootsCache(context);

        mDoc.updateFromCursor(cursor, getCursorString(cursor, RootCursorWrapper.COLUMN_AUTHORITY));

        if (state.action == ACTION_BROWSE) {
            if (null != iconView) {
                iconView.setOnClickListener(this);
            }
        }

        final boolean enabled = mEnv.isDocumentEnabled(mDoc.mimeType, mDoc.flags);
        setEnabled(enabled);

        mIconHelper.stopLoading(iconThumb);

        iconMime.animate().cancel();
        iconMime.setAlpha(1f);
        iconThumb.animate().cancel();
        iconThumb.setAlpha(0f);

        mIconHelper.load(mDoc, iconThumb, iconMime, iconMimeBackground);

        boolean hasLine1 = false;
        boolean hasLine2 = false;

        final boolean hideTitle = (state.derivedMode == MODE_GRID) && mEnv.hideGridTiles();
        if (!hideTitle) {
            title.setText(mDoc.displayName);
            hasLine1 = true;
        }

        Drawable iconDrawable = null;
        if (mEnv.getType() == TYPE_RECENT_OPEN) {
            final String docRootId = getCursorString(cursor, RootCursorWrapper.COLUMN_ROOT_ID);
            // We've already had to enumerate roots before any results can
            // be shown, so this will never block.
            final RootInfo root = roots.getRootBlocking(mDoc.authority, docRootId);
            if (state.derivedMode == MODE_GRID) {
                iconDrawable = root.loadGridIcon(context);
            } else {
                iconDrawable = root.loadIcon(context);
            }

            if (summary != null) {
                final boolean alwaysShowSummary = mEnv.getContext().getResources().getBoolean(R.bool.always_show_summary);
                if (alwaysShowSummary) {
                    summary.setText(root.getDirectoryString());
                    summary.setVisibility(View.VISIBLE);
                    hasLine2 = true;
                } else {
                    if (iconDrawable != null && roots.isIconUniqueBlocking(root)) {
                        // No summary needed if icon speaks for itself
                        summary.setVisibility(View.INVISIBLE);
                    } else {
                        summary.setText(root.getDirectoryString());
                        summary.setVisibility(View.VISIBLE);
                        // summary.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_END);
                        hasLine2 = true;
                    }
                }
            }
        } else {
            // Directories showing thumbnails in grid mode get a little icon
            // hint to remind user they're a directory.
            if (Utils.isDir(mDoc.mimeType) && state.derivedMode == MODE_GRID) {
                iconDrawable = IconUtils.applyTintAttr(context, R.drawable.ic_root_folder,
                        android.R.attr.textColorPrimaryInverse);
            }

            if (summary != null) {
                if (mDoc.summary != null && !isWatch()) {
                    summary.setText(mDoc.summary);
                    summary.setVisibility(View.VISIBLE);
                    hasLine2 = true;
                } else {
                    summary.setVisibility(View.GONE);
                }
            }
        }

        if (icon1 != null)
            icon1.setVisibility(View.GONE);
        if (icon2 != null)
            icon2.setVisibility(View.GONE);

        if (iconDrawable != null) {
            if (hasLine1) {
                icon1.setVisibility(View.GONE);
                //icon1.setImageDrawable(iconDrawable);
            } else {
                icon2.setVisibility(View.VISIBLE);
                icon2.setImageDrawable(iconDrawable);
            }
        }

        if (mDoc.lastModified == -1) {
            date.setText(null);
        } else {
            date.setText(Utils.formatTime(context, mDoc.lastModified));
            hasLine2 = true;
        }

        final FolderSizeAsyncTask oldSizeTask = (FolderSizeAsyncTask) size.getTag();
        if (oldSizeTask != null) {
            oldSizeTask.preempt();
            size.setTag(null);
        }
        if (state.showSize) {
            size.setVisibility(View.VISIBLE);
            if (Utils.isDir(mDoc.mimeType) || mDoc.size == -1) {
                ArrayMap<Integer, Long> sizes = DocumentsApplication.getFolderSizes();
                size.setText(null);
                if (state.showFolderSize) {
                    long sizeInBytes = sizes.containsKey(position) ? sizes.get(position) : -1;
                    if (sizeInBytes != -1) {
                        size.setText(Formatter.formatFileSize(context, sizeInBytes));
                    } else {
                        final FolderSizeAsyncTask task = new FolderSizeAsyncTask(size, mDoc.path, position);
                        size.setTag(task);
                        ProviderExecutor.forAuthority(mDoc.authority).execute(task);
                    }
                }
            } else {
                size.setText(Formatter.formatFileSize(context, mDoc.size));
                hasLine2 = true;
            }
        } else {
            size.setVisibility(View.GONE);
        }

        if (line1 != null) {
            line1.setVisibility(hasLine1 ? View.VISIBLE : View.GONE);
        }
        if (line2 != null) {
            line2.setVisibility(hasLine2 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        //final float iconAlpha = (state.derivedMode == MODE_LIST && !enabled) ? 0.5f : 1f;
        // Text colors enabled/disabled is handle via a color set.
        final float imgAlpha = enabled ? 1f : DISABLED_ALPHA;
        iconMime.setAlpha(imgAlpha);
        iconThumb.setAlpha(imgAlpha);
    }
}
