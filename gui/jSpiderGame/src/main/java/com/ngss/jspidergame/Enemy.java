/*
 * Enemy.java
 *
 * Created on 23 ����� 2005, 3:46 ��
 */

package com.ngss.jspidergame;

import java.awt.*;
import java.util.Random;

/**
 * @author ngeor
 */
public class Enemy extends GameAIEntity {
    /**
     * The dimension.
     */
    public static final Dimension DIMENSION = new Dimension(32, 32);

    /**
     * The step.
     */
    public static final int STEP = 4;

    private int moves;

    public Enemy(Game game, int x, int y) {
        super(game, x, y, DIMENSION.width, DIMENSION.height);
    }

    /*
    *  Level 1: Horizontal only
    *  Level 2: Vertical too
    *  Level 3 : fire
    *  Level 4: fire vertical
    *  Level 5: take short pause horizontal only
    *  Level 6: take short pause vertical

    * Level 7: faster Horizontal only
    * Level 8: faster vertical only
    * Level 9: faster take short pause horizontal only
    * Level 10: faster take short pause vertical
    */

    private boolean canFire() {
        return getGame().getLevel() > 2;
    }

    private boolean canMoveDown() {
        return getGame().getLevel() % 2 == 0;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private boolean canTakePause() {
        int level = getGame().getLevel() - 5;
        // level: 5,6,9,10,13,14
        // level - 5: 0,1,4,5,8,9

        return level >= 0 && (level % 4) < 2;
    }

    @Override
    public void draw(Graphics g) {
        Color[] clr = {Color.RED, Color.GREEN, Color.WHITE, Color.MAGENTA};
        g.setColor(clr[getGame().getLevel() % clr.length]);
        g.fillRect(this.x, this.y, this.width, this.height);
    }

    @Override
    @SuppressWarnings("checkstyle:MagicNumber")
    public void move() {
        moves++;

        boolean shouldMove = true; // maybe don't move in more difficult levels if there's space to the right

        if (canTakePause()) {
            shouldMove = (moves % 100) < 80;
        }
        if (shouldMove) {

            this.fireGameEntityMotionEventsBeforeMove();
            doMove();
            this.fireGameEntityMotionEventsAfterMove();
        }

        boolean shouldFire = canFire() && new Random().nextInt(20) < (getGame().getLevel());
        if (shouldFire) {
            getGame().enemyFire(this);
        }
    }

    private void doMove() {
        if (x + STEP > getGame().DIMENSION.width - width) {
            x = 0;
        } else {
            x += STEP;
        }

        if (canMoveDown()) {
            if (y + STEP > getGame().DIMENSION.height - height) {
                y = 0;
            } else {
                y += STEP;
            }
        }
    }
}
