/*
 * CalendarModel.java
 *
 * Created on October 25, 2004, 11:39 AM
 */

package com.ngss.jcalendar;

import java.util.Date;
import java.util.Enumeration;

/**
 * @author ngeor
 */
public interface CalendarModel {
    Enumeration<Note> notesOfDay(Date date);
}
