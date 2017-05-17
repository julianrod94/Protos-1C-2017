package com.protos.app;

import javax.xml.crypto.KeySelector;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class EchoSelectorProtocol implements TCPProtocol {
    private int bufSize; // Size of I/O buffer
    //TODO Don't hardcode it
    private static String address = "localhost";
    private static int port;
    private static int adminport;


    public void setBufSize(int bufSize){
        this.bufSize=bufSize;
    }

    public EchoSelectorProtocol(int bufSize) {
        this.bufSize = bufSize;
        this.port = Integer.parseInt(Config.getInstance().get("serverport"));
        this.adminport = Integer.parseInt(Config.getInstance().get("adminport"));
    }

    public void handleAccept(SelectionKey key) throws IOException {
        SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();
        clntChan.configureBlocking(false); // Must be nonblocking to register

        System.out.println("Socket : " + ((ServerSocketChannel) key.channel()).socket().getLocalPort());

        if (((ServerSocketChannel) key.channel()).socket().getLocalPort() == adminport){
            //LOG admin attempted connection.
            Connection unique = new Connection(clntChan, bufSize, "Admin");
            unique.setDestinationBuffer(ByteBuffer.allocate(bufSize));
            clntChan.register(key.selector(),SelectionKey.OP_READ,unique);

        }else{
            //Open server channel
            SocketChannel srvrChan = SocketChannel.open();
            srvrChan.configureBlocking(false);
            Connection clntCon = new Connection( srvrChan,bufSize, "Client");
            Connection srvrCon = new Connection(clntChan,clntCon.getSourceBuffer(),bufSize, "Server");
            clntCon.setDestinationBuffer(srvrCon.getSourceBuffer());

            //attempt connection
            if (srvrChan.connect(new InetSocketAddress("foro.comunidadargentum.com", 80))){
                System.out.println("Connected instantly");
                key.interestOps(SelectionKey.OP_READ);
                srvrChan.register(key.selector(), SelectionKey.OP_READ, srvrCon);
            }

            // Register channels to selectors + Connection attachment
            srvrChan.register(key.selector(), SelectionKey.OP_CONNECT, srvrCon);
            clntChan.register(key.selector(), 0, clntCon);
        }
    }

    public void handleConnect (SelectionKey key)  /*throws IOException*/{
        SocketChannel channel = (SocketChannel) key.channel();
        try{
            if (!channel.finishConnect()){
                //Failed Connection
                channel.close();

                ((Connection)key.attachment()).getChannel().close();
            }else{
                // Connection established & both channels ready to read now
                key.interestOps(SelectionKey.OP_READ);
                ((Connection)key.attachment()).getChannel().keyFor(key.selector()).interestOps(SelectionKey.OP_READ);
            }
        }catch(IOException e){
            System.out.println("Catched exception in handleconnect");
            //channel.close();
            //((Connection)key.attachment()).getChannel().close();
        }
    }

    public void handleRead(SelectionKey key) throws IOException {
        // Client socket channel has pending data

        if(((Connection)key.attachment()).getType().compareTo("Admin") == 0){
            System.out.println("Aca entra nico");
        }
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buf = ((Connection) key.attachment()).getDestinationBuffer();
        System.out.println("Reading from :  " + ((Connection)key.attachment()).getType());
        long bytesRead = 0;
        try{
             bytesRead = channel.read(buf);
        }catch(IOException e){
            bytesRead=-1;
            e.printStackTrace();
        }
        //System.out.println(new String( buf.array(), Charset.forName("UTF-8")));
        if (bytesRead == -1) { // Did the other end close?
            key.cancel();
            channel.close();
            System.out.println("Closed Connection  by   " + ((Connection)key.attachment()).getType());
        } else if (bytesRead > 0) {
            // Indicate via key that reading/writing are both of interest now.
            if (buf.remaining() != 0) {
                //If destination buffer has some space left
                key.interestOps(SelectionKey.OP_READ | key.interestOps());
            } else {
                //If destination buffer is full
                key.interestOps(key.interestOps() & SelectionKey.OP_WRITE);

            }
            if (key.isValid()) {
                SelectionKey otherkey = ((Connection) key.attachment()).getChannel().keyFor(key.selector());
                if (otherkey!= null  && otherkey.isValid()) {
                    ((Connection) key.attachment()).getChannel().keyFor(key.selector()).interestOps(SelectionKey.OP_WRITE |
                            ((Connection) key.attachment()).getChannel().keyFor(key.selector()).interestOps());
                }
            }
        }
    }

    public void handleWrite(SelectionKey key) throws IOException {
        /*
         * Channel is available for writing, and key is valid (i.e., client
         * channel not closed).
         */
        // Retrieve data read earlier
        ByteBuffer buf = ((Connection) key.attachment()).getSourceBuffer();
        buf.flip(); // Prepare buffer for writing
        SocketChannel channel = (SocketChannel) key.channel();
        int written = 0;
        try{
            written = channel.write(buf);
        }catch (IOException e){
            channel.close();
            e.printStackTrace();
            key.cancel();
        }
        if (!buf.hasRemaining()) { // Buffer completely written?
            // Nothing left, so no longer interested in writes
            key.interestOps(SelectionKey.OP_READ);
        }
        if (written > 0){
            SelectionKey otherkey = ((Connection)key.attachment()).getChannel().keyFor(key.selector());
            if (otherkey.isValid()){
                otherkey.interestOps(SelectionKey.OP_READ | otherkey.interestOps());
                /*((Connection)key.attachment()).getChannel().keyFor(key.selector()).interestOps(SelectionKey.OP_READ |
                        ((Connection)key.attachment()).getChannel().keyFor(key.selector()).interestOps());   */
            }

        }

        //buf.flip();// after testing, determined it was not needed
        buf.compact(); // Make room for more data to be read in
    }
}