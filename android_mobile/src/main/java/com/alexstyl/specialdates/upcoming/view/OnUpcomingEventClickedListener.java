package com.alexstyl.specialdates.upcoming.view;

import android.widget.ImageView;

import com.alexstyl.specialdates.contact.Contact;
import com.alexstyl.specialdates.date.Date;

public interface OnUpcomingEventClickedListener {
    void onContactClicked(Contact contact, ImageView avatar);

    void onNamedayClicked(Date date);
}
