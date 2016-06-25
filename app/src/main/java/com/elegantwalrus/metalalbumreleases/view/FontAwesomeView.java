package com.elegantwalrus.metalalbumreleases.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Chris on 16.05.2016.W
 */
public class FontAwesomeView extends TextView {

    public FontAwesomeView(Context context) {
        super(context);
        setTypeFace(context);
    }

    public FontAwesomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeFace(context);
    }

    public FontAwesomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeFace(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FontAwesomeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setTypeFace(context);
    }

    private void setTypeFace(Context context) {
        Typeface fontAwesome = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
        setTypeface(fontAwesome);
    }
}
