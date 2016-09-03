package com.alexstyl.specialdates.events.namedays.calendar;

import com.alexstyl.specialdates.date.DayDate;
import com.alexstyl.specialdates.events.namedays.NameCelebrations;
import com.alexstyl.specialdates.events.namedays.NamesInADate;

import java.util.ArrayList;

public enum NoSpecialNamedaysStrategy implements SpecialNamedaysStrategy {

    INSTANCE;

    @Override
    public NamesInADate getNamedayByDate(DayDate date) {
        return new NamesInADate(date);
    }

    @Override
    public ArrayList<String> getAllNames() {
        return new ArrayList<>(0);
    }

    @Override
    public NameCelebrations getNamedaysFor(String name, int year) {
        return new NameCelebrations(name);
    }
}
