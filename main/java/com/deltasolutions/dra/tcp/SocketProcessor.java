/**
 * Created by phil on 13-Dec-14.
 */
package com.deltasolutions.dra.tcp;

import com.deltasolutions.dra.base.Avp;
import com.deltasolutions.dra.base.AvpSet;
import com.deltasolutions.dra.base.IMessage;
import com.deltasolutions.dra.parser.AvpImpl;
import com.deltasolutions.dra.parser.MessageParser;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Iterator;

class SocketProcessor implements Runnable {

    private Socket sock;
    private InputStream instream;
    private OutputStream outstream;
    private static final MessageParser parser = new MessageParser();
    public static final int DEFAULT_STORAGE_SIZE = 2048;
    ByteBuffer storage = ByteBuffer.allocate(DEFAULT_STORAGE_SIZE);

    SocketProcessor(Socket s) throws Throwable {
        this.sock = s;
        this.instream = s.getInputStream();
        this.outstream = s.getOutputStream();
    }

    public void run() {
        try {
            IMessage message = readMessage();
            System.out.println("Flags:" + message.getFlags());
         //   HeaderProcessor Head = new HeaderProcessor(Headers);
         //   Head.ParseHeader();
         //   ResponseProcessor Response = new ResponseProcessor(Head, root);
            String response = null;
         //   byte[] file_output = Response.MakeResponse();
        //    if (Response.error_code == 404) {
         //       response = ResponseProcessor.response;
         //   } else if (Response.error_code == 200) {
         //       response = ResponseProcessor.response_200;
         //   } else if (Response.error_code == 500) {
          //      response = ResponseProcessor.response_500;
          //  }
          //  writeResponse(response, file_output);
        } catch (Throwable t) {
            System.err.println("Error " + t.toString() + t.getMessage());
        } finally {
            try {
                sock.close();
            } catch (Throwable t) {
                System.err.println("Error" + t.toString());
            }
        }
        System.out.println("Client processing finished");
    }

    private void writeResponse(String s, byte [] file_output) throws Throwable {

        outstream.write(s.getBytes());
        if (file_output != null) {
            outstream.write(file_output,0,file_output.length);
       }
        outstream.flush();
    }

    private IMessage readMessage() throws Throwable {
        //BufferedReader br = new BufferedReader(new InputStreamReader(instream));
        byte[] a = new byte[DEFAULT_STORAGE_SIZE];
        ByteBuffer data;
        data = ByteBuffer.allocate(DEFAULT_STORAGE_SIZE);


        String[] buf = new String[10];
        int i = 0;
        instream.read(a);
        data = ByteBuffer.wrap(a);
        //String s = new String(a,"cp1251");
        //data.put(a);
        data.position(0);
        int tmp = data.getInt();
        data.position(0);
        byte vers = (byte) (tmp >> 24);
        if (vers != 1) {
            return null;
        }
        // extract the message length, so we know how much to read
        int messageLength = (tmp & 0xFFFFFF);

        byte[] msg = new byte[messageLength];
        System.out.println("len: " + messageLength);
        data.get(msg);
        IMessage message =  parser.createMessage(ByteBuffer.wrap(msg));//parser.createMessage(data);
        AvpSet set = message.getAvps();
        AvpImpl avp = (AvpImpl) set.getAvpByIndex(1);
        Iterator<Avp> it = set.iterator();
        String str_1 = avp.getUTF8String();
        int c = 0;
        while (it.hasNext()) {
            System.out.println(it.next().getUTF8String());
            c++;
        }
        return message;
    }
}