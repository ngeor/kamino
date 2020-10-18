/*
 * GameEvents.java
 *
 * Created on 23 ����� 2005, 4:46 ��
 */

package com.ngss.jspidergame;

/**
 * @author ngeor
 */
public interface GameEvents extends java.util.EventListener {
    void entityCreated(GameEntity ge);

    void entityDestroyed(GameEntity ge);

    void spiderHit();

    void aliveChanged(boolean value);

    void levelChanged(int value);
}
