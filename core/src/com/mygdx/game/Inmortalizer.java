package com.mygdx.game;

public class Inmortalizer extends Thread implements Runnable{
    private boolean start;
    private boolean ready;
    private int i;

    // Constructor
    public Inmortalizer() {
        start = false;
        ready = false;
        i = 0;
    }

    // Método que inicia el contador
    public void count(){
        start = true;
        ready = false;
    }

    // Devuelve si está listo (finaliza su inmortalidad) o no
    public boolean isReady() {
        return ready;
    }

    @Override
    public void run() {
        while(true) {
            // Cuenta tres segundos
            while (i < 3) {
                if (start) {
                    try {
                        Thread.sleep(1000);
                        i++;
                    } catch (InterruptedException e) {}
                }
            }
            // Marca como finalizada su inmortalidad
            ready = true;
            // Desactiva la flag para que no se reinicie la cuenta atrás del bucle
            start = false;
            i = 0;
        }
    }

    public void finish() {
        i = 3;
        ready = true;
    }
}
