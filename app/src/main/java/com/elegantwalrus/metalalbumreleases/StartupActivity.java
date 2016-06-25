package com.elegantwalrus.metalalbumreleases;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_startup)
public class StartupActivity extends AppCompatActivity {

    public Context mContext;

    @AfterViews
    void onContent() {
        mContext = this;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                ReleaseListActivity_.intent(mContext).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 1500);


    }
}
