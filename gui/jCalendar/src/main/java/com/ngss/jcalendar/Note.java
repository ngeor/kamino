/*
 * Note.java
 *
 * Created on October 25, 2004, 8:02 PM
 */

package com.ngss.jcalendar;

/**
 * @author ngeor
 */
public class Note implements Cloneable, Comparable<Note> {

    /**
     * Holds value of property text.
     */
    private String text;

    private Object value;

    /**
     * Creates a new instance of Note.
     */
    public Note(String text, Object value) {
        this.text = text;
        this.value = value;
    }

    /**
     * Getter for property text.
     *
     * @return Value of property text.
     */
    public String getText() {
        return this.text;
    }

    /**
     * Setter for property text.
     *
     * @param text New value of property text.
     */
    public void setText(String text) {
        this.text = text;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public int compareTo(Note other) {
        return this.text.compareTo(other.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Note that) {
            return that != null && this.text.equals(that.text);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return text;
    }
}
