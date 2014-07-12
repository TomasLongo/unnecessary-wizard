package de.tlongo.unnecessarywizard.java.test.objects;

/**
 * Created by tolo on 11.07.2014.
 */
public class CtorInjection {
    String field;
    SingleInterface singleInterface;

    public CtorInjection(String field, SingleInterface singleInterface) {
        this.field = field;
        this.singleInterface = singleInterface;
    }

    public String getField() {
        return field;
    }

    public SingleInterface getSingleInterface() {
        return singleInterface;
    }
}
