package ChannelChooserTest;

import com.deltasolutions.dra.base.AvpDataException;
import com.deltasolutions.dra.chanelChooserHelper.ConditionHelper;
import com.deltasolutions.dra.config.ConfigCondition;
import com.deltasolutions.dra.parser.AvpSetImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by phil on 10-Jun-15.
 */
public class ChannelChooserTest  extends Assert {
    private List<ConditionHelper> list = new ArrayList<ConditionHelper>();
    @Before
    public void createCondition() {
        ConfigCondition cond = new ConfigCondition(1,3,"4","TestName");
        list.add(new ConditionHelper(cond));
    }

    @Test
    public void test() {
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(1,4,true);
        Iterator it = list.iterator();
        ConditionHelper cond;
        while (it.hasNext()) {
            cond = (ConditionHelper) it.next();
            try {
                    if (cond.checkCondition(set)) {
                    assertEquals("TestName", cond.getUpstreamName());
                }
            } catch (AvpDataException e) {
                e.printStackTrace();
            }
        }

    }
}
