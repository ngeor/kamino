/*
 * GameAIEntity.java
 *
 * Created on 23 ����� 2005, 3:43 ��
 */

package com.ngss.jspidergame;

/**
 * This class represents a game entity controlled by the AI, such as an enemy or a bullet.
 *
 * @author ngeor
 */
public abstract class GameAIEntity extends GameEntity {

    public GameAIEntity(Game game, int x, int y, int width, int height) {
        super(game, x, y, width, height);
    }

    /**
     * This method moves the entity to its new location. Note that the actual drawing code
     * must not be placed in this method
     */
    public abstract void move();
}
