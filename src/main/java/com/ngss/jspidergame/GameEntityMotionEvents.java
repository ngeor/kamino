/*
 * GameEntityMotionEvents.java
 *
 * Created on 23 ����� 2005, 4:19 ��
 */

package com.ngss.jspidergame;

/**
 * @author ngeor
 */
public interface GameEntityMotionEvents extends java.util.EventListener {
    void beforeMove(GameEntity ge);

    void afterMove(GameEntity ge);
}
