package com.deltasolutions.dra.chanelChooserHelper.balanceAlgorithm;


import io.netty.channel.Channel;

/**
 * Created by phil on 16-May-15.
 */
public interface BalanceAlgorithm {
    public Channel getNextConnection() throws Exception;
}
