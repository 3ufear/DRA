package com.deltasolutions.dra.tcp;

import org.jboss.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by phil on 14-May-15.
 */
public class ClientConnectionsPool {
    private static ClientConnectionsPool instance = null;
    private Map<String,Channel> ClientConnections = new HashMap<String, Channel>();

    private ClientConnectionsPool() {

    }

    public Channel getConnection(String key) {
        return ClientConnections.get(key);
    }

    public void setConnection(String key,Channel ch) {
        ClientConnections.put(key,ch);
    }

    public static ClientConnectionsPool getInstance() {
        if (instance == null) {
            instance = new ClientConnectionsPool();
        }
        return instance;

    }
}
