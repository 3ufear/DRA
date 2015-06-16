package ConditionTest;

import com.deltasolutions.dra.chanelChooserHelper.Conditions.Condition;
import com.deltasolutions.dra.chanelChooserHelper.Conditions.NotEqualsCondition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by phil on 10-Jun-15.
 */
public class NotEquialsCondition extends Assert {
    static Condition cond;
    @Before
    public void createCondition() {
        cond = new NotEqualsCondition();
    }

    @Test
    public void test() {
        boolean res = cond.CheckCondition(1,1);
        assertFalse(res);
        res = cond.CheckCondition(1,2);
        assertTrue(res);
        res = cond.CheckCondition("qq","qq");
        assertFalse(res);
        res = cond.CheckCondition("32","wrw");
        assertTrue(res);
    }
}
