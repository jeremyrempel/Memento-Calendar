package com.alexstyl.specialdates.theming

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.WindowManager
import com.alexstyl.android.Version

class Themer(private val preferences: ThemingPreferences, private val attributeExtractor: AttributeExtractor) {

    fun applyThemeTo(activity: Activity) {
        val theme = preferences.selectedTheme
        activity.setTheme(theme.androidTheme())

        if (Version.hasLollipop()) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            val primaryDark = attributeExtractor.extractPrimaryColorFrom(activity)
            window.statusBarColor = primaryDark
        }

    }

    fun tintToolbarIcon(drawableResId: Int, activity: Activity): Drawable {
        val wrappedDrawable = DrawableCompat.wrap(ResourcesCompat.getDrawable(activity.resources, drawableResId, null)!!)
        val color = attributeExtractor.extractToolbarIconColors(activity)
        DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(color))
        return wrappedDrawable
    }
}
