package de.stefanschade.AdventureGame;

import de.stefanschade.AdventureGame.framework.datamodel.StateOfGame;
import de.stefanschade.AdventureGame.framework.datamodel.StateOfPlayer;
import de.stefanschade.AdventureGame.framework.datamodel.World;
import de.stefanschade.StefanLog;

import java.util.Scanner;

public class Main {


    private static World world = new World();
    private static StateOfPlayer player = new StateOfPlayer();
    private static StateOfGame game = new StateOfGame();


    public static void main(String[] args) {

        // get singelton
        StefanLog LogObject = StefanLog.getInstance();
        LogObject.setLoglevel(1000);

        Scanner sc = new Scanner(System.in);

        while (!player.isQuit()) {

            System.out.println(world.getRoomDescription(player));

            System.out.print("What should I do?\n>");
            String input = sc.next().toLowerCase();

            if (world.isValidPassage(player, input)) {
                world.changeRoom(player, input);
            } else {
                System.out.println("You can not go in this direction!");
            }

            System.out.println(input);
            if (input.trim().equalsIgnoreCase("quit")) {
                player.quit();
            }

        }

    }

    public static void log(String msg) {
        //System.out.println(msg);
    }

}
