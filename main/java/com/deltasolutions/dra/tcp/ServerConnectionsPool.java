package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.chanelChooserHelper.balanceAlgorithm.BalanceAlgorithm;
import com.deltasolutions.dra.chanelChooserHelper.balanceAlgorithm.RoundRobin;
import org.jboss.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 13-May-15.
 */
public class ServerConnectionsPool {
    private int i = 1;
    private List<Channel> ServerConnections = new ArrayList<Channel>();
    private BalanceAlgorithm balance = new RoundRobin(ServerConnections);

    public ServerConnectionsPool() {

    }

    public Channel getConnection() {
        try {
            return balance.getNextConnection();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return null;
        }
    }
    public void setConnection(Channel ch) {
        setConnection(ch, 1);
    }


    public void setConnection(Channel ch, int num ) { //Добавляет connection n раз для roundrobindns;
        for (int k = 0;k < num; k++) {
            ServerConnections.add(ch);
        }
      //  Collections.shuffle(this.ServerConnections);
    }



}
