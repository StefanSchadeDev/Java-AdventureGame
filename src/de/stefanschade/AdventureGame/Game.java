package de.stefanschade.AdventureGame;

import de.stefanschade.AdventureGame.framework.datamodel.StateOfGame;
import de.stefanschade.AdventureGame.framework.datamodel.StateOfPlayer;
import de.stefanschade.AdventureGame.framework.datamodel.immutables.World;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game {

    private final Logger logger = Logger.getLogger(Game.class.getName());
    private World world = new World();
    private StateOfPlayer playerState = new StateOfPlayer();
    private StateOfGame gameState = new StateOfGame();



    public void play() {
        Scanner sc = new Scanner(System.in);
        while (!playerState.isQuit()) {

            System.out.println(world.getRoomDescription(playerState));

            System.out.print("What should I do?\n>");
            String input = null;
            try {
                input = sc.next().toLowerCase();
            } catch (NoSuchElementException e) {
                logger.log(Level.SEVERE, "InputStream disrupted, terminating game",e);
                System.exit(-1);
            }

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
