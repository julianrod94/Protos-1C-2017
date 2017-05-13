package com.protos.app.parser;

import com.protos.app.exceptions.ParserException;
import com.protos.app.exceptions.UnsupportedProtocolException;

import java.nio.ByteBuffer;

/**
 * Created by julian on 11/05/17.
 */
public class ProtocolState implements State {

    private StringBuffer protocol;
    private boolean finishedParsingFirstSlash = false;
    boolean finishedParsing = false;


    @Override
    public State handle(Context context) throws ParserException {
        ByteBuffer queue = context.getQueue();

        //There's nothing else to read
        if(queue == null || queue.position() == 0) return this;
        queue.flip();
        //Parses protocol and "://"
        while(queue.hasRemaining() && !finishedParsing){
            char current = (char) queue.get();
            if(((current >= 'A' && current <= 'Z') || (current >= 'a' && current <= 'b') || (current == ':'))){
                protocol.append(current);
            }
            //makes sure there is only one :
            else if(current == '/'){
                if(!finishedParsingFirstSlash){
                    finishedParsingFirstSlash = true;
                }else{
                    finishedParsing = true;
                }
                protocol.append(current);
            }
            else{
                throw new UnsupportedProtocolException();
            }
        }
        queue.flip();
        if(finishedParsing){
            return checkProtocol(context);
        }
        return this;
    }

    private State checkProtocol(Context context) {
        if(!protocol.toString().toLowerCase().equals("http://")) throw new UnsupportedProtocolException();
        return new HostState();
    }

}
