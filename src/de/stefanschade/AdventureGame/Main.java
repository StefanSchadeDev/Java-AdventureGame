package de.stefanschade.AdventureGame;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    private static final String LOG_CONFIG_PATH = "./resources/logging.properties";
    private static Logger logger = Logger.getLogger(Main.class.getName());

    static {
        System.setProperty("java.util.logging.config.file", LOG_CONFIG_PATH);
        LogManager manager = LogManager.getLogManager();
        try {
            manager.readConfiguration();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not open logging.properties file");
            System.exit(-1);
            System.exit(-1);
        }
        logger.info("class Main / static block processed / logging configured");
    }


    public static void main(String[] args) {

        Game game = new Game();
        game.play();

    }


}
