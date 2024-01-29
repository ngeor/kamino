/*
 * GameEntity.java
 *
 * Created on 23 ����� 2005, 3:42 ��
 */

package com.ngss.jspidergame;

import java.awt.*;

/**
 * This class represents a game entity.
 *
 * @author ngeor
 */
public abstract class GameEntity extends Rectangle {
    /**
     * The game object.
     */
    private Game game;

    /**
     * Utility field holding list of GameEntityMotionEventss.
     */
    private transient java.util.ArrayList<GameEntityMotionEvents> gameEntityMotionEventsList;

    /**
     * Creates a new instance of a game entity.
     *
     * @param game   The game object
     * @param x      The x coordinate of the entity
     * @param y      The y coordinate of the entity
     * @param width  The width of the entity
     * @param height The height of the entity
     */
    public GameEntity(Game game, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.game = game;
    }

    /**
     * Draws the entity on the game field.
     */
    public abstract void draw(Graphics g);

    /**
     * Registers GameEntityMotionEvents to receive events.
     *
     * @param listener The listener to register.
     */
    public synchronized void addGameEntityMotionEvents(com.ngss.jspidergame.GameEntityMotionEvents listener) {

        if (gameEntityMotionEventsList == null) {
            gameEntityMotionEventsList = new java.util.ArrayList<GameEntityMotionEvents>();
        }
        gameEntityMotionEventsList.add(listener);
    }

    /**
     * Removes GameEntityMotionEvents from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    public synchronized void removeGameEntityMotionEvents(com.ngss.jspidergame.GameEntityMotionEvents listener) {

        if (gameEntityMotionEventsList != null) {
            gameEntityMotionEventsList.remove(listener);
        }
    }

    /**
     * Notifies all registered listeners about the event.
     */
    protected void fireGameEntityMotionEventsBeforeMove() {
        java.util.ArrayList list;
        synchronized (this) {
            if (gameEntityMotionEventsList == null) {
                return;
            }

            list = (java.util.ArrayList) gameEntityMotionEventsList.clone();
        }

        for (int i = 0; i < list.size(); i++) {
            ((com.ngss.jspidergame.GameEntityMotionEvents) list.get(i)).beforeMove(this);
        }
    }

    /**
     * Notifies all registered listeners about the event.
     */
    protected void fireGameEntityMotionEventsAfterMove() {

        java.util.ArrayList list;
        synchronized (this) {
            if (gameEntityMotionEventsList == null) {
                return;
            }

            list = (java.util.ArrayList) gameEntityMotionEventsList.clone();
        }

        for (int i = 0; i < list.size(); i++) {
            ((com.ngss.jspidergame.GameEntityMotionEvents) list.get(i)).afterMove(this);
        }
    }

    protected Game getGame() {
        return game;
    }
}
