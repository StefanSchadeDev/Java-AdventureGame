package de.stefanschade.AdventureGame;

import de.stefanschade.AdventureGame.framework.datamodel.StateOfGame;
import de.stefanschade.AdventureGame.framework.datamodel.StateOfPlayer;
import de.stefanschade.AdventureGame.framework.datamodel.World;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    static {
        Logger rootLogger = Logger.getLogger("");
        try {
            FileHandler f = new FileHandler("./out/logfile.log", false);
            System.setProperty("java.util.logging.config.file", "./resources/logging.properties");
            LogManager.getLogManager().readConfiguration();
            try {
                rootLogger.addHandler(f);
            } finally {
                if (f != null) {
                    f.close();
                }
            }
        } catch (IOException | SecurityException e) {
            System.out.println("Error Initializing Logger: ");
            e.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static World world = new World();
    private static StateOfPlayer playerState = new StateOfPlayer();

    private static StateOfGame gameState = new StateOfGame();


    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (!playerState.isQuit()) {

            System.out.println(world.getRoomDescription(playerState));

            System.out.print("What should I do?\n>");
            String input = sc.next().toLowerCase();

            if (world.isValidPassage(playerState, input)) {
                world.changeRoom(playerState, input);
            } else {
                System.out.println("You can not go in this direction!");
            }

            System.out.println(input);
            if (input.trim().equalsIgnoreCase("quit")) {
                playerState.quit();
            }
        }
    }
}
