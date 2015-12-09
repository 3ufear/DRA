package com.deltasolutions.dra.tcp.Encoder;

import com.deltasolutions.dra.parser.MessageParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.List;

/**
 * Created by phil on 11-May-15.
 */
public class DiameterEncoder extends ByteToMessageDecoder {
    public static final MessageParser parser = new MessageParser();

    @Override
    protected void decode(ChannelHandlerContext chx,ByteBuf byteBuf, List<Object> out) throws Exception {

        int tmp = byteBuf.getInt(0);
        //channelBuffer.
        //data.position(0);
        //System.out.println(channelBuffer.toString());
        byte vers = (byte) (tmp >> 24);
        // extract the message length, so we know how much to read
        int messageLength = (tmp & 0x00FFFFFF);
        if (messageLength > byteBuf.readableBytes()) {
            return;
        }
        out.add(parser.createMessage(byteBuf.readBytes(messageLength).nioBuffer()));
    }
}
