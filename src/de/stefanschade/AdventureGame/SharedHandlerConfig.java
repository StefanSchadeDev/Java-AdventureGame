package de.stefanschade.AdventureGame;

import de.stefanschade.AdventureGame.framework.datamodel.Passages;
import de.stefanschade.AdventureGame.framework.datamodel.Rooms;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public final class SharedHandlerConfig {

    private static final Logger[] LOGGERS
            = new Logger[]{Logger.getLogger(Passages.class.getName()),
            Logger.getLogger(Rooms.class.getName())};

    private static final FileHandler FILE;

    static {


        System.setProperty("java.util.logging.config.file", "logging.properties");


        try {
            LogManager.getLogManager().readConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            FileHandler f = new FileHandler("./logfile.log", true);
            try {
                for (Logger l : LOGGERS) {
                    l.addHandler(f);
                }
                FILE = f;
                f = null;
            } finally {
                if (f != null) {
                    f.close();
                    for (Logger l : LOGGERS) {
                        l.removeHandler(f);
                    }
                }
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        System.out.println("XXXXXXXXXXXXXXXXx");
    }
}