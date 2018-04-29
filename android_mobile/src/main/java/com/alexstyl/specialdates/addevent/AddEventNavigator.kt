package com.alexstyl.specialdates.addevent

import android.app.Activity
import com.alexstyl.specialdates.contact.Contact
import com.alexstyl.specialdates.person.PersonActivity

class AddEventNavigator(private val activity: Activity) {
    fun toContactDetails(updatedContact: Contact) {
        val intent = PersonActivity.buildIntentFor(activity, updatedContact)
        activity.startActivity(intent)
    }
}
