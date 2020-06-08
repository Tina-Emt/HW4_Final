package Util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerClass {
    private static LoggerClass LoggerInstance = null;
    private Logger logger ;

    {
        try {
            FileHandler handler = new FileHandler("logfile.log", true);
            handler.setFormatter(new SimpleFormatter());
            logger = Logger.getLogger(String.valueOf(LoggerClass.class));
            logger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // private constructor restricted to this class itself
    private LoggerClass() {

    }

    // static method to create instance of Singleton class
    public static LoggerClass getInstance() {

        if (LoggerInstance == null)
            LoggerInstance = new LoggerClass();


        return LoggerInstance;
    }


    public Logger getLogger() {
        return logger;
    }

}
