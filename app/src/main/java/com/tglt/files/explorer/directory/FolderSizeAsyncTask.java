package com.tglt.files.explorer.directory;

import android.net.Uri;
import androidx.core.os.CancellationSignal;
import androidx.core.os.OperationCanceledException;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import java.io.File;

import com.tglt.files.explorer.DocumentsApplication;
import com.tglt.files.explorer.misc.AsyncTask;
import com.tglt.files.explorer.misc.CrashReportingManager;
import com.tglt.files.explorer.misc.ProviderExecutor;
import com.tglt.files.explorer.misc.Utils;

import static com.tglt.files.explorer.BaseActivity.TAG;

public class FolderSizeAsyncTask extends AsyncTask<Uri, Void, Long> implements ProviderExecutor.Preemptable {
		private final TextView mSizeView;
		private final CancellationSignal mSignal;
		private final String mPath;
		private final int mPosition;

		public FolderSizeAsyncTask(TextView sizeView, String path, int position) {
			mSizeView = sizeView;
			mSignal = new CancellationSignal();
			mPath = path;
			mPosition = position;
		}

		@Override
		public void preempt() {
			cancel(false);
			mSignal.cancel();
		}

		@Override
		protected Long doInBackground(Uri... params) {
			if (isCancelled())
				return null;

			Long result = null;
			try {
				if (!TextUtils.isEmpty(mPath)) {
					File dir = new File(mPath);
					result = Utils.getDirectorySize(dir);
				}
			} catch (Exception e) {
				if (!(e instanceof OperationCanceledException)) {
					Log.w(TAG, "Failed to calculate size for " + mPath + ": " + e);
				}
				CrashReportingManager.logException(e);
			}
			return result;
		}

		@Override
		protected void onPostExecute(Long result) {
            if (isCancelled()) {
                result = null;
            }
			if (mSizeView.getTag() == this && result != null) {
				mSizeView.setTag(null);
				String size = Formatter.formatFileSize(mSizeView.getContext(), result);
				mSizeView.setText(size);
				DocumentsApplication.getFolderSizes().put(mPosition, result);
			}
		}
	}