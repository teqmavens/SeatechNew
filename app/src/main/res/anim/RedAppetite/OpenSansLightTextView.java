package com.vadevelopment.RedAppetite;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by vibrantappz on 5/8/2017.
 */

public class OpenSansLightTextView extends TextView {

    public OpenSansLightTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OpenSansLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OpenSansLightTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/OpenSans-Light.ttf");
        setTypeface(tf);
    }

}