package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.base.*;
import com.deltasolutions.dra.chanelChooserHelper.ChanelChooser;
import com.deltasolutions.dra.config.ProxyAgent;
import com.deltasolutions.dra.parser.AvpSetImpl;
import com.deltasolutions.dra.parser.MessageImpl;
import com.deltasolutions.dra.tcp.Encoder.DiameterEncoder;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

public class CommandProcessorSmall extends Thread {

	private NetContext _nCtx;
	private boolean _debug;
    String originHost = ProxyAgent.originHost;
    String originRealm = ProxyAgent.originRealm;
    int resultCode = 2001;
    int vendor_id = ProxyAgent.vendorId;
    String product_name = ProxyAgent.productName;
    int appId = ProxyAgent.appId;
    int InbandSecurityId = 0;
    ChanelChooser channelChooser = ChanelChooser.getInstance();//ServerConnectionsPool.getInstance();
    ClientConnectionsPool ClientChannels = ClientConnectionsPool.getInstance();



    public CommandProcessorSmall(String name, NetContext nCtx, boolean debug) {
		super();
	//	setName(name);
		_nCtx = nCtx;
		_debug = true;
	}

    private ChannelBuffer DWA_msg() throws ParseException {
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(Avp.ORIGIN_HOST, originHost, false);
        set.addAvp(Avp.ORIGIN_REALM, originRealm, false);
        set.addAvp(Avp.RESULT_CODE, resultCode);
        IMessage msg = new MessageImpl(Message.DEVICE_WATCHDOG_ANSWER, appId, (short) 0,  21414, 33252, set);
        return ChannelBuffers.wrappedBuffer(DiameterEncoder.parser.encodeMessage(msg));
    }

    private ChannelBuffer CEA_msg() throws ParseException {
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(264, originHost, false);
        set.addAvp(296, originRealm, false);
        set.addAvp(268, resultCode);
        set.addAvp(266, vendor_id);
        set.addAvp(269, product_name, false);
        set.addAvp(258, appId);
        set.addAvp(299, InbandSecurityId);
        IMessage msg = new MessageImpl(Message.CAPABILITIES_EXCHANGE_ANSWER, appId, (short) 0,  _nCtx.message.getHopByHopIdentifier(), _nCtx.message.getEndToEndIdentifier(), set);
        //IMessage msg = (IMessage) _nCtx.message.createAnswer();
        return ChannelBuffers.wrappedBuffer(DiameterEncoder.parser.encodeMessage(msg));
    }

    private ChannelBuffer DPA_msg() throws ParseException {
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(264, originHost, false);
        set.addAvp(296, originRealm, false);
        set.addAvp(268, resultCode);
        IMessage msg = new MessageImpl(Message.DISCONNECT_PEER_ANSWER, appId, (short) 0,  _nCtx.message.getHopByHopIdentifier(), _nCtx.message.getEndToEndIdentifier(), set);
        return ChannelBuffers.wrappedBuffer(DiameterEncoder.parser.encodeMessage(msg));
    }

    private ChannelBuffer CCA_msg(MessageImpl msg) throws ParseException {
        IMessage message = new MessageImpl(msg);
        AvpSetImpl set = new AvpSetImpl();
        set.addAvp(264, originHost, false);
        set.addAvp(296, originRealm, false);
        set.addAvp(268, resultCode);
        message.setRequest(false);
      //  IMessage msg = new MessageImpl(Message.CREDIT_CONTROL_ANSWER, appId, (short) 0,  _nCtx.message.getHopByHopIdentifier(), _nCtx.message.getEndToEndIdentifier(), set);
        return ChannelBuffers.wrappedBuffer(DiameterEncoder.parser.encodeMessage(message));
    }
    @Override
	public void run() {
       log("IN  > " + _nCtx.message + "\n");
       log("COMMAND_CODE " + _nCtx.message.getCommandCode());
       try {
            switch (_nCtx.message.getCommandCode()) {
                case Message.CAPABILITIES_EXCHANGE_REQUEST:
                    _nCtx.channel.write(CEA_msg());
                    break;
                case Message.DEVICE_WATCHDOG_REQUEST:
                    _nCtx.channel.write(DWA_msg());
                    break;
                case Message.CREDIT_CONTROL_REQUEST:
                    Channel ch = channelChooser.chooseChannel(_nCtx.message.getAvps());
                    ClientChannels.setConnection(_nCtx.message.getSessionId(), _nCtx.channel);
                    _nCtx.message.getAvps().addAvp(Avp.ROUTE_RECORD, 636);
                    ch.write(ChannelBuffers.wrappedBuffer(DiameterEncoder.parser.encodeMessage(_nCtx.message)));//ССR request;
                        log(_nCtx.message.getSessionId() + "  CCAAnswer");
                        //_nCtx.channel.write(CCA_msg((MessageImpl)_nCtx.message));
                    break;
                case Message.DISCONNECT_PEER_REQUEST:
                    if (_debug) {
                        log("DPA Answer");
                        log(_nCtx.message.getSessionId() + "  DPAAnswer");
                    }
                    ChannelFuture f = _nCtx.channel.write(DPA_msg());
                   // _nCtx.channel.disconnect();
                    f.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            future.getChannel().disconnect();
                        }
                    });
                    break;
            }
       } catch (ParseException e) {
           e.printStackTrace();
       } catch (AvpDataException e) {
           e.printStackTrace();
       }
    }

    private void log(String txt) {
        if (_debug) {
            System.out.print("CommandProcessor(InBoundHandler): " + txt + "\n");
        }
    }
}