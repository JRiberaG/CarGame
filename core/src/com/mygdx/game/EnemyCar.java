package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;
import static com.mygdx.game.CarGame.CARRIL3;
import static com.mygdx.game.CarGame.score;
import static com.mygdx.game.CarGame.velocidad;

public class EnemyCar extends Thread implements Runnable{
    private int screenH;
//    private int carH;
//    private float y;
//    private float x;
    private int velocidad;
    private boolean go;
    private Rectangle recCar;

//    public EnemyCar(int screenH, int carH, float y, float x, int velocidad) {
//        this.screenH = screenH;
//        this.carH = carH;
//        this.y = y;
//        this.x = x;
//        this.velocidad = velocidad;
//        go = false;
//    }


    public EnemyCar(int screenH, int velocidad, Rectangle recCar) {
        this.screenH = screenH;
        this.velocidad = velocidad;
        this.recCar = recCar;
    }

    public int getScreenH() {
        return screenH;
    }

    public void setScreenH(int screenH) {
        this.screenH = screenH;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public boolean isGo() {
        return go;
    }

    public void setGo(boolean go) {
        this.go = go;
    }

    public Rectangle getRecCar() {
        return recCar;
    }

    public void setRecCar(Rectangle recCar) {
        this.recCar = recCar;
    }

    @Override
    public void run() {
//        while (y > (0 - carH * 2)) {
//            // Si el juego no está pausado, los coches se mueven
//            if (go) {
//                y -= velocidad;
//                try {
//                    Thread.sleep(10);
//                    if (y < 0 - carH){
//                        y = screenH;
//                        // TODO cambiar X
//                        //x = CARRIL3;
//                    }
//                } catch (InterruptedException e) {}
//            }
//        }
        while (recCar.y > (0 - recCar.getHeight() * 2)) {
            // Si el juego no está pausado, los coches se mueven
            if (go) {
                recCar.y -= velocidad;
                try {
                    Thread.sleep(10);
                    if (recCar.y < 0 - recCar.getHeight()){
                        recCar.y = screenH;
                        score++;
                        velocidad = CarGame.velocidad;
                        // TODO cambiar X
                        //x = CARRIL3;
                    }
                } catch (InterruptedException e) {}
            }
        }
    }
}
