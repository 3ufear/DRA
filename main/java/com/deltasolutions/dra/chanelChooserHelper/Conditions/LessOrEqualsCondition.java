package com.deltasolutions.dra.chanelChooserHelper.Conditions;

/**
 * Created by phil on 20-May-15.
 */
public class LessOrEqualsCondition implements Condition {

    @Override
    public boolean CheckCondition(String firstParameter, String secondParameter) {
        if (Integer.parseInt(firstParameter) <= Integer.parseInt(secondParameter))
            return true;
        else
            return false;
    }

    @Override
    public boolean CheckCondition(int firstParameter, int secondParameter) {
        if (firstParameter <= secondParameter)
            return true;
        else
            return false;
    }
}
