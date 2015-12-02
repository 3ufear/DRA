package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.base.IMessage;
import org.jboss.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by phil on 14-May-15.
 */
public class ClientConnectionsPool {
    private static ClientConnectionsPool instance = null;
    private Map<String,ClientConnection> ClientConnections = new HashMap<String, ClientConnection>();

    private ClientConnectionsPool() {

    }

    synchronized public ClientConnection getConnection(String key) {
        log("getConnections");
        return ClientConnections.get(key);
    }

    synchronized public void setConnection(String key, Channel ch, Channel failoverChannel, IMessage message, String failover) {
        ClientConnections.put(key,new ClientConnection(ch, failoverChannel, message, failover ));
    }

    public synchronized static ClientConnectionsPool getInstance() {
        if (instance == null) {
            instance = new ClientConnectionsPool();
        }
        return instance;

    }

    private synchronized void log(String log) {
        System.out.println("ClientConnectionsPool: " + log);
    }
}
