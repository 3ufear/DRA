package com.deltasolutions.dra.chanelChooserHelper;

import com.deltasolutions.dra.config.Upstream;
import com.deltasolutions.dra.tcp.ServerConnectionsFactory;
import com.deltasolutions.dra.tcp.ServerConnectionsPool;

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

    public ChanelChooser() {

    }

    public void processUpstreams(List<Upstream> upstreams) {
        Iterator it= upstreams.iterator();
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
}
