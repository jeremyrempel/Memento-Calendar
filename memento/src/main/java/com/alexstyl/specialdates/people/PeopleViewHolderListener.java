package com.alexstyl.specialdates.people;

import com.alexstyl.specialdates.contact.Contact;

public interface PeopleViewHolderListener {
    void onPersonClicked(Contact contact, int position);

    void onFacebookImport();

    void onAddContactClicked();
}
