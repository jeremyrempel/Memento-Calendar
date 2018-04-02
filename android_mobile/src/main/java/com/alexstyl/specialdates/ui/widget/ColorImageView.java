package com.alexstyl.specialdates.ui.widget;

import android.content.Context;
import android.support.annotation.Size;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ColorImageView extends android.support.v7.widget.AppCompatImageView {

    public ColorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Returns the currently selected background variant of the view
     */
    public void setCircleColorVariant(@Size(min = 1, max = 5) int i) {
    }

    public void setText(String text) {
    }

    public void setLetter(String letter) {
    }

    public void setLetter(String word, boolean firstChar) {
    }

    public ImageView getImageView() {
        return this;
    }
}
