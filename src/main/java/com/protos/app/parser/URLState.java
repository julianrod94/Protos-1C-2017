package com.protos.app.parser;

public class URLState implements State {

    private StringBuffer url = new StringBuffer();
    private StringBuffer host = new StringBuffer();
    private State currentURLState = new ProtocolState();

    public URLState(){
    }

    @Override
    public State handle(Context context) {
        //TODO Implement
        return null;
    }

}
