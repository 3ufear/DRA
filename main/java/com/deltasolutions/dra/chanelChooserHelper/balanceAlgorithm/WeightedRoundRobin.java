package com.deltasolutions.dra.chanelChooserHelper.balanceAlgorithm;



import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by phil on 16-May-15.
 */
public class WeightedRoundRobin implements  BalanceAlgorithm {
    private List list;
    private AtomicInteger i = new AtomicInteger(0);

    public WeightedRoundRobin(List list) {
        this.list = list;
    }

    @Override
    public Channel getNextConnection() throws Exception {
        if (list.size() == 0) {
            throw new Exception(" ClientsConnectionsPool is empty");
        }
        int k = i.incrementAndGet();
        try {
            if (k != list.size()) {
                return (Channel) list.get(k);
            } else {
                i.set(0);
                return (Channel) list.get(0);
            }
        } catch (Exception e) {
             log(e.getMessage());
        }
        return null;
    }

    private void log(String log) {
        System.out.println("WeigtedRoundRobin: " + log);
    }
}
