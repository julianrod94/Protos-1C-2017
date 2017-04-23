package com.protos.app;

import javax.xml.crypto.KeySelector;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class EchoSelectorProtocol implements TCPProtocol {
    private int bufSize; // Size of I/O buffer
    //TODO Don't hardcode it
    private static String address = "localhost";
    private static int port = 7666;


    public EchoSelectorProtocol(int bufSize) {
        this.bufSize = bufSize;
    }

    public void handleAccept(SelectionKey key) throws IOException {
        SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();
        clntChan.configureBlocking(false); // Must be nonblocking to register

        //Open server channel
        SocketChannel srvrChan = SocketChannel.open();
        srvrChan.configureBlocking(false);
        //attempt connection
        srvrChan.connect(new InetSocketAddress(address, port));

        Connection clntCon = new Connection(srvrChan,1);
        Connection srvrCon = new Connection(clntChan,clntCon.getSourceBuffer(),1);
        clntCon.setDestinationBuffer(srvrCon.getSourceBuffer());

        // Register the selector with new channel for read and attach byte buffer
        srvrChan.register(key.selector(), SelectionKey.OP_CONNECT, srvrCon);
        clntChan.register(key.selector(), SelectionKey.OP_READ, clntCon);
    }

    public void handleRead(SelectionKey key) throws IOException {
        // Client socket channel has pending data
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buf = ((Connection) key.attachment()).getDestinationBuffer();
        long bytesRead = channel.read(buf);
        if (bytesRead == -1) { // Did the other end close?
            channel.close();
        } else if (bytesRead > 0) {
            // Indicate via key that reading/writing are both of interest now.
            if(buf.remaining()!=0){
                //If destination buffer has some space left
                key.interestOps(SelectionKey.OP_READ | key.interestOps());
            }else{
                //If destination buffer is full
                key.interestOps(key.interestOps());
            }
            ((Connection)key.attachment()).getChannel().keyFor(key.selector()).interestOps(SelectionKey.OP_WRITE |
                    ((Connection)key.attachment()).getChannel().keyFor(key.selector()).interestOps());
        }
    }

    public void handleConnect (SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        if (!channel.isConnected()){
            ((Connection)key.attachment()).getChannel().close();
        }else{
            key.interestOps(SelectionKey.OP_READ);
//            channel.register(key.selector(),SelectionKey.OP_READ,key.attachment());
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
        int written = channel.write(buf);
        if (!buf.hasRemaining()) { // Buffer completely written?
            // Nothing left, so no longer interested in writes
            key.interestOps(SelectionKey.OP_READ);
        }
        if (written > 0){
            ((Connection)key.attachment()).getChannel().keyFor(key.selector()).interestOps(SelectionKey.OP_READ |
                    ((Connection)key.attachment()).getChannel().keyFor(key.selector()).interestOps());
        }
        buf.flip();
        buf.compact(); // Make room for more data to be read in
    }
}