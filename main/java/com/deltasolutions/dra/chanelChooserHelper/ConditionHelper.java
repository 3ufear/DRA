package com.deltasolutions.dra.chanelChooserHelper;

import com.deltasolutions.dra.base.AvpDataException;
import com.deltasolutions.dra.base.AvpSet;
import com.deltasolutions.dra.chanelChooserHelper.Conditions.*;
import com.deltasolutions.dra.config.ConfigCondition;

/**
 * Created by phil on 20-May-15.
 */
public class ConditionHelper {
    private int firstValueAvpIndex;
    private String secondValue;
    private Condition condition;
    private String upstreamName;

    public ConditionHelper(int firstValueAvpIndex, String secondValue, int conditionIndex,String upstreamName) {
        this.firstValueAvpIndex = firstValueAvpIndex;
        this.secondValue = secondValue;
        this.upstreamName = upstreamName;
        CreateCondition(conditionIndex);
    }

    private void CreateCondition(int conditionIndex) {
        switch (conditionIndex) {
            case ConfigCondition.LESS_CONDITION:
                condition = new LessCondition();
                break;
            case ConfigCondition.LESS_OR_EQUIALS_CONDITION:
                condition = new LessOrEqualsCondition();
                break;
            case ConfigCondition.MORE_CONDITION:
                condition = new MoreCondition();
                break;
            case ConfigCondition.MORE_OR_EQUIALS_CONDITION:
                condition = new MoreOrEqualsCondition();
                break;
            case ConfigCondition.EQUIALS_STRING:
            case ConfigCondition.EQUIALS_CONDITION:
                condition = new EqualsCondition();
                break;
            case ConfigCondition.NOT_EQUIALS_STRING:
            case ConfigCondition.NOT_EQUIALS_CONDITION:
                condition = new NotEqualsCondition();
                break;
        }
    }

    public ConditionHelper(ConfigCondition configCondition) {
        this.firstValueAvpIndex = configCondition.getAvpCode();
        this.secondValue = configCondition.getConditionValue();
        this.upstreamName = configCondition.getConditionResultName();
        CreateCondition(configCondition.getCondititionCode());
    }

    public boolean checkCondition(AvpSet set) throws AvpDataException {
        return condition.CheckCondition(set.getAvp(firstValueAvpIndex).getUTF8String(), secondValue);
    }

    public String getUpstreamName() {
        return this.upstreamName;
    }

}
