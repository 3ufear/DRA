package com.deltasolutions.dra.chanelChooserHelper.Conditions;

/**
 * Created by phil on 20-May-15.
 */
public interface Condition {

    public boolean CheckCondition(String firstParameter, String secondParameter);
    public boolean CheckCondition(int firstParameter, int secondParameter);

}
