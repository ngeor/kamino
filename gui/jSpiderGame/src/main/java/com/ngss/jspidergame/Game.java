/*
 * Game.java
 *
 * Created on 23 ����� 2005, 4:10 ��
 */

package com.ngss.jspidergame;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author ngeor
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class Game {

    /**
     * The dimension.
     */
    public static final Dimension DIMENSION = new Dimension(620, 440);

    private int level;
    private boolean gameAlive;
    private Spider spider;
    private Vector<Enemy> enemies = new Vector<Enemy>();
    private Vector<Bullet> bullets = new Vector<Bullet>();

    /**
     * Utility field holding list of GameEventss.
     */
    private transient java.util.ArrayList<GameEvents> gameEventsList;

    /**
     * Creates a new instance of Game.
     */
    public Game() {}

    public synchronized int getLevel() {
        return level;
    }

    /**
     * Initializes the game.
     * @param aLevel
     */
    public synchronized void init(int aLevel) {
        if (this.level != aLevel) {
            this.level = aLevel;
            this.fireGameEventsLevelChanged();
        }

        enemies.clear();
        bullets.clear();

        spider = new Spider(
                this, (DIMENSION.width - Spider.DIMENSION.width) / 2, DIMENSION.height - Spider.DIMENSION.height);
        fireGameEventsEntityCreated(spider);

        for (int i = 0; i < 10; i++) {
            addEnemy((int) (i * Enemy.DIMENSION.width * 1.5), 0);
        }
    }

    /**
     * Registers GameEvents to receive events.
     *
     * @param listener The listener to register.
     */
    public synchronized void addGameEvents(com.ngss.jspidergame.GameEvents listener) {

        if (gameEventsList == null) {
            gameEventsList = new java.util.ArrayList<GameEvents>();
        }
        gameEventsList.add(listener);
    }

    /**
     * Removes GameEvents from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    public synchronized void removeGameEvents(com.ngss.jspidergame.GameEvents listener) {

        if (gameEventsList != null) {
            gameEventsList.remove(listener);
        }
    }

    /**
     * Notifies all registered listeners about the event.
     */
    protected void fireGameEventsEntityCreated(GameEntity ge) {
        java.util.ArrayList list;
        synchronized (this) {
            if (gameEventsList == null) {
                return;
            }
            list = (java.util.ArrayList) gameEventsList.clone();
        }
        for (int i = 0; i < list.size(); i++) {
            ((com.ngss.jspidergame.GameEvents) list.get(i)).entityCreated(ge);
        }
    }

    /**
     * Notifies all registered listeners about the event.
     */
    protected void fireGameEventsEntityDestroyed(GameEntity ge) {
        java.util.ArrayList list;
        synchronized (this) {
            if (gameEventsList == null) {
                return;
            }
            list = (java.util.ArrayList) gameEventsList.clone();
        }
        for (int i = 0; i < list.size(); i++) {
            ((com.ngss.jspidergame.GameEvents) list.get(i)).entityDestroyed(ge);
        }
    }

    /**
     * Notifies all registered listeners about the event.
     */
    protected void fireGameEventsSpiderHit() {
        java.util.ArrayList list;
        synchronized (this) {
            if (gameEventsList == null) {
                return;
            }
            list = (java.util.ArrayList) gameEventsList.clone();
        }
        for (int i = 0; i < list.size(); i++) {
            ((com.ngss.jspidergame.GameEvents) list.get(i)).spiderHit();
        }
    }

    /**
     * Notifies all registered listeners about the event.
     */
    protected void fireGameEventsAliveChanged() {
        java.util.ArrayList list;
        synchronized (this) {
            if (gameEventsList == null) {
                return;
            }
            list = (java.util.ArrayList) gameEventsList.clone();
        }
        for (int i = 0; i < list.size(); i++) {
            ((com.ngss.jspidergame.GameEvents) list.get(i)).aliveChanged(gameAlive);
        }
    }

    /**
     * Notifies all registered listeners about the event.
     */
    protected void fireGameEventsLevelChanged() {
        java.util.ArrayList list;
        synchronized (this) {
            if (gameEventsList == null) {
                return;
            }
            list = (java.util.ArrayList) gameEventsList.clone();
        }
        for (int i = 0; i < list.size(); i++) {
            ((com.ngss.jspidergame.GameEvents) list.get(i)).levelChanged(level);
        }
    }

    private synchronized void addEnemy(int x, int y) {
        Enemy enemy = new Enemy(this, x, y);
        enemies.add(enemy);
        fireGameEventsEntityCreated(enemy);
    }

    public synchronized Spider getSpider() {
        return spider;
    }

    private synchronized boolean moveEnemies() {
        for (Enemy enemy : enemies) {
            enemy.move();

            if (enemy.intersects(spider)) {
                this.fireGameEventsSpiderHit();
                return false;
            }
        }
        return true;
    }

    private synchronized void moveBullets() {
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();

            bullet.move();
            if (bullet.y < 0 || bullet.y >= DIMENSION.height) {
                iter.remove();
                this.fireGameEventsEntityDestroyed(bullet);
            } else {
                if (bullet.isEnemyBullet()) {
                    if (spider.intersects(bullet)) {
                        iter.remove();
                        this.fireGameEventsSpiderHit();
                    }
                } else {
                    Enemy enemy = checkCollision(bullet);
                    if (enemy != null) {
                        iter.remove();
                        enemies.remove(enemy);
                        this.fireGameEventsEntityDestroyed(bullet);
                        this.fireGameEventsEntityDestroyed(enemy);
                    }
                }
            }
        }
    }

    private synchronized Enemy checkCollision(Bullet bullet) {
        for (Enemy enemy : enemies) {
            if (enemy.intersects(bullet)) {
                return enemy;
            }
        }
        return null;
    }

    /**
     * Gets the entities.
     * @return
     */
    public synchronized List<GameEntity> entities() {
        Vector<GameEntity> v = new Vector<GameEntity>();
        for (Enemy e : enemies) {
            v.add(e);
        }
        for (Bullet b : bullets) {
            v.add(b);
        }
        v.add(spider);
        return v;
    }

    /**
     * Lets the spider fire.
     */
    public synchronized void spiderFire() {
        int spiderBullets = 0;
        for (Bullet b : bullets) {
            if (!b.isEnemyBullet()) {
                spiderBullets++;
            }
        }

        if (spiderBullets <= 2) {
            Bullet bullet = new Bullet(this, spider.x + spider.width / 2, spider.y - Bullet.DIMENSION.height - 1);
            bullets.add(bullet);
            fireGameEventsEntityCreated(bullet);
        }
    }

    /**
     * Lets the enemy fire.
     * @param enemy
     */
    public synchronized void enemyFire(Enemy enemy) {
        int enemyBullets = 0;
        for (Bullet b : bullets) {
            if (b.isEnemyBullet()) {
                enemyBullets++;
            }
        }

        if (enemyBullets <= 0) {
            Bullet bullet = new Bullet(
                    this, enemy.x + enemy.width / 2, enemy.y + Bullet.DIMENSION.height + 1, Bullet.STEP, true);
            bullets.add(bullet);
            fireGameEventsEntityCreated(bullet);
        }
    }

    public synchronized int getEnemyCount() {
        return enemies.size();
    }

    public synchronized boolean isGameAlive() {
        return gameAlive;
    }

    /**
     * Sets the game alive.
     * @param gameAlive
     */
    public synchronized void setGameAlive(boolean gameAlive) {
        if (this.gameAlive != gameAlive) {
            this.gameAlive = gameAlive;
            this.fireGameEventsAliveChanged();
        }
    }

    public synchronized void processEvents() {
        moveEnemies();
        moveBullets();
    }
}
