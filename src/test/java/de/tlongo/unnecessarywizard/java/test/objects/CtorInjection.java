package de.tlongo.unnecessarywizard.java.test.objects;

import java.math.BigDecimal;

/**
 * Created by tolo on 11.07.2014.
 */
public class CtorInjection {
    String field;
    SingleInterface singleInterface;
    float floatValue;
    double doubleValue;
    int intValue;
    long longValue;
    BigDecimal bd;

    public CtorInjection(String field,
                         SingleInterface singleInterface,
                         float floatValue,
                         double doubleValue,
                         int intValue,
                         long longValue,
                         BigDecimal bd) {
        this.field = field;
        this.singleInterface = singleInterface;
        this.floatValue = floatValue;
        this.doubleValue = doubleValue;
        this.intValue = intValue;
        this.longValue = longValue;
        this.bd = bd;
    }

    public String getField() {
        return field;
    }

    public SingleInterface getSingleInterface() {
        return singleInterface;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public BigDecimal getBd() {
        return bd;
    }
}
