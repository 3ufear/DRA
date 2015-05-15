package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.base.Avp;
import com.deltasolutions.dra.base.IMessage;
import com.deltasolutions.dra.base.Message;
import com.deltasolutions.dra.base.ParseException;
import com.deltasolutions.dra.parser.AvpSetImpl;
import com.deltasolutions.dra.parser.MessageImpl;
import com.deltasolutions.dra.tcp.Encoder.DiameterEncoder;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;

public class CommandProcessorSmall extends Thread {

    public static final int DEFAULT_BUFFER_SIZE  = 1024;
	private NetContext _nCtx;
	private boolean _debug;
    private final ClientSocketChannelFactory cf;
    private volatile Channel outboundChannel;
    String originHost = "192.168.1.153";//Заменить на данные из модуля конфига.
    String originRealm = "vimpelcom.com";
    int resultCode = 2001;
    int vendor_id = 124141;
    String product_name = "DiamProxy";
    int appId = 123;
    int InbandSecurityId = 0;
    ServerConnectionsPool Channels = ServerConnectionsPool.getInstance();
    ClientConnectionsPool ClientChannels = ClientConnectionsPool.getInstance();



    public CommandProcessorSmall(String name, NetContext nCtx, boolean debug, ClientSocketChannelFactory cf) {
		super();
		setName(name);
        this.cf = cf;
		_nCtx = nCtx;
		_debug = debug;
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
	
	@Override
	public void run() {
       if (_debug) {
            System.out.print("IN  > " + _nCtx.message + "\n");
            System.out.println("COMMAND_CODE " + _nCtx.message.getCommandCode());
       }
       try {
            switch (_nCtx.message.getCommandCode()) {
                case Message.CAPABILITIES_EXCHANGE_REQUEST:
                    _nCtx.channel.write(CEA_msg());
                    break;
                case Message.DEVICE_WATCHDOG_REQUEST:
                    _nCtx.channel.write(DWA_msg());
                    break;
                case Message.CREDIT_CONTROL_REQUEST:
                    Channel ch = Channels.getConnection();
                    ClientChannels.setConnection(_nCtx.message.getSessionId(), _nCtx.channel);
                    if (_debug) {
                        System.out.println(_nCtx.message.getSessionId() + "  CCAAnsewr");
                    }
                   // _nCtx.message.getAvps().getAvp()
                    _nCtx.message.getAvps().addAvp(Avp.ROUTE_RECORD, 636);
                    ch.write(ChannelBuffers.wrappedBuffer(DiameterEncoder.parser.encodeMessage(_nCtx.message)));//ССR request;
                    break;
                case Message.DISCONNECT_PEER_REQUEST:
                    if (_debug) {
                        System.out.println("DPA Answer");
                        System.out.println(_nCtx.message.getSessionId() + "  DPAAnswer");
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
            /*      if (_nCtx.message.getCommandCode() == Message.CAPABILITIES_EXCHANGE_REQUEST) {
                        //DiameterEncoder.parser;
                        //Если пришел CER то отправляем CEA ANSWER

                        //CEA Answer
                    } else if (_nCtx.message.getCommandCode() == Message.CREDIT_CONTROL_ANSWER) {


                        ClientChannels.setConnection(_nCtx.message.getSessionId(), _nCtx.channel);
                        System.out.println(_nCtx.message.getSessionId() + "  CCAREQUEST");
                        ch.write(ChannelBuffers.wrappedBuffer(DiameterEncoder.parser.encodeMessage(_nCtx.message)));//ССR request;
                    } else if (_nCtx.message.getCommandCode() == Message.DEVICE_WATCHDOG_REQUEST) {

                    }
            }*/
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}