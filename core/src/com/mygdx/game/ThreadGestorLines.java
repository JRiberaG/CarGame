package com.mygdx.game;

import java.util.ArrayList;

public class ThreadGestorLines extends Thread implements Runnable{
//    private ArrayList<LineaThread> threads;
    private int screenHeight;
    private int imgLineHeight;
    private int velocidad;

    public ThreadGestorLines(int screenHeight, int imgLineHeight, int velocidad) {
        this.screenHeight = screenHeight;
        this.imgLineHeight = imgLineHeight;
        this.velocidad = velocidad;
//        threads = new ArrayList<>();
    }

//    public ArrayList<LineaThread> getThreads() {
//        return threads;
//    }
//
//    public void setThreads(ArrayList<LineaThread> threads) {
//        this.threads = threads;
//    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getImgLineHeight() {
        return imgLineHeight;
    }

    public void setImgLineHeight(int imgLineHeight) {
        this.imgLineHeight = imgLineHeight;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    @Override
    public void run() {
        int contadorThreads = 0;
        int aux = -1;
        for (int i = screenHeight; i > (0 - imgLineHeight); i-= velocidad) {
            if (i == screenHeight) {
//                threads.add(new LineaThread(screenHeight, imgLineHeight, velocidad));
//                threads.get(contadorThreads).start();
                contadorThreads++;
            } else {
//                if (threads.get(contadorThreads - 1).getY()%imgLineHeight == 0) {
//                if (i % imgLineHeight == 0) {
                if (aux == -1) {
                    if (i >= screenHeight - (imgLineHeight*2)){
                        aux = i;
//                        threads.add(new LineaThread(screenHeight, imgLineHeight, velocidad));
//                        threads.get(contadorThreads).start();
                        contadorThreads++;
                    }
                } else {
                    if (aux - i >= imgLineHeight) {
                        aux = i;
//                        threads.add(new LineaThread(screenHeight, imgLineHeight, velocidad));
//                        threads.get(contadorThreads).start();
                        contadorThreads++;
                    }
                }
            }
        }
    }
}
