package com.alexstyl.specialdates.upcoming.view;

import android.view.View;

import com.alexstyl.specialdates.contact.Contact;
import com.alexstyl.specialdates.date.Date;

public interface OnUpcomingEventClickedListener {
    void onContactClicked(Contact contact, View avatar);

    void onNamedayClicked(Date date);
}
