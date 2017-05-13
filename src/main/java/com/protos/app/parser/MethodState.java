package com.protos.app.parser;


import com.protos.app.exceptions.UnsupportedMethodException;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by julian on 11/05/17.
 */
public class MethodState implements State {

    private StringBuffer method = new StringBuffer();
    private Set<String> supportedMethods = new HashSet<>();

    public MethodState(){
        supportedMethods.add("GET");
        supportedMethods.add("POST");
        supportedMethods.add("HEAD");
    }

    @Override
    public State handle(Context context) {
        ByteBuffer queue = context.getQueue();

        //There's nothing else to read
        if(queue == null || queue.position() == 0) return this;
        queue.flip();
        //Method is case sensitive, must be on caps
        boolean finishedParsing = false;
        while(queue.hasRemaining() && !finishedParsing){
            char current = (char) queue.get();
            if(current >= 'A' && current <= 'Z'){
                method.append(current);
            }else if(current == ' '){
                finishedParsing = true;
            }else{
                throw new UnsupportedMethodException();
            }
        }
        queue.flip();
        if(finishedParsing){
            return checkMethod(context);
        }
        return this;
    }

    private State checkMethod(Context context) {
        String method = this.method.toString();
        if(!supportedMethods.contains(method)){
            throw new UnsupportedMethodException();
        }
        context.setMethod(this.method);
        context.getParsed().put(method.getBytes());
        context.getParsed().putChar(' ');
        return new URLState();
    }
}
