package de.tlongo.unnecessarywizard.java.test.objects;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by tolo on 07.05.2014.
 */
public class ComplexObject {
    BigDecimal decimalValue;
    List list;

    public BigDecimal getDecimalValue() {
        return decimalValue;
    }

    public void setDecimalValue(BigDecimal decimalValue) {
        this.decimalValue = decimalValue;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
