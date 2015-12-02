package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.base.IMessage;
import com.deltasolutions.dra.base.Message;
import org.jboss.netty.channel.Channel;

/**
 * Created by phil on 11/8/2015.
 */
public class ClientConnection {
    private Channel ch;
    private Channel failoverCh;
    private boolean failOver = false;
    private IMessage msg;
    private String destHost;

    ClientConnection(Channel ch, Channel failoverCh, IMessage msg, String failover) {
        this.ch = ch;
        this.failoverCh = failoverCh;
        this.msg = msg;
        destHost = "ups1";
    }

    public Channel getChannel() {
        return ch;
    }

    public Channel getFailoverChannel() {
        failOver = true;
        return failoverCh;
    }

    public String getFailoverDestHost() {
        return destHost;
    }

    public boolean isFailover() {
        return failOver;
    }

    public IMessage getMessage() {
        return msg;
    }
}
