package com.alexstyl.specialdates.facebook;

import com.alexstyl.specialdates.ErrorTracker;
import com.alexstyl.specialdates.date.ContactEvent;
import com.alexstyl.specialdates.date.DateParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;

import org.apache.log4j.BasicConfigurator;

class FacebookBirthdaysImporter {

    private final CalendarLoader fileLoader;
    private final FacebookContactFactory factory;

    FacebookBirthdaysImporter(CalendarLoader fileLoader, FacebookContactFactory factory) {
        this.fileLoader = fileLoader;
        this.factory = factory;
    }

    List<ContactEvent> fetchFriends() {
        BasicConfigurator.configure();

        Calendar calendar = fileLoader.loadCalendar();
        List<ContactEvent> contacts = new ArrayList<>();
        for (CalendarComponent component : calendar.getComponents()) {
            System.out.println("Component [" + component.getName() + "]");
            // new contact
            Map<String, String> map = new HashMap<>();
            for (Property property : component.getProperties()) {
                System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
                map.put(property.getName(), property.getValue());
            }
            try {
                ContactEvent contactFrom = factory.createContactFrom(map);
                contacts.add(contactFrom);
            } catch (DateParseException e) {
                ErrorTracker.track(e);
            }
        }
        return contacts;
    }

}
