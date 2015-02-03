package de.tlongo.unnecessarywizard.java.test.objects;

/**
 * Created by tomas on 27.01.15.
 */

/**
 * This class is supposed to be instantiated only once by the wizard.
 * Any further injection of this class should always return the same instance
 */
public class Singleton {
    static int id = 0;

    int myId;

    public Singleton() {
        myId = id++;
    }

    public int getMyId() {
        return myId;
    }
}
