package com.deltasolutions.dra.chanelChooserHelper;

/**
 * Created by phil on 15-May-15.
 */
public class Condition {
    public static int LESS_CONDITION = 1; //<
    public static int LESS_OR_EQUIALS_CONDITION = 2; //<=
    public static int EQUIALS_CONDITION = 3;// ==
    public static int MORE_OR_EQUIALS_CONDITION = 4;//>=
    public static int MORE_CONDITION = 5;//>
    public static int NOT_EQUIALS_CONDITION = 6;// !=
    public static int EQUIALS_STRING = 7;//eq
    public static int NOT_EQUIALS_STRING = 8;//neq;

    private int avpCode = 0;

    private int condtitionCode = 0;

    private String conditionValue = null;

    public Condition(int avpCode, String conditionCode, String conditionValue) {
        this.avpCode = avpCode;
        this.condtitionCode = condtitionCode;
        this.conditionValue = conditionValue;
    }


}
