package de.stefanschade.AdventureGame;

import de.stefanschade.AdventureGame.framework.datamodel.StateOfGame;
import de.stefanschade.AdventureGame.framework.datamodel.StateOfPlayer;
import de.stefanschade.AdventureGame.framework.datamodel.World;

import java.util.Scanner;

public class Main {

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
