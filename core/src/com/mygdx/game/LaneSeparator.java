package com.mygdx.game;

import java.util.ArrayList;

import static com.mygdx.game.CarGame.SCREEN_HEIGHT;

public class LaneSeparator extends Thread implements Runnable{

    private int imgLineHeight;
    private ArrayList<Float> x;
    private int y;
    private float velocidad;
    private boolean go;

    public LaneSeparator(int imgLineHeight, ArrayList<Float> x, int y) {
        this.imgLineHeight = imgLineHeight;
        this.x = x;
        this.y = y;
        velocidad = 3;
    }

    @Override
    public void run() {
        while (y > 0 - imgLineHeight) {
            if (go) {
                y -= velocidad;

                try {
                    Thread.sleep(10);
                    if (y < 0) {
                        y = SCREEN_HEIGHT;
                    }
                } catch (InterruptedException e) {}
            }
        }
    }

    public ArrayList<Float> getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setGo(boolean go) {
        this.go = go;
    }
}
