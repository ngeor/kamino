package jtetris.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.*;
import jtetris.common.Game;
import jtetris.common.GameListener;
import jtetris.common.GameState;
import jtetris.common.MovingShape;
import jtetris.common.Shape;

/**
 * @author ngeor
 */
public class GameComponent extends JPanel implements KeyListener {

    private static final int DELAY = 500;
    private static final int ANIMATION_DELAY = 10;
    private static final int LEVEL_SPEED_UP_FACTOR = 10;
    private final Game game;
    private final Timer timer;

    /**
     * Creates a new instance of this class.
     */
    GameComponent(final Game game) {
        this.game = game;
        addKeyListener(this);

        timer = new Timer(DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (game.getState() == GameState.GameOver) {
                    timer.stop();
                    return;
                }

                game.moveDown();
            }
        });

        game.addGameListener(new GameListener() {

            @Override
            public void deletingRow(int row) {
                slideImageDown(getGraphics(), row);
                AudioHelper.play("beepspace.wav");

                int level = game.getLevel();
                int delay = DELAY - level * LEVEL_SPEED_UP_FACTOR;
                if (delay <= 0) {
                    delay = 1;
                }

                timer.setDelay(delay);
            }

            @Override
            public void stateChanged() {}

            @Override
            public void moved(MovingShape old, MovingShape current) {
                Graphics g = getGraphics();

                Dimension bounds = getSize();
                int cellSize = Math.min(bounds.width / getColumns(), bounds.height / getRows());

                int dcol = Math.abs(current.getColumn() - old.getColumn());
                int drow = Math.abs(current.getRow() - old.getRow());

                int width = (dcol + Math.max(current.getColumns(), old.getColumns())) * cellSize;
                int height = (drow + Math.max(current.getRows(), old.getRows())) * cellSize;

                BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics bufferedGraphics = bufferedImage.getGraphics();

                int oldCol = current.getColumn() > old.getColumn() ? 0 : dcol;
                int newCol = current.getColumn() > old.getColumn() ? dcol : 0;
                int oldRow = current.getRow() > old.getRow() ? 0 : drow;
                int newRow = current.getRow() > old.getRow() ? drow : 0;

                erase(bufferedGraphics, old.getShapeDefinition(), cellSize, oldCol, oldRow);
                paint(bufferedGraphics, current.getShapeDefinition(), cellSize, newCol, newRow);

                int destCol = current.getColumn() > old.getColumn() ? old.getColumn() : current.getColumn();
                int destRow = current.getRow() > old.getRow() ? old.getRow() : current.getRow();
                g.drawImage(bufferedImage, destCol * cellSize, destRow * cellSize, null);
                bufferedGraphics.dispose();
                g.dispose();
            }

            @Override
            public void shapeChanged(MovingShape nextBlock) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void paint(Graphics g) {
        Dimension bounds = getSize();
        g.setColor(Color.white);
        g.fillRect(0, 0, bounds.width, bounds.height);

        int cellSize = Math.min(bounds.width / getColumns(), bounds.height / getRows());
        BlockPainter blockPainter = BlockPainter.instance();
        blockPainter.paint(game.getMap(), g, cellSize);

        MovingShape current = game.getCurrent();
        if (current != null) {
            blockPainter.paint(current, g, cellSize);
        }
    }

    private void paint(Graphics g, Shape shape, int cellSize, int x, int y) {
        BlockPainter blockPainter = BlockPainter.instance();
        blockPainter.paint(shape, g, cellSize, y, x);
    }

    private void erase(Graphics g, Shape shape, int cellSize, int x, int y) {
        BlockPainter blockPainter = BlockPainter.instance();
        blockPainter.erase(shape, g, cellSize, y, x);
    }

    private int getColumns() {
        return game.getMap().getColumns();
    }

    private int getRows() {
        return game.getMap().getRows();
    }

    private void makeBeep() {
        AudioHelper.play("typekey.wav");
    }

    private void slideImageDown(Graphics g, int row) {
        Rectangle bounds = getBounds();
        int cellSize = Math.min(bounds.width / getColumns(), bounds.height / getRows());

        for (int y = 0; y < cellSize; y++) {
            g.copyArea(0, 0, bounds.width, row * cellSize + y, 0, 1);
            try {
                Thread.sleep(ANIMATION_DELAY);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                game.moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                makeBeep();
                game.moveRight();
                break;
            case KeyEvent.VK_UP:
                makeBeep();
                game.rotate();
                break;
            case KeyEvent.VK_DOWN:
                makeBeep();
                game.moveDown();
                break;
            case KeyEvent.VK_SPACE:
                game.drop();
                break;
            case KeyEvent.VK_S:
                switch (game.getState()) {
                    case Started:
                        timer.stop();
                        game.pause();
                        break;
                    case Paused:
                        game.start();
                        timer.start();
                        break;
                    default:
                        timer.stop();
                        game.reset();
                        game.start();
                        timer.start();
                        break;
                }
                break;
            case KeyEvent.VK_A:
                AudioHelper.toggleMute();
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
