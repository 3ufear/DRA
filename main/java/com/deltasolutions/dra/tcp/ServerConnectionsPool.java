package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.chanelChooserHelper.balanceAlgorithm.BalanceAlgorithm;
import com.deltasolutions.dra.chanelChooserHelper.balanceAlgorithm.WeightedRoundRobin;
import org.jboss.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 13-May-15.
 */
public class ServerConnectionsPool {
    private List<Channel> ServerConnections = new ArrayList<Channel>();
    private BalanceAlgorithm balance = new WeightedRoundRobin(ServerConnections);
    private String failoverUpstream;

    public ServerConnectionsPool() {

    }

    public Channel getConnection () throws Exception {
            return balance.getNextConnection();
    }
    public void setConnection(Channel ch) {
        setConnection(ch, 1);
    }


    public void setConnection(Channel ch, int num ) { //Добавляет connection n раз для weightedroundrobin;
        for (int k = 0;k < num; k++) {
            ServerConnections.add(ch);
        }
      //  Collections.shuffle(this.ServerConnections);
    }
    private void log(String log) {
        System.out.println("ServerConnectionsPool: " + log);
    }
    public void setFailoverConnectionName(String name) {
        failoverUpstream = name;
    }



    public String getFailoverUpstream() {
        return failoverUpstream;
    }

}
