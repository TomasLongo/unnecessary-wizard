package de.tlongo.unnecessarywizard.java.test.objects;

/**
 * Created by tolo on 16.05.2014.
 */
public class Hello implements TestInterface {

    @Override
    public String produceSomeString() {
        return Hello.class.getName();
    }
}
