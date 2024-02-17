/*
 * SimpleCalendarModel.java
 *
 * Created on October 25, 2004, 11:44 AM
 */

package com.ngss.jcalendar;

import java.util.*;

/**
 * @author ngeor
 */
public class SimpleCalendarModel implements CalendarModel {
    private final Hashtable<Date, Vector<Note>> notes = new Hashtable<Date, Vector<Note>>();
    private final Calendar cal = Calendar.getInstance();

    /**
     * Creates a new instance of SimpleCalendarModel.
     */
    public SimpleCalendarModel() {}

    private Date hashDate(Date date) {
        cal.setTime(date);
        DateUtil.setMidnight(cal);
        return cal.getTime();
    }

    private Vector<Note> vectorNotesOfDay(Date hashedDate) {
        Vector<Note> v = notes.get(hashedDate);
        if (v == null) {
            v = new Vector<Note>();
            notes.put(hashedDate, v);
        }

        return v;
    }

    @Override
    public Enumeration<Note> notesOfDay(Date date) {
        return vectorNotesOfDay(hashDate(date)).elements();
    }

    public void addNote(Date date, String note, Object value) {
        Vector<Note> v = vectorNotesOfDay(hashDate(date));
        v.add(new Note(note, value));
    }

    public void clear() {
        notes.clear();
    }
}
