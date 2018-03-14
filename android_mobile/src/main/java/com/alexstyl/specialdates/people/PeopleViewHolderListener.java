package com.alexstyl.specialdates.people;

import android.view.View;

import com.alexstyl.specialdates.contact.Contact;

public interface PeopleViewHolderListener {
    void onPersonClicked(Contact contact, View avatar);

    void onFacebookImport();

    void onAddContactClicked();
}
