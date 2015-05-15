package com.deltasolutions.dra.tcp;

import org.jboss.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by phil on 13-May-15.
 */
public class ServerConnectionsPool {
    private static ServerConnectionsPool instance = null;
    private int i = 1;
    private List<Channel> ServerConnections = new ArrayList<Channel>();

    private ServerConnectionsPool() {

    }

    public Channel getConnection() {

        i++;
        if (i >= ServerConnections.size()) {
            i = 0;
        }
        return ServerConnections.get(i);
    }
    public void setConnection(Channel ch) {
        setConnection(ch, 1);
    }


    public void setConnection(Channel ch, int num ) { //Добавляет connection n раз для roundrobindns;
        for (int k = 0;k < num; k++) {
            ServerConnections.add(ch);
        }
        Collections.shuffle(this.ServerConnections);
    }

    public static ServerConnectionsPool getInstance() {
        if (instance == null) {
            instance = new ServerConnectionsPool();
        }
        return instance;

    }


}