package jtetris.swing;

import jtetris.common.Game;
import jtetris.common.GameListener;
import jtetris.common.GameState;
import jtetris.common.MovingShape;

import javax.swing.*;

import java.awt.*;

/**
 * MainForm.java.
 * <p>
 * Created on Sep 4, 2011, 10:04:51 AM
 *
 * @author ngeor
 */
public final class MainForm extends javax.swing.JFrame {

    private static final int INITIAL_HEIGHT = 400;
    private static final int INITIAL_WIDTH = 200;
    private static final int NEXT_PREVIEW_SIZE = 80;
    private final Game game = new Game();
    private final GameComponent gc;
    private final ShapeComponent nextPreview;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblLevel;
    private javax.swing.JLabel lblLines;

    /**
     * Creates new form MainForm.
     */
    private MainForm() {
        nextPreview = new ShapeComponent();

        initComponents();

        gc = new GameComponent(game);
        getContentPane().add(gc, BorderLayout.CENTER);

        game.addGameListener(new GameListener() {

            @Override
            public void deletingRow(int row) {
                updateLabels();
            }

            @Override
            public void stateChanged() {
                if (game.getState() == GameState.GameOver) {
                    JOptionPane.showMessageDialog(MainForm.this, "Game over!");
                }
            }

            @Override
            public void moved(MovingShape old, MovingShape current) {

            }

            @Override
            public void shapeChanged(MovingShape nextBlock) {
                nextPreview.setShape(nextBlock);
            }
        });
        gc.requestFocus();

        final Dimension initialSize = new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT);
        gc.setMinimumSize(initialSize);
        gc.setPreferredSize(initialSize);

        nextPreview.setMinimumSize(new Dimension(NEXT_PREVIEW_SIZE, NEXT_PREVIEW_SIZE));
        nextPreview.setPreferredSize(new Dimension(NEXT_PREVIEW_SIZE, NEXT_PREVIEW_SIZE));

        updateLabels();
        pack();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new MainForm().setVisible(true));
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblLevel = new javax.swing.JLabel();
        lblLines = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("jTetris");

        lblLevel.setText("Level: {0}");
        jPanel1.add(lblLevel);

        lblLines.setText("Lines: {0}");
        jPanel1.add(lblLines);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        pack();
    }

    private void updateLabels() {
        lblLevel.setText("Level: " + (game.getLevel() + 1));
        lblLines.setText("Lines: " + game.getLinesCleared());
    }
}