package com.deltasolutions.dra.config;

/**
 * Created by phil on 15-May-15.
 */
public class ConfigCondition {
    public static final int LESS_CONDITION = 1; //<
    public static final int LESS_OR_EQUIALS_CONDITION = 2; //<=
    public static final int EQUIALS_CONDITION = 3;// ==
    public static final int MORE_OR_EQUIALS_CONDITION = 4;//>=
    public static final int MORE_CONDITION = 5;//>
    public static final int NOT_EQUIALS_CONDITION = 6;// !=
    public static final int EQUIALS_STRING = 7;//eq
    public static final int NOT_EQUIALS_STRING = 8;//ne;

    public int getAvpCode() {
        return avpCode;
    }

    public int getCondititionCode() {
        return condititionCode;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    private int avpCode = 0;

    private int condititionCode = 0;

    private String conditionValue = null;

    private String conditionResultName = null;

    public ConfigCondition(int avpCode, int conditionCode, String conditionValue, String conditionResultName) {
        this.avpCode = avpCode;
        this.condititionCode = conditionCode;
        this.conditionValue = conditionValue;
        this.conditionResultName = conditionResultName;
    }

    public ConfigCondition(int avpCode, String conditionCode, String conditionValue, String conditionResultName) {
        this.avpCode = avpCode;
      //  this.condtitionCode = conditionCode;
        this.conditionValue = conditionValue;
        this.conditionResultName = conditionResultName;
        if (conditionCode.equals("<")) {
            this.condititionCode = LESS_CONDITION;
        } else if (conditionCode.equals("<=")) {
            this.condititionCode = LESS_OR_EQUIALS_CONDITION;
        } else if (conditionCode.equals("==")) {
            this.condititionCode = EQUIALS_CONDITION;
        }else if (conditionCode.equals(">=")) {
            this.condititionCode = MORE_OR_EQUIALS_CONDITION;
        }else if (conditionCode.equals(">")) {
            this.condititionCode = MORE_CONDITION;
        }else if (conditionCode.equals("!=")) {
            this.condititionCode = NOT_EQUIALS_CONDITION;
        }else if (conditionCode.equals("eq")) {
            this.condititionCode = EQUIALS_STRING;
        }else if (conditionCode.equals("ne")) {
            this.condititionCode = NOT_EQUIALS_STRING;
        }
    }

    public String getConditionResultName() {
        return conditionResultName;
    }

}
