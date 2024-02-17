package com.ngss.jcalendar;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

/*
 * JCalendar.java
 *
 * Created on October 25, 2004, 10:38 AM
 */

/**
 * @author ngeor
 */
@SuppressWarnings("serial")
public class JCalendar extends JComponent implements MouseInputListener {

    private static final int DAYS_PER_WEEK = 7;

    /**
     * Holds value of property date.
     */
    private Date date = new Date();

    /**
     * Holds value of property model.
     */
    private CalendarModel model;

    /**
     * Holds value of property todayBackgroundColor.
     */
    private Color todayBackgroundColor = new Color(255, 255, 224);

    /**
     * Holds value of property selectedBackgroundColor.
     */
    private Color selectedBackgroundColor = new Color(192, 192, 255);

    /**
     * Creates a new instance of JCalendar.
     */
    public JCalendar() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    private Calendar firstDayOfMonthCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        DateUtil.setMidnight(cal);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal;
    }

    private int getRowCount() {
        Calendar cal = firstDayOfMonthCalendar();
        Cell temp = firstDayOfMonthPoint();
        int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i < lastDayOfMonth; i++) {
            temp.inc();
        }

        return temp.getRow() + 1;
    }

    /**
     * Gets the date at the given position.
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @return The date at the given position.
     */
    public Date dateAtPos(int x, int y) {
        int width = this.getWidth();
        int height = this.getHeight();
        int columnWidth = width / DAYS_PER_WEEK;
        int columnHeight = height / getRowCount();
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
        }

        Cell pos = new Cell(y / columnHeight, x / columnWidth);
        Cell first = firstDayOfMonthPoint();

        Calendar cal = firstDayOfMonthCalendar();
        cal.add(Calendar.DAY_OF_MONTH, (pos.getRow() - first.getRow()) * DAYS_PER_WEEK + pos.getCol() - first.getCol());
        return cal.getTime();
    }

    private Cell firstDayOfMonthPoint() {
        Calendar cal = firstDayOfMonthCalendar();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return new Cell(1, dayOfWeek - Calendar.SUNDAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        FontMetrics fontMetrics = g.getFontMetrics();
        int textHeight = fontMetrics.getHeight();

        Rectangle r = getBounds();
        g.setColor(this.getBackground());
        g.fillRect(0, 0, r.width, r.height);
        g.setColor(Color.BLACK);

        Calendar cal = firstDayOfMonthCalendar();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int row = 1;
        int col = dayOfWeek - Calendar.SUNDAY;
        int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int columnWidth = r.width / DAYS_PER_WEEK;
        int columnHeight = r.height / getRowCount();

        SimpleDateFormat sdf = new SimpleDateFormat("E");

        Calendar temp = Calendar.getInstance();
        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
            int x = (i - Calendar.SUNDAY) * columnWidth;
            g.setClip(x, 0, columnWidth + 1, columnHeight + 1);
            g.setColor(Color.WHITE);
            g.fillRect(x, 0, columnWidth, columnHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, 0, columnWidth, columnHeight);

            temp.set(Calendar.DAY_OF_WEEK, i);

            g.drawString(sdf.format(temp.getTime()), x + 2, columnHeight - 2);
        }

        Calendar today = Calendar.getInstance();
        DateUtil.setMidnight(today);

        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);
        DateUtil.setMidnight(calDate);

        for (int i = 1; i <= lastDayOfMonth; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            int x = col * columnWidth;
            int y = row * columnHeight;

            if (today.equals(cal)) {
                g.setColor(todayBackgroundColor);
            } else if (calDate.equals(cal)) {
                g.setColor(selectedBackgroundColor);
            } else {
                g.setColor(Color.WHITE);
            }

            g.setClip(x, y, columnWidth + 1, columnHeight + 1);
            g.fillRect(x, y, columnWidth, columnHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, columnWidth, columnHeight);
            g.drawString(String.valueOf(i), x + 2, y + columnHeight - 2);

            if (model != null) {
                Enumeration<Note> notes = model.notesOfDay(cal.getTime());
                int hh = textHeight;
                while (notes.hasMoreElements()) {
                    Note note = notes.nextElement();
                    g.drawString(note.toString(), x + 2, y + hh);
                    hh += textHeight;
                }
            }

            col++;
            if (col >= DAYS_PER_WEEK) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Getter for property date.
     *
     * @return Value of property date.
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Setter for property date.
     *
     * @param date New value of property date.
     */
    public void setDate(Date date) {
        this.firePropertyChange("date", this.date, date);
        this.date = date;
        this.repaint();
    }

    /**
     * Getter for property model.
     *
     * @return Value of property model.
     */
    public CalendarModel getModel() {
        return this.model;
    }

    /**
     * Setter for property model.
     *
     * @param model New value of property model.
     */
    public void setModel(CalendarModel model) {
        this.model = model;
        this.repaint();
    }

    /**
     * Getter for property todayBackgroundColor.
     *
     * @return Value of property todayBackgroundColor.
     */
    public Color getTodayBackgroundColor() {
        return this.todayBackgroundColor;
    }

    /**
     * Setter for property todayBackgroundColor.
     *
     * @param todayBackgroundColor New value of property todayBackgroundColor.
     */
    public void setTodayBackgroundColor(Color todayBackgroundColor) {
        this.todayBackgroundColor = todayBackgroundColor;
        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
            int x = e.getX();
            int y = e.getY();
            // JOptionPane.showMessageDialog(this, "Double click at " + x + " - " + y);
            Date d = dateAtPos(x, y);
            // JOptionPane.showMessageDialog(this, d);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        Date d = dateAtPos(e.getX(), e.getY());
        if (d != null) {
            setDate(d);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    /**
     * Getter for property selectedBackgroundColor.
     *
     * @return Value of property selectedBackgroundColor.
     */
    public Color getSelectedBackgroundColor() {
        return this.selectedBackgroundColor;
    }

    /**
     * Setter for property selectedBackgroundColor.
     *
     * @param selectedBackgroundColor New value of property selectedBackgroundColor.
     */
    public void setSelectedBackgroundColor(Color selectedBackgroundColor) {
        this.selectedBackgroundColor = selectedBackgroundColor;
        this.repaint();
    }
}
