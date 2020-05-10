package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

/**
 * Clase que sirve para que un coche enemigo (Thread) pueda obtener su próximo num de carril
 * sin que coincida con el que había tenido previamente
 */
public class Randomizer extends Thread implements Runnable{
    private ArrayList<Integer> carriles;
    private Random rand;
    private int maxIteraciones;
    private int i;

    public Randomizer() {
        carriles = new ArrayList<>();
        carriles.add(1);
        carriles.add(3);
        carriles.add(2);
        carriles.add(4);
        rand = new Random();
        maxIteraciones = 4500;
        i = 3;
    }

    public ArrayList<Integer> getCarriles() {
        return carriles;
    }

    @Override
    public void run() {
        while(i < maxIteraciones) {
            int pos;
            do {
                pos = rand.nextInt(4) + 1;
            }while(pos == carriles.get(i) || pos == carriles.get(i-1));
            carriles.add(pos);
            i++;
        }
    }

    public void reload() {
        i = 3;
    }
}
