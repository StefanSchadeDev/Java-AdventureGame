package de.stefanschade.AdventureGame.framework.datamodel;

import de.stefanschade.AdventureGame.Main;

import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;

public final class World {

    private static final Logger[] LOGGERS
            = new Logger[]{Logger.getLogger(Passages.class.getName()),
            Logger.getLogger(Rooms.class.getName())};

    private static final FileHandler FILE;

    static {

        for (Logger l : LOGGERS) {
            System.out.println(l.getName());
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



    public static final int START_ROOM = 1;

    private static final String FILE_ROOMS = "./resources/Rooms.csv";
    private static final String FILE_PASSAGES = "./resources/Passages.csv";

    private final Rooms roomsInWorld;
    private final Passages passagesInWorld;
//    private final java.util.logging.Logger logger = new java.util.logging.Logger("World");

    public World() {
        roomsInWorld = new Rooms(FILE_ROOMS);
        passagesInWorld = new Passages(FILE_PASSAGES);
    }

    private static void captureRoom(Map<Integer, Rooms.RoomEntry> rooms, Map<Integer, Passages.ExitsForOneOrigin> passages, Integer id, Integer north, Integer east, Integer west, Integer south, String name, String description) {
        Map<String, Passages.Exit> exits = new HashMap<>();
        if (north != null) exits.put("north", new Passages.Exit(north));
        if (east != null) exits.put("east", new Passages.Exit(east));
        if (west != null) exits.put("west", new Passages.Exit(west));
        if (south != null) exits.put("south", new Passages.Exit(south));
        Rooms.RoomEntry room;
        room = new Rooms.RoomEntry(id, name, description);
        rooms.put(id, room);
        passages.put(id, new Passages.ExitsForOneOrigin(id, exits));
    }

    public int getDestination(int currentRoom, String direction) {
        return this.passagesInWorld.getDestination(currentRoom, direction);
    }

    public boolean isValidPassage(StateOfPlayer player, String direction) {
        int currentRoom = player.getCurrentRoom();
        if (this.passagesInWorld.isValidPassage(currentRoom, direction)) {
            Main.log("passage from rooom  " + player.getCurrentRoom() + " to " + direction
                    + " is validated as: " + this.passagesInWorld.getDestination(currentRoom, direction));
            return true;
        } else {
            return false;
        }
    }

    public String getRoomDescription(StateOfPlayer player) {
        StringBuffer sb = new StringBuffer();
        int currentRoom = player.getCurrentRoom();
        sb.append("\n");
        sb.append(roomsInWorld.getName(currentRoom));
        sb.append("\n");
        sb.append(roomsInWorld.getDescrpition(currentRoom));
        sb.append("\n");
        sb.append(passagesInWorld.getDirections(currentRoom));
        return sb.toString();
    }


    public void changeRoom(StateOfPlayer player, String direction) {
        Main.log("change rooom from " + player.getCurrentRoom() + " direction " + direction);
        int currentRoom = player.getCurrentRoom();
        int destinationRoom = this.passagesInWorld.getDestination(currentRoom, direction);
        player.setCurrentRoom(destinationRoom);
    }

}
