package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.base.*;
import com.deltasolutions.dra.chanelChooserHelper.ChanelChooser;
import com.deltasolutions.dra.config.ProxyAgent;
import com.deltasolutions.dra.parser.AvpSetImpl;
import com.deltasolutions.dra.parser.MessageImpl;
import com.deltasolutions.dra.tcp.Encoder.DiameterEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Date;

public class CommandProcessor  {

	private NetContext _nCtx;
	private boolean _debug;
    String originHost = "drax";
    String originRealm = "drax.realm";
    int resultCode = 2001;
    int vendor_id = ProxyAgent.vendorId;
    String product_name = ProxyAgent.productName;
    int appId = ProxyAgent.appId;
    int InbandSecurityId = 0;
    String failoverName = "ups1";
    ChanelChooser channelChooser = ChanelChooser.getInstance();//ServerConnectionsPool.getInstance();
    ClientConnectionsPool ClientChannels = ClientConnectionsPool.getInstance();



    public CommandProcessor(String name, NetContext nCtx, boolean debug) {
		super();
	//	setName(name);
		_nCtx = nCtx;
		_debug = true;
	}

    private ByteBuffer DWA_msg() throws ParseException {
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(Avp.ORIGIN_HOST, originHost, false);
        set.addAvp(Avp.ORIGIN_REALM, originRealm, false);
        set.addAvp(Avp.RESULT_CODE, resultCode);
        IMessage msg = new MessageImpl(Message.DEVICE_WATCHDOG_ANSWER, appId, (short) 0,  21414, 33252, set);
        return DiameterEncoder.parser.encodeMessage(msg);
    }

    private IMessage CEA_msg() throws ParseException {
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(264, originHost, false);
        set.addAvp(296, originRealm, false);
        set.addAvp(268, resultCode);
        set.addAvp(266, vendor_id);
        set.addAvp(269, product_name, false);
        set.addAvp(258, appId);
        set.addAvp(299, InbandSecurityId);
        set.addAvp(257, "10.169.17.11".getBytes());
        set.addAvp(267, 1);
        set.addAvp(265, 10415);
        set.addAvp(259, "Diameter Base Accounting".getBytes());
        set.addAvp(Avp.AUTH_APPLICATION_ID, 0);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 4);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 16777238);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 16777236);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 16777266);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 16777251);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 16777252);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 16777267);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 16777217);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 16777216);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 16777302);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 16777303);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 10);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 16777222);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 3);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 318);
        set.addAvp(Avp.AUTH_APPLICATION_ID, 55557);
        IMessage msg = new MessageImpl(Message.CAPABILITIES_EXCHANGE_ANSWER, appId, (short) 0,  _nCtx.message.getHopByHopIdentifier(), _nCtx.message.getEndToEndIdentifier(), set);
        //IMessage msg = (IMessage) _nCtx.message.createAnswer();
        return  msg;
    }

    private ByteBuffer DPA_msg() throws ParseException {
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(264, originHost, false);
        set.addAvp(296, originRealm, false);
        set.addAvp(268, resultCode);
        IMessage msg = new MessageImpl(Message.DISCONNECT_PEER_ANSWER, appId, (short) 0,  _nCtx.message.getHopByHopIdentifier(), _nCtx.message.getEndToEndIdentifier(), set);
        return DiameterEncoder.parser.encodeMessage(msg);
    }

    private ByteBuffer CCA_msg(MessageImpl msg) throws ParseException {
        IMessage message = new MessageImpl(msg);
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(264, originHost, false);
        set.addAvp(296, originRealm, false);
        set.addAvp(268, resultCode);
        message.setRequest(false);
        return DiameterEncoder.parser.encodeMessage(message);
    }
	public void run() throws Exception {
       try {

            switch (_nCtx.message.getCommandCode()) {
                case Message.CAPABILITIES_EXCHANGE_REQUEST:
                    log("CER Request");
                    DiameterEncoder.parser.encodeMessage(CEA_msg()).array();

                    _nCtx.channel.writeAndFlush(CEA_msg());

                    break;
                case Message.DEVICE_WATCHDOG_REQUEST:
                    log("DWR Request " + _nCtx.channel.channel().remoteAddress());
                    _nCtx.channel.write(DWA_msg());
                    break;
                case Message.CREDIT_CONTROL_REQUEST:
                case Message.AA:
                case Message.ULAR_REQUEST:
                    String session_id = _nCtx.message.getSessionId();
                    log("Message with session_id " + session_id);
                   // log("Processing statrted");
                    try {
                       // log("CreditControlRequest");
                        ServerConnectionsPool serverConnectionsPool = channelChooser.chooseChannel(_nCtx.message.getAvps());
                     //   String failoverName = serverConnectionsPool.getFailoverUpstream().split(",")[0];
                        String failoverName = "ups2";
                        ClientChannels.setConnection(_nCtx.message.getSessionId(), _nCtx.channel.channel(), channelChooser.getPoolByName(failoverName).getConnection(), _nCtx.message, serverConnectionsPool.getFailoverUpstream());
                        Channel ch = serverConnectionsPool.getConnection();
                        synchronized (ch) {
                            ch.write(DiameterEncoder.parser.encodeMessage(_nCtx.message));//ССR request;
                        }
                        log("Sent to DRA Message with session_id " + session_id);
                    } catch (Exception e) {
                       // _nCtx.channel.write(CCA_msg((MessageImpl) _nCtx.message));
                        throw e;
                    }
                    break;
                case Message.DISCONNECT_PEER_REQUEST:
                  //  log("DPR Request");
                    ChannelFuture f = _nCtx.channel.write(DPA_msg());
                   /* f.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            future.getChannel().disconnect();
                        }
                    });*/
                    break;
            }
       } catch (ParseException e) {
           e.printStackTrace();
       }
    }

    private void log(String txt) {
        if (_debug) {
            Date curdate = new Date();
            System.out.print(curdate + "  CommandProcessor(InBoundHandler): " + txt + "\n");
        }
    }
}