package com.deltasolutions.dra.tcp.handlers;

import com.deltasolutions.dra.base.Avp;
import com.deltasolutions.dra.base.IMessage;
import com.deltasolutions.dra.base.Message;
import com.deltasolutions.dra.base.ParseException;
import com.deltasolutions.dra.config.Config;
import com.deltasolutions.dra.config.ProxyAgent;
import com.deltasolutions.dra.parser.AvpSetImpl;
import com.deltasolutions.dra.parser.MessageImpl;
import com.deltasolutions.dra.tcp.Encoder.DiameterEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.channel.Channel;

import java.nio.ByteBuffer;


/**
 * Created by phil on 11/10/2015.
 */
public class DewiceWatchDogMessage implements Runnable {
    private int HopByHop = 123;
    private int EndToEnd = 36363;
    Channel ch;
    DewiceWatchDogMessage(Channel ch) {
    this.ch = ch;
    }
    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                try {
                    System.out.println("Sending Device WatchDog request");
                    HopByHop++;
                    EndToEnd++;
                    ch.write(DWA_msg());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ByteBuffer DWA_msg() throws ParseException {
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(Avp.ORIGIN_HOST, ProxyAgent.originHost, false);
        set.addAvp(Avp.ORIGIN_REALM, ProxyAgent.originRealm, false);


        IMessage msg = new MessageImpl(Message.DEVICE_WATCHDOG_REQUEST, 16777251, (short) 0,  HopByHop, EndToEnd, set);
        msg.setRequest(true);

        return  DiameterEncoder.parser.encodeMessage(msg);
    }
}
