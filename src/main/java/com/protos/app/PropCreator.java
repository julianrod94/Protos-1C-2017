package com.protos.app;

/**
 * Created by seguido on 13/05/17.
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class PropCreator {
    public static void main(String[] args) {

        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream("config.properties");

            // set the properties value
            prop.setProperty("serverport", "7666");
            prop.setProperty("adminport", "7667");
            prop.setProperty("adminid", "admin");
            prop.setProperty("adminpass","admin");
            prop.setProperty("l33t","on");
            prop.setProperty("rotate","off");
            prop.setProperty("imgbuffsize","4096");
            prop.setProperty("conbuffsize","1");
            prop.setProperty("defbuff","256");

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}

