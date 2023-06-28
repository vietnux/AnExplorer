package com.tglt.files.explorer.ui;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
//
//import com.tglt.files.explorer.AppFlavour;
import com.tglt.files.explorer.DocumentsApplication;
import com.tglt.files.explorer.R;
import com.tglt.files.explorer.misc.CrashReportingManager;
import com.tglt.files.explorer.misc.Utils;

import static com.tglt.files.explorer.DocumentsApplication.isTelevision;

public class ProWrapper extends FrameLayout {

    public ProWrapper(Context context) {
        super(context);
        init(context);
    }

    public ProWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
//        View view = LayoutInflater.from(context).inflate(R.layout.pro_wrapper, this, true);
//        view.findViewById(R.id.action_layout).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DocumentsApplication.openPurchaseActivity(getContext());
//            }
//        });
//        setVisibility(AppFlavour.isPurchased() ? GONE : VISIBLE);
    }

}
