/*
 * DateUtil.java
 *
 * Created on October 25, 2004, 7:47 PM
 */

package com.ngss.jcalendar;

import java.util.Calendar;

/**
 * @author ngeor
 */
public final class DateUtil {
    private DateUtil() {}

    /**
     * Sets the given calendar to midnight.
     * @param cal The calendar.
     */
    public static void setMidnight(Calendar cal) {
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
    }
}
