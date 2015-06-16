package ConditionTest;

import com.deltasolutions.dra.chanelChooserHelper.Conditions.Condition;
import com.deltasolutions.dra.chanelChooserHelper.Conditions.EqualsCondition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by phil on 10-Jun-15.
 */
public class EquialsCondition extends Assert {
    static Condition cond;
    @Before
    public void createCondition() {
        cond = new EqualsCondition();
    }

    @Test
    public void test() {
        boolean res = cond.CheckCondition(1,1);
        assertTrue(res);
        res = cond.CheckCondition(1,2);
        assertFalse(res);
        res = cond.CheckCondition("qq","qq");
        assertTrue(res);
        res = cond.CheckCondition("32","wrw");
        assertFalse(res);
    }
}
