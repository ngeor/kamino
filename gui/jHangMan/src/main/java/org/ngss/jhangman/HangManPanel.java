/*
 * HangManPanel.java
 *
 * Created on August 6, 2004, 1:06 PM
 */

package org.ngss.jhangman;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * @author ngeor
 */
public class HangManPanel extends JPanel {
    private static final String EL_LETTERS = "ΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩ";
    private static final String EN_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVXWYZ";
    private static final int FAIL_STATE = 5;

    private javax.swing.JLabel lblAnswer;
    private javax.swing.JLabel lblFace;
    private javax.swing.JLabel lblState;
    private javax.swing.JPanel panLetters;
    private javax.swing.JTextArea txtQuestion;

    private char[] correctAnswer;
    private char[] currentAnswer;
    private int state;
    private boolean solved;
    private boolean failed;
    private String oldLanguage;

    /**
     * Creates new form HangManPanel.
     */
    public HangManPanel() {
        initComponents();
    }

    private void updateStateImage() {
        final int step = 3;
        String s;
        if (solved) {
            s = "/org/ngss/jhangman/img/success.png";
        } else if (failed) {
            s = "/org/ngss/jhangman/img/fail.png";
        } else {
            s = "/org/ngss/jhangman/img/step" + (state + step) + ".png";
        }

        lblState.setIcon(new ImageIcon(getClass().getResource(s)));
    }

    private char removeStress(char ch) {
        String s1 = "ΆΈΉΊΌΎΏΪΫ";
        String s2 = "ΑΕΗΙΟΥΩΙΥ";

        for (int i = 0; i < s1.length(); i++) {
            if (ch == s1.charAt(i)) {
                return s2.charAt(i);
            }
        }

        return ch;
    }

    /**
     * Checks if the game is solved.
     */
    public boolean isSolved() {
        return solved;
    }

    /**
     * Starts the game.
     */
    public void startGame(String question, String answer, String language) {

        solved = false;
        failed = false;
        if (state != 0) {
            state = 0;
            updateStateImage();
        }

        txtQuestion.setText(question);

        String capitalAnswer = answer.toUpperCase();

        int len = capitalAnswer.length() * 2; // every character preceded by a space
        correctAnswer = new char[len];
        currentAnswer = new char[len];
        for (int i = 0; i < capitalAnswer.length(); i++) {
            correctAnswer[i * 2] = ' ';
            correctAnswer[i * 2 + 1] = removeStress(capitalAnswer.charAt(i));

            currentAnswer[i * 2] = ' ';
            currentAnswer[i * 2 + 1] = (capitalAnswer.charAt(i) == ' ') ? ' ' : '_';
        }

        lblAnswer.setText(new String(currentAnswer));

        if (oldLanguage == null || !oldLanguage.equals(language)) {
            if ("en".equals(language)) {
                createLetterControls(EN_LETTERS);
                oldLanguage = "en";
            } else {
                createLetterControls(EL_LETTERS);
                oldLanguage = "el";
            }
        }
    }

    private void nextState(boolean found, boolean isSolved) {
        String sImg = null;
        solved = isSolved;
        if (solved) {
            updateStateImage();
        } else {
            if (!found) {
                state++;
                failed = state >= FAIL_STATE;
                updateStateImage();
            }
        }
    }

    /**
     * A listener of letter actions.
     */
    class LetterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (solved || failed) {
                return;
            }

            Object source = e.getSource();
            if (source instanceof JButton btn) {
                char chLetter = e.getActionCommand().charAt(0);
                boolean found = false;
                int remainingChars = 0;
                for (int i = 0; i < currentAnswer.length; i++) {
                    char chFound = currentAnswer[i];
                    char chReal = correctAnswer[i];
                    if (chLetter == chReal && chLetter != chFound) {
                        currentAnswer[i] = chLetter;
                        found = true;
                    }

                    if (currentAnswer[i] == '_') {
                        remainingChars++;
                    }
                }

                if (found) {
                    lblAnswer.setText(new String(currentAnswer));
                }

                btn.setEnabled(false);

                nextState(found, remainingChars == 0);

            } else {
                System.out.println(e.getActionCommand());
            }
        }
    }

    private void createLetterControls(String letters) {
        panLetters.removeAll();
        LetterActionListener lal = new LetterActionListener();
        String s = letters;
        for (int i = 0; i < s.length(); i++) {
            JButton btn = new JButton();
            String sLetter = s.substring(i, i + 1);
            btn.setText(sLetter);
            btn.setActionCommand(sLetter);
            btn.addActionListener(lal);
            panLetters.add(btn);
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblState = new javax.swing.JLabel();
        lblFace = new javax.swing.JLabel();
        txtQuestion = new javax.swing.JTextArea();
        lblAnswer = new javax.swing.JLabel();
        panLetters = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        setBackground(new java.awt.Color(255, 255, 255));
        lblState.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ngss/jhangman/img/step3.png")));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 4;
        add(lblState, gridBagConstraints);

        lblFace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ngss/jhangman/img/face.png")));
        lblFace.setText("\u0391\u03c2 \u03c0\u03b1\u03af\u03be\u03bf\u03c5\u03bc\u03b5 "
                + "\u03ba\u03c1\u03b5\u03bc\u03ac\u03bb\u03b1!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(lblFace, gridBagConstraints);

        txtQuestion.setEditable(false);
        txtQuestion.setFont(new java.awt.Font("Dialog", 0, 24));
        txtQuestion.setLineWrap(true);
        txtQuestion.setText("\u03a0\u03ce\u03c2 \u03bb\u03ad\u03b3\u03b5\u03c4\u03b1\u03b9 "
                + "\u03c4\u03bf \u03c3\u03cd\u03c3\u03c4\u03b7\u03bc\u03b1 \u03c0\u03bf\u03c5 "
                + "\u03b1\u03c0\u03bf\u03b8\u03b7\u03ba\u03b5\u03cd\u03b5\u03b9 \u03c4\u03b1 "
                + "\u03b4\u03b9\u03ac\u03c6\u03bf\u03c1\u03b1 \u03b4\u03b5\u03b4\u03bf\u03bc\u03ad\u03bd\u03b1;");
        txtQuestion.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(txtQuestion, gridBagConstraints);

        lblAnswer.setFont(new java.awt.Font("Dialog", 1, 18));
        lblAnswer.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        gridBagConstraints.weightx = 1.0;
        add(lblAnswer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panLetters, gridBagConstraints);
    }
}
