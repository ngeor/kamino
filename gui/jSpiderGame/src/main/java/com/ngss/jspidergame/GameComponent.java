/*
 * GameComponent.java
 *
 * Created on 23 ����� 2005, 2:04 ��
 */

package com.ngss.jspidergame;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.*;
import javax.swing.*;

/**
 * @author ngeor
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class GameComponent extends JComponent {
    private Game game;

    private GameEntityMotionEvents geme = new GameEntityMotionEvents() {
        @Override
        public void beforeMove(GameEntity ge) {
            Graphics g = getGraphics();
            eraseRect(g, ge);
        }

        @Override
        public void afterMove(GameEntity ge) {
            Graphics g = getGraphics();
            ge.draw(g);
        }
    };

    /**
     * A timer.
     */
    class MyTimer extends Thread {

        private int getLevelSpeed() {
            int level = game.getLevel();
            if (level >= 7) {
                return 1 + ((level - 7) / 4);
            } else {
                return 0;
            }
        }

        private int getLevelDelay() {
            int speed = getLevelSpeed();
            int sleep = 30 - speed * 10;
            if (sleep <= 0) {
                sleep = 1;
            }

            return sleep;
        }

        @Override
        public void run() {
            try {
                while (game.isGameAlive()) {
                    game.processEvents();
                    sleep(getLevelDelay());
                }
            } catch (InterruptedException ex) {
            }
        }
    }

    private MyTimer myTimer = new MyTimer();

    /**
     * Creates a new instance of GameComponent.
     */
    public GameComponent(Game agame) {
        this.game = agame;

        game.addGameEvents(new GameEvents() {
            @Override
            public void entityCreated(GameEntity ge) {
                ge.addGameEntityMotionEvents(geme);
                if (ge instanceof Bullet) {
                    playClip("bullet.wav");
                }
            }

            @Override
            public void entityDestroyed(GameEntity ge) {
                geme.beforeMove(ge);
                if (ge instanceof Enemy) {
                    playClip("enemyhit.wav");
                    if (game.getEnemyCount() <= 0) {
                        game.setGameAlive(false);
                        game.init(game.getLevel() + 1);
                        System.out.println("Level " + game.getLevel() + " - hit S to start");
                        repaint();
                    }
                }
            }

            @Override
            public void spiderHit() {
                game.setGameAlive(false);
                System.out.println("One Live Less");
                game.init(game.getLevel());
                repaint();
            }

            @Override
            public void aliveChanged(boolean value) {
                if (value) {
                    myTimer.start();
                } else {
                    myTimer.interrupt();
                    myTimer = new MyTimer();
                }
            }

            @Override
            public void levelChanged(int value) {}
        });
    }

    private void eraseRect(Graphics g, Rectangle r) {
        g.setColor(new Color(0, 0, 96));
        g.fillRect(r.x, r.y, r.width, r.height);
    }

    @Override
    public void paint(Graphics g) {
        Dimension d = this.getSize();
        g.setColor(new Color(0, 0, 96));
        g.fillRect(0, 0, d.width, d.height);

        for (GameEntity ge : game.entities()) {
            ge.draw(g);
        }
    }

    private void moveSpider(int x) {
        Spider spider = game.getSpider();
        if (game.isGameAlive() && x >= 0 && x < game.DIMENSION.width - spider.width) {
            Graphics g = this.getGraphics();

            eraseRect(g, spider);
            spider.x = x;
            spider.draw(g);
        }
    }

    public void moveLeft() {
        Spider spider = game.getSpider();
        moveSpider(spider.x - Spider.STEP);
    }

    public void moveRight() {
        Spider spider = game.getSpider();
        moveSpider(spider.x + Spider.STEP);
    }

    public void moveAt(int x) {
        Spider spider = game.getSpider();
        moveSpider(x - spider.width / 2);
    }

    /**
     * Lets the spider fire.
     */
    public void fire() {
        if (game.isGameAlive()) {
            game.spiderFire();
        }
    }

    public void togglePause() {
        game.setGameAlive(!game.isGameAlive());
    }

    private void playClip(String file) {
        try {
            Clip clip = AudioSystem.getClip();
            InputStream inputStream = GameComponent.class.getResourceAsStream(file);
            if (inputStream == null) {
                System.err.println("Null stream");
            } else {
                AudioInputStream stream = AudioSystem.getAudioInputStream(inputStream);
                clip.open(stream);
                clip.start();
            }

        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
