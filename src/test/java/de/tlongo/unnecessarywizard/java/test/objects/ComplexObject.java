package de.tlongo.unnecessarywizard.java.test.objects;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by tolo on 07.05.2014.
 */
public class ComplexObject {
    BigDecimal decimalValue;
    List list;
    SampleClassToInject sampleClass;
    SingleInterface singleInterface;
    TestInterface testInterface;

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

    public SampleClassToInject getSampleClass() {
        return sampleClass;
    }

    public void setSampleClass(SampleClassToInject sampleClass) {
        this.sampleClass = sampleClass;
    }

    public SingleInterface getSingleInterface() {
        return singleInterface;
    }

    public void setSingleInterface(SingleInterface singleInterface) {
        this.singleInterface = singleInterface;
    }

    public TestInterface getTestInterface() {
        return testInterface;
    }

    public void setTestInterface(TestInterface testInterface) {
        this.testInterface = testInterface;
    }
}
