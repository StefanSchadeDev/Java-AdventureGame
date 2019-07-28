package de.stefanschade;

import de.stefanschade.AdventureGame.framework.datamodel.Passages;
import de.stefanschade.AdventureGame.framework.datamodel.Rooms;
import de.stefanschade.AdventureGame.framework.datamodel.World;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Level {


    public static final int START_ROOM = 1;
    // init of all loggers
    private static final Logger LOGGER = Logger.getLogger(World.class.getName());
    private static final Logger[] LOGGERS
            = new Logger[]{LOGGER,
            Logger.getLogger(Passages.class.getName()), // Passages
            Logger.getLogger(Rooms.class.getName())}; // Rooms
    private static final FileHandler FILE;
    private static final String FILE_ROOMS = "./resources/Rooms.csv";
    private static final String FILE_PASSAGES = "./resources/Passages.csv";

    static {
        for (Logger l : LOGGERS) {
            System.out.println("Logger: "+ l.getName());
        }
        System.setProperty("java.util.logging.config.file", "./resources/logging.properties");
        try {
            LogManager.getLogManager().readConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileHandler f = new FileHandler("./out/logfile.log", true);
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
//                        l.removeHandler(f);
                        l.setUseParentHandlers(false);
                    }
                }
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }


    public static final int INFO = 100;
    public static final int WARNING = 500;
    public static final int ERROR = 1000;

}
