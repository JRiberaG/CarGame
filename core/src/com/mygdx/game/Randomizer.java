package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class used to decided which lanes the enemyCars will take.
 */
public class Randomizer extends Thread implements Runnable{

    private static final int MAX_ITERATIONS = 4500;

    private ArrayList<Integer> lanes;
    private Random rand;
    private int i;

    public Randomizer() {
        lanes = new ArrayList<>();
        lanes.add(1);
        lanes.add(3);
        lanes.add(2);
        lanes.add(4);
        rand = new Random();
        i = 3;
    }

    public ArrayList<Integer> getLanes() {
        return lanes;
    }

    @Override
    public void run() {
        while(true) {
            while(i < MAX_ITERATIONS) {
                int pos;
                do {
                    pos = rand.nextInt(4) + 1;
                }while(pos == lanes.get(i) || pos == lanes.get(i-1));
                System.out.println(pos);
                lanes.add(pos);
                i++;
            }
        }
    }

    public void reload() {
        i = 3;
    }
}
