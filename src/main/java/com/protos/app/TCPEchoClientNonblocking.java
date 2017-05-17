package com.protos.app;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Cliente TCP Echo no bloqueante, pero que hace "polling" tanto para la conexion como para leer y escribir,
 * lo cual lo vuelve muy ineficiente ya que consume mucho procesador
 */
public class TCPEchoClientNonblocking {
    public static void main(String args[]) throws Exception {
        if ((args.length < 2) || (args.length > 3)) // Test for correct # of args
        throw new IllegalArgumentException("Parameter(s): <Server> <Word> [<Port>]");

        String server = args[0]; // Server name or IP address
        // Convert input String to bytes using the default charset
        byte[] argument = args[1].getBytes();

        int servPort = (args.length == 3) ? Integer.parseInt(args[2]) : 7;
        // Create channel and set to nonblocking
        SocketChannel clntChan = SocketChannel.open();
        clntChan.configureBlocking(false);
        System.out.println(server + " asd " + servPort);
        // Initiate connection to server and repeatedly poll until complete
        if (!clntChan.connect(new InetSocketAddress(server, servPort))) {
            while (!clntChan.finishConnect()) {
                System.out.print("."); // Do something else
            }
        }
        ByteBuffer writeBuf = ByteBuffer.wrap(argument);
        ByteBuffer readBuf = ByteBuffer.allocate(argument.length);
        int totalBytesRcvd = 0; // Total bytes received so far
        int bytesRcvd; // Bytes received in last read
        int counter = 0;
//        while (totalBytesRcvd < argument.length) {
        while (counter++ <2 ){
            if (writeBuf.hasRemaining()) {
                clntChan.write(writeBuf);
            }
            if ((bytesRcvd = clntChan.read(readBuf)) == -1) {
                throw new SocketException("Connection closed prematurely");
            }
            totalBytesRcvd += bytesRcvd;
            System.out.print("."); // Do something else
        }
        System.out.println("Received: " + // convert to String per default
                                          // charset
                new String(readBuf.array(), 0, totalBytesRcvd));
        Thread.sleep(35000);
        System.out.println("Closed Connection");
        clntChan.close();
    }

}
