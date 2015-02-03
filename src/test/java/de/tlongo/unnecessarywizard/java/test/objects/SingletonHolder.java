package de.tlongo.unnecessarywizard.java.test.objects;

/**
 * Created by tomas on 27.01.15.
 */
public class SingletonHolder {
    Singleton singleton;

    public SingletonHolder() {

    }

    public SingletonHolder(Singleton singleton) {
        this.singleton = singleton;
    }

    public void setSingleton(Singleton singleton) {
        this.singleton = singleton;
    }

    public Singleton getSingleton() {
        return singleton;
    }
}
