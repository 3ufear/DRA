package com.deltasolutions.dra.tcp.handlers;

import com.deltasolutions.dra.base.*;
import com.deltasolutions.dra.chanelChooserHelper.ChanelChooser;
import com.deltasolutions.dra.config.ProxyAgent;
import com.deltasolutions.dra.parser.AvpSetImpl;
import com.deltasolutions.dra.parser.MessageImpl;
import com.deltasolutions.dra.tcp.ClientConnection;
import com.deltasolutions.dra.tcp.ClientConnectionsPool;
import com.deltasolutions.dra.tcp.Encoder.DiameterEncoder;
import io.netty.channel.*;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashSet;

public class OutboundHandler extends ChannelInboundHandlerAdapter {

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
    private HashSet<Integer> errorCodes;
    private boolean _debug = true;
    private Thread DWAThread;

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
        this.errorCodes = ProxyAgent.errorResultCodes;
    }

    OutboundHandler(String originHost, String originRealm, int vendorId, String productName, int appId) {
        this.resultCode = 2001;
        this.originHost = originHost;
        this.originRealm = originRealm;
        this.vendorId = vendorId;
        this.productName = productName;
        this.appId = appId;
        this.errorCodes = ProxyAgent.errorResultCodes;
    }


    private ByteBuffer CER_msg() throws ParseException {
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(Avp.ORIGIN_HOST, originHost, false);
        set.addAvp(Avp.ORIGIN_REALM, originRealm, false);
        set.addAvp(Avp.ACCT_APPLICATION_ID, 0);
        //InetSocketAddress
//00010AA91144
//10.169.17.68
        Number[] num = new Number[2];
        num[0] = (short) 0x0001;
        num[1] = (int) 0x0AA91144;
        byte[] array = new byte[6];
        array[0] = 0x00;
        array[1] = 0x01;
        array[2] = 0x0A;
        array[3] = (byte) 0xA9;
        array[4] = 0x11;
        array[5] = 0x62;
        set.addAvp(Avp.HOST_IP_ADDRESS, array, true, false);
        set.addAvp(Avp.VENDOR_ID, 10415);
        set.addAvp(278, 365061546);
        set.addAvp(267, 0);
        set.addAvp(Avp.PRODUCT_NAME, productName, false);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 16777251);
        set.addAvp(Avp.INBAND_SECURITY_ID, 0);

        IMessage msg = new MessageImpl(Message.CAPABILITIES_EXCHANGE_REQUEST, 0, (short) 0,  HopByHop, EndToEnd, set);

        msg.setRequest(true);
        return (DiameterEncoder.parser.encodeMessage(msg));
    }


    private ByteBuffer DWA_msg(int HopByHop, int EndToEnd) throws ParseException {
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(Avp.ORIGIN_HOST, originHost, false);
        set.addAvp(Avp.ORIGIN_REALM, originRealm, false);
        set.addAvp(Avp.RESULT_CODE, resultCode);
        IMessage msg = new MessageImpl(Message.DEVICE_WATCHDOG_ANSWER, appId, (short) 0,  HopByHop, EndToEnd, set);
        return (DiameterEncoder.parser.encodeMessage(msg));
    }

    public void send(Channel ch, ByteBuffer msg) {
            ChannelFuture f = ch.write(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws ParseException {
        log("Connected to server");
        this.ch = ctx.channel();
        channelChooser.getPoolByName(name).setConnection(ch);
        ch.write(CER_msg());
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object ms)
            throws Exception {
        IMessage msg = (IMessage) ms;
        synchronized (ch) {
            switch (msg.getCommandCode()) {
                case Message.CREDIT_CONTROL_ANSWER:
                case Message.AA:
                case Message.ULAR_ANSWER:
                //    log("Credit Control or AA answer");
                    String session_id = msg.getSessionId();
                    ClientConnection clientConnection = ClientChannel.getConnection(session_id);
                    log("Message with session_id " + session_id);
                 //   log("Get Session ID");
                    if (clientConnection.isFailover()) {
                        Channel ch = clientConnection.getChannel();
                        send(ch, DiameterEncoder.parser.encodeMessage(msg));
                     //   log("Message Processing Finished");
                    } else {

                        if (errorCodes != null) {
                            int rs = msg.getAvps().getAvp(268).getInteger32();
                            if (errorCodes.contains(rs)) {
                                log("Failover to another connection");
                                Channel ch = clientConnection.getFailoverChannel();
                                //ch.write(ChannelBuffers.wrappedBuffer(DiameterEncoder.parser.encodeMessage(clientConnection.getMessage())));
                                IMessage message = clientConnection.getMessage();
                                AvpSetImpl set = (AvpSetImpl) message.getAvps();
                                set.removeAvp(283);
                                set.removeAvp(293);
                                set.addAvp(293, clientConnection.getFailoverDestHost().getBytes());
                                set.addAvp(283, "epc.mnc050.mcc250.3gppnetwork.org", false);
                                //int commandCode, long applicationId, short flags, long hopByHopId, long endToEndId, AvpSetImpl avpSet
                                IMessage mes = new MessageImpl(message.getCommandCode(), message.getHeaderApplicationId(),(short) message.getFlags(), message.getHopByHopIdentifier(), message.getEndToEndIdentifier(), set);
                               // set = (AvpSetImpl) mes.getAvps();
                               // set.addAvp(293, clientConnection.getFailoverDestHost().getBytes());
                                log("AVP DESTHOST = " + set.getAvp(293).getUTF8String());
                                send(ch, DiameterEncoder.parser.encodeMessage(mes));

                            } else {
                                Channel ch = clientConnection.getChannel();
                                send(ch, DiameterEncoder.parser.encodeMessage(msg));
                             //   log("Message Processing Finished");
                            }
                        } else {

                        //    log("get Ch connection");
                            Channel ch = clientConnection.getChannel();
                        //    log("Sending Message");

                            send(ch, DiameterEncoder.parser.encodeMessage(msg));
                       //     log("Message Processing Finished");
                        }
                    }
                    break;
                case Message.DEVICE_WATCHDOG_REQUEST:
                    if (msg.isRequest()) {
                        log("DWA ANSWER " + ctx.channel().remoteAddress());
                        ctx.channel().write(DWA_msg((int) msg.getHopByHopIdentifier(), (int) msg.getEndToEndIdentifier()));
                    }
                    break;
                case Message.DISCONNECT_PEER_REQUEST:
                //    log("DPA ANSWER");
                    break;
                case Message.CAPABILITIES_EXCHANGE_ANSWER:
                 //   log("CER ANSWER");
                    DWAThread = new Thread(new DewiceWatchDogMessage(ctx.channel()));
                    DWAThread.start();

            }
        }
}


    @Override
    public void channelInactive(ChannelHandlerContext ctx)
            throws Exception {
        log("Server channel closed " + ctx.channel().remoteAddress());
        log("Stopping DWA Requests");
        DWAThread.interrupt();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
            throws Exception {

        log("Server channel exception " + e  + " adde " + ctx.channel().remoteAddress());
/*        try {
            throw e.getCause();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
       */
    }

    private void log(String txt) {
        if (_debug) {
            Date curdate = new Date();
            System.out.print(curdate + " OutboundHandler: " + txt + "\n");
        }
    }
}

