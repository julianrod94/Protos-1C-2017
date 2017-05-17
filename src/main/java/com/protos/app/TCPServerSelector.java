package com.protos.app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Properties;

public class TCPServerSelector {
    private static final int BUFSIZE = 256; // Buffer size (bytes)
    // lo haog variable?
    private static int TIMEOUT; // Wait timeout (milliseconds)

    public static void main(String[] args) throws IOException {
        if (args.length < 1) { // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Port> ...");
        }

        Config.getInstance().load();
        // Create a selector to multiplex listening sockets and connections
        Selector selector = Selector.open();
        // Create listening socket channel for each port and register selector

        ServerSocketChannel listnChannel = ServerSocketChannel.open();
        listnChannel.socket().bind(new InetSocketAddress(Integer.parseInt(Config.getInstance().get("serverport"))));
        listnChannel.configureBlocking(false); // must be nonblocking to register
        ServerSocketChannel adminChannel = ServerSocketChannel.open();
        adminChannel.socket().bind(new InetSocketAddress(Integer.parseInt(Config.getInstance().get("adminport"))));
        adminChannel.configureBlocking(false);
        // Register selector with channel. The returned key is ignored
        listnChannel.register(selector, SelectionKey.OP_ACCEPT);
        adminChannel.register(selector, SelectionKey.OP_ACCEPT);


        // Create a handler that will implement the protocol
        TCPProtocol protocol = new EchoSelectorProtocol(Integer.parseInt(Config.getInstance().get("IObuffsize")));
        TIMEOUT = Integer.parseInt(Config.getInstance().get("Timeout"));
        while (true) { // Run forever, processing available I/O operations
            // Wait for some channel to be ready (or timeout)
            if (selector.select(TIMEOUT) == 0) { // returns # of ready chans
               // System.out.print(".");
                continue;
            }
            // Get iterator on set of keys with I/O to process
            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
            while (keyIter.hasNext()) {
                System.out.println(selector.keys().size());
                SelectionKey key = keyIter.next(); // Key is bit mask
                // Server socket channel has pending connection requests?
                if (key.isAcceptable()) {
                    protocol.handleAccept(key);
                }
                if (key.isConnectable()) {
                    System.out.println("Connecting");
                    protocol.handleConnect(key);
                }
                // Client socket channel has pending data?
                if (key.isValid() && key.isReadable()) {
                    protocol.handleRead(key);
                }
                // Client socket channel is available for writing and
                // key is valid (i.e., channel not closed)?
                if (key.isValid() && key.isWritable()) {
                    protocol.handleWrite(key);
                }
                keyIter.remove(); // remove from set of selected keys
            }
        }
    }
}