package com.deltasolutions.dra.tcp.handlers;

import com.deltasolutions.dra.base.Avp;
import com.deltasolutions.dra.base.IMessage;
import com.deltasolutions.dra.base.Message;
import com.deltasolutions.dra.base.ParseException;
import com.deltasolutions.dra.chanelChooserHelper.ChanelChooser;
import com.deltasolutions.dra.config.ProxyAgent;
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
    private int HopByHop = 123;
    private int EndToEnd = 36363;
    private boolean _debug = true;

    private ChanelChooser channelChooser = ChanelChooser.getInstance();//ServerConnectionsPool.getInstance();
    private ClientConnectionsPool ClientChannel = ClientConnectionsPool.getInstance();


    public OutboundHandler(String name) {
        this.resultCode = 2001;
        this.originHost = ProxyAgent.originHost;
        this.originRealm = ProxyAgent.originRealm;
        this.vendorId = ProxyAgent.vendorId;
        this.productName = ProxyAgent.productName;
        this.appId = ProxyAgent.appId;
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
        IMessage msg = new MessageImpl(Message.CAPABILITIES_EXCHANGE_REQUEST, appId, (short) 0,  HopByHop, EndToEnd, set);
        msg.setRequest(true);
        return ChannelBuffers.wrappedBuffer(DiameterEncoder.parser.encodeMessage(msg));
    }


    private ChannelBuffer DWA_msg() throws ParseException {
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(Avp.ORIGIN_HOST, originHost, false);
        set.addAvp(Avp.ORIGIN_REALM, originRealm, false);
        set.addAvp(Avp.RESULT_CODE, resultCode);
        IMessage msg = new MessageImpl(Message.DEVICE_WATCHDOG_ANSWER, appId, (short) 0,  HopByHop, EndToEnd, set);
        return ChannelBuffers.wrappedBuffer(DiameterEncoder.parser.encodeMessage(msg));
    }

    public void send(ByteBuffer msg) {
            ChannelFuture f = ch.write(ChannelBuffers.wrappedBuffer(msg));
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws ParseException {
        log("Connected to server");
        this.ch = e.getChannel();
        channelChooser.getPoolByName(name).setConnection(ch);
        ch.write(CER_msg());
    }


    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e)
            throws Exception {
        IMessage msg = (IMessage) e.getMessage();
        log("<<<<<< " + msg.getCommandCode() + "  SSID " + msg.getSessionId());
        switch (msg.getCommandCode()) {
            case Message.CREDIT_CONTROL_ANSWER:
                log("CCA ANSWER");
                ch = ClientChannel.getConnection(msg.getSessionId());
                send(DiameterEncoder.parser.encodeMessage(msg));
                break;
            case Message.DEVICE_WATCHDOG_REQUEST:
                log("DWA ANSWER");
                e.getChannel().write(DWA_msg());
                break;
            case Message.DISCONNECT_PEER_REQUEST:
                log("DPA ANSWER");
        }
}


    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        log("Server channel closed " + ctx.getChannel().getRemoteAddress() + " id = " + ctx.getChannel().getId());
       // closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        log("Server channel exception" + ctx.getChannel().getRemoteAddress() + e.getCause() + " id = " + ctx.getChannel().getId());
      //  closeOnFlush(e.getChannel());
    }
    static void closeOnFlush(Channel ch) {
       // log("Server channel exception" + ch.getRemoteAddress() + " id = " + ch.getId());
        if (ch.isConnected()) {
            ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void log(String txt) {
        if (_debug) {
            System.out.print("OutboundHandler: " + txt + "\n");
        }
    }
}

