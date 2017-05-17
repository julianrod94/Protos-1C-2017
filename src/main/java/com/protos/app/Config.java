package com.protos.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by seguido on 13/05/17.
 */
public class Config {
    public static Config instance = null;
    public static FileInputStream file = null;
    public static Properties prop = new Properties();

    public static Config getInstance(){
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void load(){
        if (file == null){
            try {
                file = new FileInputStream("config.properties");
                prop.load(file);
                file.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }

    public String get(String key){
        return prop.getProperty(key);
    }


}
