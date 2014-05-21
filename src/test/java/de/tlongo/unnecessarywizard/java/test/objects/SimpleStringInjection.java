package de.tlongo.unnecessarywizard.java.test.objects;

/**
 * Created by tolo on 26.04.2014.
 */
public class SimpleStringInjection {
    String fieldToInject;
    String secondField;
    String thirdField;

    public String getSecondField() {
        return secondField;
    }

    public String getThirdField() {
        return thirdField;
    }

    public String getFieldToInject() {
        return fieldToInject;
    }

    public void setFieldToInject(String fieldToInject) {
        this.fieldToInject = fieldToInject;
    }

    public void setSecondField(String secondField) {
        this.secondField = secondField;
    }

    public void setThirdField(String thirdField) {
        this.thirdField = thirdField;
    }
}
