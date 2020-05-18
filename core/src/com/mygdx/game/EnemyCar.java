package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;

import static com.mygdx.game.CarGame.LANE1;
import static com.mygdx.game.CarGame.LANE2;
import static com.mygdx.game.CarGame.LANE3;
import static com.mygdx.game.CarGame.LANE4;
import static com.mygdx.game.CarGame.SCORE_JUMP;
import static com.mygdx.game.CarGame.moveDragged;
import static com.mygdx.game.CarGame.moveTapped;
import static com.mygdx.game.CarGame.multiplierDragged;
import static com.mygdx.game.CarGame.multiplierTapped;
import static com.mygdx.game.CarGame.score;

public class EnemyCar extends Thread implements Runnable{
    private static final float INCREASE = 0.25f;

    private int screenH;
    private float speed;
    private boolean go;
    private Rectangle recCar;

    private int i;

    public EnemyCar(int screenH, float speed, Rectangle recCar) {
        this.screenH = screenH;
        this.speed = speed;
        this.recCar = recCar;

        if (recCar.x == LANE1) {
            i = 1;
        } else if(recCar.x == LANE2) {
            i = 2;
        } else if(recCar.x == LANE3) {
            i = 3;
        } else {
            i = 4;
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
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
            // If the game is not paused, the car moves
            if (go) {
                // Goes down the road
                recCar.y -= speed;
                try {
                    Thread.sleep(10);
                    if (recCar.y < 0 - recCar.getHeight()){
                        // The car reappears
                        recCar.y = screenH;

                        score++;

                        // The new lane is obtained
                        i+=2;
                        switch(CarGame.randomizer.getLanes().get(i)){
                            case 1:
                                recCar.x = LANE1;
                                break;
                            case 2:
                                recCar.x = LANE2;
                                break;
                            case 3:
                                recCar.x = LANE3;
                                break;
                            default:
                                recCar.x = LANE4;
                                break;
                        }

                        if (score % SCORE_JUMP == 0 && score > 0) {
                            // The speed is increased
                            CarGame.speed += INCREASE;
                            // The user will be able to increase his car speed only three times
                            if (score <= SCORE_JUMP * 3) {
                                // Increases speed of the player's car
                                moveDragged += multiplierDragged;
                                moveTapped += multiplierTapped;
                            }

                            Thread.sleep(150);
                        }
                        speed = CarGame.speed;
                    }
                } catch (InterruptedException e) {}
            }
        }
    }
}
