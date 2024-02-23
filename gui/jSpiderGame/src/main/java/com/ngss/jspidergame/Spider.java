/*
 * Spider.java
 *
 * Created on 23 ����� 2005, 3:50 ��
 */

package com.ngss.jspidergame;

import java.awt.*;

/**
 * @author ngeor
 */
public class Spider extends GameEntity {
    /**
     * The dimension.
     */
    public static final Dimension DIMENSION = new Dimension(32, 32);

    /**
     * The step.
     */
    public static final int STEP = 4;

    /**
     * Creates a new instance of Spider.
     */
    public Spider(Game game, int x, int y) {
        super(game, x, y, DIMENSION.width, DIMENSION.height);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(this.x, this.y, this.width, this.height);
    }
}
