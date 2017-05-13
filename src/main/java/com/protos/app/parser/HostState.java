package com.protos.app.parser;

import com.protos.app.exceptions.ParserException;
import com.protos.app.exceptions.UnsupportedHostException;

import java.nio.ByteBuffer;

/**
 * Created by julian on 11/05/17.
 */
public class HostState implements State {

    private StringBuffer host = new StringBuffer();
    private boolean foundURIFirstSlash = false;
    boolean foundURI = false;


    public State handle(Context context) throws ParserException {
        ByteBuffer queue = context.getQueue();

        //There's nothing else to read
        if(queue == null || queue.position() == 0) return this;
        queue.flip();
        while(queue.hasRemaining() && !foundURI){
            char current = (char) queue.get();
            if(((current >= 'A' && current <= 'Z') || (current >= 'a' && current <= 'z') || (current >= '0'  && current <= '9') || (current == '.'))){
                host.append(current);
            }
            else if(current == '/') foundURI = true;
            else{
                throw new UnsupportedHostException();
            }
        }
        queue.flip();
        if(foundURI){
            return checkURI(); //checkHost(context);
        }
        else {
            return this;
        }
    }

    private State checkHost() {
        return new HostState();
    }

    private State checkURI() {
        return new URIState();
    }
}
