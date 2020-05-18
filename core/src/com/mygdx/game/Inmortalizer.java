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

    // Method that initiates the counter
    public void count(){
        start = true;
        ready = false;
    }

    // Returns whether it's ready (ends its inmortality) or not
    public boolean isReady() {
        return ready;
    }

    @Override
    public void run() {
        while(true) {
            // Counts three seconds
            while (i < 3) {
                if (start) {
                    try {
                        Thread.sleep(1000);
                        i++;
                    } catch (InterruptedException e) {}
                }
            }
            // Flags its inmortality as finished
            ready = true;
            // Deactivates the flag so the loop countdown is not reset
            start = false;
            i = 0;
        }
    }

    public void finish() {
        i = 3;
        ready = true;
    }
}
