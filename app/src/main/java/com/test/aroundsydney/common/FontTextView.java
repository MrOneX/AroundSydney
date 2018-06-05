package com.test.aroundsydney.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.test.aroundsydney.R;

public class FontTextView extends AppCompatTextView {

    public FontTextView(Context context) {
        this(context, null);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FontTextView);
        if (ta != null) {
            String fontAsset = ta.getString(R.styleable.FontTextView_typefaceAsset);
            if (!TextUtils.isEmpty(fontAsset)) {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), fontAsset);
                setTypeface(tf);
            }
            ta.recycle();
        }
    }
}
