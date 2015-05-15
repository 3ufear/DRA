package com.deltasolutions.dra.tcp.handlers;

import com.deltasolutions.dra.base.Avp;
import com.deltasolutions.dra.base.IMessage;
import com.deltasolutions.dra.base.Message;
import com.deltasolutions.dra.base.ParseException;
import com.deltasolutions.dra.chanelChooserHelper.ChanelChooser;
import com.deltasolutions.dra.parser.AvpSetImpl;
import com.deltasolutions.dra.parser.MessageImpl;
import com.deltasolutions.dra.tcp.ClientConnectionsPool;
import com.deltasolutions.dra.tcp.Encoder.DiameterEncoder;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

import java.nio.ByteBuffer;

public class OutboundHandler extends SimpleChannelUpstreamHandler {

    private String originHost;
    private String originRealm;
    private int vendorId;
    private String productName;
    private int appId;
    private int resultCode;
    private Channel ch = null;
    private String name;

    private ChanelChooser channelChooser = ChanelChooser.getInstance();//ServerConnectionsPool.getInstance();
    private ClientConnectionsPool ClientChannel = ClientConnectionsPool.getInstance();

    public OutboundHandler() {
        System.out.println("OUTBOUNDHANDLER");
        this.resultCode = 2001;
        this.originHost = "192.168.0.149";
        this.originRealm = "vimpelcom.com";
        this.vendorId = 12414;
        this.productName = "DiamProxy";
        this.appId = 1414145;
    }

    public OutboundHandler(String name) {
        System.out.println("OUTBOUNDHANDLER");
        this.resultCode = 2001;
        this.originHost = "192.168.0.149";
        this.originRealm = "vimpelcom.com";
        this.vendorId = 12414;
        this.productName = "DiamProxy";
        this.appId = 1414145;
        this.name = name;
    }

    OutboundHandler(String originHost, String originRealm, int vendorId, String productName, int appId) {
        this.resultCode = 2001;
        this.originHost = originHost;
        this.originRealm = originRealm;
        this.vendorId = vendorId;
        this.productName = productName;
        this.appId = appId;
    }


    private ChannelBuffer CER_msg() throws ParseException {
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(Avp.ORIGIN_HOST, originHost, false);
        set.addAvp(Avp.ORIGIN_REALM, originRealm, false);
        set.addAvp(Avp.VENDOR_ID, vendorId);
        set.addAvp(Avp.RESULT_CODE, resultCode);
        set.addAvp(Avp.PRODUCT_NAME, productName, false);
        set.addAvp(Avp.AUTH_APPLICATION_ID, appId);
        set.addAvp(Avp.INBAND_SECURITY_ID, 0);
        IMessage msg = new MessageImpl(Message.CAPABILITIES_EXCHANGE_REQUEST, appId, (short) 0,  21414, 33252, set);
        msg.setRequest(true);
        return ChannelBuffers.wrappedBuffer(DiameterEncoder.parser.encodeMessage(msg));
    }


    private ChannelBuffer DWA_msg() throws ParseException {
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(Avp.ORIGIN_HOST, originHost, false);
        set.addAvp(Avp.ORIGIN_REALM, originRealm, false);
        set.addAvp(Avp.RESULT_CODE, resultCode);
        IMessage msg = new MessageImpl(Message.DEVICE_WATCHDOG_ANSWER, appId, (short) 0,  21414, 33252, set);
        return ChannelBuffers.wrappedBuffer(DiameterEncoder.parser.encodeMessage(msg));
    }

    public void send(ByteBuffer msg) {
        ChannelFuture f = ch.write(ChannelBuffers.wrappedBuffer(msg));
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws ParseException {
        System.out.println("Connected to server");
        this.ch = e.getChannel();
        channelChooser.getPoolByName(name).setConnection(ch);
        ch.write(CER_msg());
    }


    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e)
            throws Exception {
        IMessage msg = (IMessage) e.getMessage();
        System.out.println("<<< " + " <<< " + msg.getCommandCode() + "  SSID " + msg.getSessionId());
        switch (msg.getCommandCode()) {
            case Message.CREDIT_CONTROL_ANSWER:
                System.out.println("CCA ANSWER");
                ch = ClientChannel.getConnection(msg.getSessionId());
                System.out.println(ch);
                send(DiameterEncoder.parser.encodeMessage(msg));
                break;
            case Message.DEVICE_WATCHDOG_REQUEST:
                System.out.println("DWA ANSWER");
                e.getChannel().write(DWA_msg());
                break;
            case Message.DISCONNECT_PEER_REQUEST:
        }
      /*  if (msg.getCommandCode() == Message.CREDIT_CONTROL_ANSWER) {
            System.out.println("CCA ANSWER");
            ch = ClientChannel.getConnection(msg.getSessionId());
            System.out.println(ch);
            send(DiameterEncoder.parser.encodeMessage(msg));
        } else if(msg.getCommandCode() == Message.DEVICE_WATCHDOG_REQUEST) {
            System.out.println("DWA ANSWER");
            e.getChannel().write(DWA_msg());
        }*/
      //  synchronized (trafficLock) {
        /*    inboundChannel.write(ChannelBuffers.wrappedBuffer(DiameterEncoder.parser.encodeMessage(msg)));
            // If inboundChannel is saturated, do not read until notified in
            // HexDumpProxyInboundHandler.channelInterestChanged().
            if (!inboundChannel.isWritable()) {
               // e.getChannel().setReadable(false);
            }
      // }*/
    }

    @Override
    public void channelInterestChanged(ChannelHandlerContext ctx,
                                       ChannelStateEvent e) throws Exception {
        // If outboundChannel is not saturated anymore, continue accepting
        // the incoming traffic from the inboundChannel.
      // synchronized (trafficLock) {
            if (e.getChannel().isWritable()) {
             //   inboundChannel.setReadable(true);
            }
      ///  }
    }



    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        System.out.println("Server channel closed" + ctx.getChannel().getRemoteAddress() + " id = " + ctx.getChannel().getId());
       // closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        System.out.println("Server channel exception" + ctx.getChannel().getRemoteAddress() + " id = " + ctx.getChannel().getId());
        e.getCause().printStackTrace();
        closeOnFlush(e.getChannel());
    }
    static void closeOnFlush(Channel ch) {
        System.out.println("Server channel exception" + ch.getRemoteAddress() + " id = " + ch.getId());
        if (ch.isConnected()) {
            ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}

