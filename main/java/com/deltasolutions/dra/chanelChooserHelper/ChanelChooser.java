package com.deltasolutions.dra.chanelChooserHelper;

import com.deltasolutions.dra.base.AvpDataException;
import com.deltasolutions.dra.base.AvpSet;
import com.deltasolutions.dra.config.ConfigCondition;
import com.deltasolutions.dra.config.Upstream;
import com.deltasolutions.dra.tcp.ServerConnectionsFactory;
import com.deltasolutions.dra.tcp.ServerConnectionsPool;
import org.jboss.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

;

/**
 * Created by phil on 15-May-15.
 */
public class ChanelChooser {
    private String defaultName = null;
    private static ChanelChooser instance = null;
    HashMap<String, ServerConnectionsPool> map = new HashMap<String, ServerConnectionsPool>();
    private List<ConditionHelper> conditionList = new ArrayList<ConditionHelper>();


    public ChanelChooser() {}

    public void processUpstreams(List<Upstream> upstreams, List<ConfigCondition> configConditions) {
        Iterator conditionIterator = configConditions.listIterator();
        while (conditionIterator.hasNext()) {
            conditionList.add(new ConditionHelper((ConfigCondition) conditionIterator.next()));
        }
        Iterator it = upstreams.iterator();
        while (it.hasNext()) {
            Upstream up =(Upstream) it.next();
            if (up.isActive()) {
                ServerConnectionsPool serverpool = new ServerConnectionsPool();
                map.put(up.getName(),serverpool);
                if (up.isDefault()) {
                     this.defaultName = up.getName();
                }
                for (int i = 0; i < up.getHosts().length; i++) {
                    ServerConnectionsFactory factory = new ServerConnectionsFactory(up.getHosts()[i], up.getName());
                    factory.start();
                }
            }
        }
    }

    public ServerConnectionsPool getPoolByName(String name) {
        return map.get(name);
    }

    public static ChanelChooser getInstance() {
        if (instance == null) {
            instance = new ChanelChooser();
        }
        return instance;
    }

    public Channel chooseChannel(AvpSet avps) throws AvpDataException {

        if (conditionList.size() == 0) {
            //System.out.println(map.get(this.defaultName).toString());
            return map.get(this.defaultName).getConnection();
        } else {
            Iterator it = conditionList.iterator();
            ConditionHelper cond;
            while (it.hasNext()) {
                cond = (ConditionHelper) it.next();
                if (cond.checkCondition(avps)) {
                    return map.get(cond.getUpstreamName()).getConnection();
                }
            }
        }
        return null;
    }
}
