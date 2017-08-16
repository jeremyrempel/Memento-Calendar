package com.alexstyl.specialdates.search;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.widget.ImageView;

import com.alexstyl.specialdates.R;
import com.alexstyl.specialdates.analytics.Analytics;
import com.alexstyl.specialdates.contact.Contact;
import com.alexstyl.specialdates.person.PersonActivity;

class SearchNavigator {
    private final Activity activity;
    private final Analytics analytics;

    SearchNavigator(Activity activity, Analytics analytics) {
        this.activity = activity;
        this.analytics = analytics;
    }

    void toContactDetails(Contact contact, ImageView avatar) {
        Intent intent = PersonActivity.buildIntentFor(activity, contact);

        String transitionName = avatar.getResources().getString(R.string.transition_person_avatar);
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, avatar, transitionName);
        activity.startActivity(intent, transitionActivityOptions.toBundle());
        analytics.trackContactDetailsViewed(contact);
    }
}
