package com.protos.app;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by julian on 20/04/17.
 */
public class Connection {
    private String  type;
    private SocketChannel channel;
    private ByteBuffer sourceBuffer;
    private ByteBuffer destinationBuffer;

    public Connection(SocketChannel channel, int bufferSize, String type) {
        this.type = type;
        this.channel = channel;
        this.sourceBuffer = ByteBuffer.allocate(bufferSize);
    }

    public Connection(SocketChannel channel, ByteBuffer destinationBuffer, int bufferSize, String type) {
        this.type =  type;
        this.channel = channel;
        this.destinationBuffer = destinationBuffer;
        this.sourceBuffer = ByteBuffer.allocate(bufferSize);
    }

    public String getType() { return type; }

    public SocketChannel getChannel() {
        return channel;
    }

    public ByteBuffer getSourceBuffer() {
        return sourceBuffer;
    }

    public ByteBuffer getDestinationBuffer() {
        return destinationBuffer;
    }

    public void setDestinationBuffer(ByteBuffer destinationBuffer) {
        this.destinationBuffer = destinationBuffer;
    }

}
