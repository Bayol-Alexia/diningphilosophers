package diningphilosophers;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChopStick {

    private final Lock verrou = new ReentrantLock();
    private final Condition dispo = verrou.newCondition();
    private static int stickCount = 0;
    private boolean iAmFree = true;
    private final int myNumber;

    public ChopStick() {
        myNumber = ++stickCount;
    }

    public boolean tryTake(int delay) throws InterruptedException {
        verrou.lock();
        try {
            if (!iAmFree) {
                wait(delay);
                if (!iAmFree) // Toujours pas libre, on abandonne
                {
                    return false; // Echec
                }
            }
            iAmFree = false;
        } finally {
            verrou.lock();
        }
        // Pas utile de faire notifyAll ici, personne n'attend qu'elle soit occupée
        return true; // Succès
    }

    public void release() {
        iAmFree = true;
        verrou.lock();
        try{
            dispo.signalAll();
        } finally {
            verrou.unlock();
        }
        System.out.println("Stick " + myNumber + " Released");
    }

    @Override
    public String toString() {
        return "Stick#" + myNumber;
    }
}
