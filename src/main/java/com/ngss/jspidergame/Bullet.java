/*
 * Bullet.java
 *
 * Created on 23 ����� 2005, 3:54 ��
 */

package com.ngss.jspidergame;

import java.awt.*;

/**
 * @author ngeor
 */
public class Bullet extends GameAIEntity {

    /**
     * The dimension.
     */
    public static final Dimension DIMENSION = new Dimension(3, 3);

    /**
     * The step.
     */
    public static final int STEP = 7;

    private int step;
    private boolean enemyBullet;

    public Bullet(Game game, int x, int y) {
        this(game, x, y, -STEP, false);
    }

    /**
     * Creates an instance of this class.
     * @param game
     * @param x
     * @param y
     * @param step
     * @param enemyBullet
     */
    public Bullet(Game game, int x, int y, int step, boolean enemyBullet) {
        super(game, x, y, DIMENSION.width, DIMENSION.height);
        this.step = step;
        this.enemyBullet = enemyBullet;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(this.x, this.y, this.width, this.height);
    }

    @Override
    public void move() {
        this.fireGameEntityMotionEventsBeforeMove();
        y += step;
        this.fireGameEntityMotionEventsAfterMove();
    }

    public boolean isEnemyBullet() {
        return this.enemyBullet;
    }
}
