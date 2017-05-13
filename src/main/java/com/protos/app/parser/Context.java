package com.protos.app.parser;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by julian on 10/05/17.
 */
public class Context {

    private State state;
    private ByteBuffer queue;
    private ByteBuffer parsed;
    private Map<String, String> headers;
    private StringBuffer method;
    private StringBuffer url;
    private ByteBuffer image;

    public Context(State state, ByteBuffer queue, ByteBuffer parsed) {
        this.state = state;
        this.queue = queue;
        this.parsed = parsed;
        this.headers =  new HashMap<>();
        this.method = new StringBuffer();
        this.url = new StringBuffer();
    }

    public State getState() {
        return state;
    }

    public ByteBuffer getQueue() {
        return queue;
    }

    public ByteBuffer getParsed() {
        return parsed;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public StringBuffer getMethod() {
        return method;
    }

    public StringBuffer getUrl() {
        return url;
    }

    public ByteBuffer getImage() {
        return image;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setMethod(StringBuffer method) {
        this.method = method;
    }

    public void setUrl(StringBuffer url) {
        this.url = url;
    }

    public void setImage(ByteBuffer image) {
        this.image = image;
    }
}
