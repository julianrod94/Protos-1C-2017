package com.protos.app.parser;

import com.protos.app.exceptions.ParserException;

/**
 * Created by julian on 10/05/17.
 */
public interface State {


    State handle(Context context) throws ParserException;

}


