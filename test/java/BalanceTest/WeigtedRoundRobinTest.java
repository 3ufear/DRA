package BalanceTest;

import com.deltasolutions.dra.chanelChooserHelper.balanceAlgorithm.BalanceAlgorithm;
import com.deltasolutions.dra.chanelChooserHelper.balanceAlgorithm.WeightedRoundRobin;
import org.jboss.netty.channel.Channel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 10-Jun-15.
 */
public class WeigtedRoundRobinTest extends Assert {
    public static List<Channel> list = new ArrayList<Channel>();
    public static BalanceAlgorithm balance = new WeightedRoundRobin(list);

    @Before
    public void createCondition() {
        Channel ch = null;
        list.add(ch);
    }

    @Test
    public void test() {
        try {
           Channel ch =  balance.getNextConnection();
           assertNull(ch);
        } catch (Exception e) {
        }
    }

}
