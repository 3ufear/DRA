package com.deltasolutions.dra.chanelChooserHelper.balanceAlgorithm;

import org.jboss.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by phil on 16-May-15.
 */
public class RoundRobin implements  BalanceAlgorithm {
    private List list;
    private AtomicInteger i = new AtomicInteger(0);

    public RoundRobin(List list) {
        this.list = list;
    }

    @Override
    public Channel getNextConnection() throws Exception {
        int k = i.incrementAndGet();
        System.out.println("Get Chanel k = " + k);
        if (k != list.size()) {
            return (Channel) list.get(k);
        } else {
            i.set(0);
            if (list.get(0) != null) {
                return (Channel) list.get(0);
            } else {
                throw new Exception("Can not get connection: ClientConnectionPool is empty");
            }
        }
    }
}
