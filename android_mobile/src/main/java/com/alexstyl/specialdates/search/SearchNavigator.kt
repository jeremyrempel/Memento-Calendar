package com.alexstyl.specialdates.search

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.support.v4.view.ViewCompat
import android.view.View
import com.alexstyl.specialdates.analytics.Analytics
import com.alexstyl.specialdates.analytics.Screen
import com.alexstyl.specialdates.contact.Contact
import com.alexstyl.specialdates.date.Date
import com.alexstyl.specialdates.events.namedays.activity.NamedayActivity
import com.alexstyl.specialdates.permissions.ContactPermissionActivity
import com.alexstyl.specialdates.person.PersonActivity

internal class SearchNavigator(private val analytics: Analytics) {

    fun toContactDetails(contact: Contact, activity: Activity, avatarView: View) {
        val intent = PersonActivity.buildIntentFor(activity, contact)
        val options = ActivityOptions.makeSceneTransitionAnimation(
                activity, avatarView, ViewCompat.getTransitionName(avatarView))
        activity.startActivity(intent, options.toBundle())
        analytics.trackContactDetailsViewed(contact)
    }

    fun toNamedays(date: Date, activity: Activity) {
        val currentYearDate = Date.on(date.dayOfMonth, date.month, Date.CURRENT_YEAR)
        val intent = NamedayActivity.getStartIntent(activity, currentYearDate)
        activity.startActivity(intent)
    }

    fun toContactPermission(activity: Activity) {
        val intent = Intent(activity, ContactPermissionActivity::class.java)
        analytics.trackScreen(Screen.CONTACT_PERMISSION_REQUESTED)
        activity.startActivity(intent)
    }
}
