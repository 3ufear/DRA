package ConditionTest;

import com.deltasolutions.dra.chanelChooserHelper.Conditions.Condition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by phil on 10-Jun-15.
 */
public class MoreCondition extends Assert {
    static Condition cond;
    @Before
    public void createCondition() {
        cond = (Condition) new  com.deltasolutions.dra.chanelChooserHelper.Conditions.MoreCondition();
    }

    @Test
    public void test() {
        boolean res = cond.CheckCondition(2,1);
        assertTrue(res);
        res = cond.CheckCondition(2,2);
        assertFalse(res);
        res = cond.CheckCondition("12","11");
        assertTrue(res);
        res = cond.CheckCondition("11","12");
        assertFalse(res);
    }
}
