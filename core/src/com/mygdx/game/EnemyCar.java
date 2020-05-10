package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

import static com.mygdx.game.CarGame.CARRIL1;
import static com.mygdx.game.CarGame.CARRIL2;
import static com.mygdx.game.CarGame.CARRIL3;
import static com.mygdx.game.CarGame.CARRIL4;
import static com.mygdx.game.CarGame.SCORE_JUMP;
import static com.mygdx.game.CarGame.SCREEN_HEIGHT;
import static com.mygdx.game.CarGame.listEnemyCars;
import static com.mygdx.game.CarGame.moveDragged;
import static com.mygdx.game.CarGame.moveTapped;
import static com.mygdx.game.CarGame.multiplierDragged;
import static com.mygdx.game.CarGame.multiplierTapped;
import static com.mygdx.game.CarGame.score;
import static com.mygdx.game.CarGame.velocidad;

public class EnemyCar extends Thread implements Runnable{
    private int screenH;
    private float velocidad;
    private boolean go;
    private Rectangle recCar;

    private int i;

    public EnemyCar(int screenH, float velocidad, Rectangle recCar) {
        this.screenH = screenH;
        this.velocidad = velocidad;
        this.recCar = recCar;

        if (recCar.x == CARRIL1) {
            i = 1;
        } else if(recCar.x == CARRIL2) {
            i = 2;
        } else if(recCar.x == CARRIL3) {
            i = 3;
        } else {
            i = 4;
        }
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

    @Override
    public void run() {
        while (recCar.y > (0 - recCar.getHeight() * 2)) {
            // Si el juego no está pausado, el coche se mueve
            if (go) {
                // TODO pendiente de comprobar que funcione
                for (EnemyCar ec : listEnemyCars) {
                    if (ec.recCar.y > (1700) &&
                        recCar.y > (1700) &&
                        ec.recCar.x == recCar.x &&
                        (recCar.y - ec.recCar.y) < 256 ) {
                        recCar.y -= 50;
                    }
                }

                // Baja por la carretera
                recCar.y -= velocidad;
                try {
                    Thread.sleep(10);
                    if (recCar.y < 0 - recCar.getHeight()){
                        // Coche vuelve a aparecer
                        recCar.y = screenH;
                        // Un punto más para el jugador
                        score++;

                        // Se obtiene el nuevo carril por donde saldrá
                        i+=3;
                        switch(CarGame.randomizer.getCarriles().get(i)){
                            case 1:
                                recCar.x = CARRIL1;
                                break;
                            case 2:
                                recCar.x = CARRIL2;
                                break;
                            case 3:
                                recCar.x = CARRIL3;
                                break;
                            default:
                                recCar.x = CARRIL4;
                                break;
                        }

                        if (score % SCORE_JUMP == 0 && score > 0) {
                            // Aumenta la velocidad de los coches enemigos
                            CarGame.velocidad += 0.3;
                            // El usuario mejora su velocidad sólo 3 veces
                            if (score <= SCORE_JUMP * 3) {
                                // Aumenta velocidad coche jugador
                                moveDragged += multiplierDragged;
                                moveTapped += multiplierTapped;
                            }

                            Thread.sleep(150);
                        }
                        velocidad = CarGame.velocidad;
                    }
                } catch (InterruptedException e) {}
            }
        }
    }
}
