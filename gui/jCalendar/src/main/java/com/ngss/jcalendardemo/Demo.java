package com.ngss.jcalendardemo;

import com.ngss.jcalendar.JCalendar;
import java.awt.*;
import javax.swing.*;

/**
 * The demo app.
 */
@SuppressWarnings("serial")
public class Demo extends JFrame {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 480;

    /**
     * Creates a new instance of this class.
     */
    public Demo() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JCalendar jcal = new JCalendar();
        add(jcal, BorderLayout.CENTER);

        this.setSize(WIDTH, HEIGHT);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Demo().setVisible(true);
            }
        });
    }
}
