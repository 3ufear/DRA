package com.deltasolutions.dra.chanelChooserHelper.Conditions;

/**
 * Created by phil on 20-May-15.
 */
public class NotEqualsCondition implements Condition {
    @Override
    public boolean CheckCondition(String firstParameter, String secondParameter) {
        if (!(firstParameter.equals(secondParameter)))
            return true;
        else
            return false;
    }

    @Override
    public boolean CheckCondition(int firstParameter, int secondParameter) {
        if (firstParameter != secondParameter)
            return true;
        else
            return false;
    }
}