package de.stefanschade.AdventureGame.framework.datamodel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class World {

    public static final int START_ROOM = 1;
    // init of all loggers
    private static final Logger LOGGER = Logger.getLogger(World.class.getName());
    private static final Logger[] LOGGERS
            = new Logger[]{LOGGER,
            Logger.getLogger(Passages.class.getName()), // Passages
            Logger.getLogger(Rooms.class.getName())}; // Rooms
    private static final String FILE_ROOMS = "./resources/Rooms.csv";
    private static final String FILE_PASSAGES = "./resources/Passages.csv";

    static {
        System.setProperty("java.util.logging.config.file", "./resources/logging.properties");
        try {
            LogManager.getLogManager().readConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Logging properites could not be read, exit");
            System.exit(-1);
        }

        try {
            FileHandler f = new FileHandler("./out/logfile.log", false);
            for (Logger l : LOGGERS) {
                l.addHandler(f);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final Rooms roomsInWorld;
    private final Passages passagesInWorld;

    public World() {
        roomsInWorld = new Rooms(FILE_ROOMS);
        passagesInWorld = new Passages(FILE_PASSAGES);
    }

    private static void captureRoom
            (Map<Integer, Rooms.RoomEntry> rooms, Map<Integer, Passages.ExitsForOneOrigin> passages, Integer
                    id, Integer north, Integer east, Integer west, Integer south, String name, String description) {
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
        StringBuffer sb = new StringBuffer();
        boolean returnvalue;

        sb.append("isValidPassage() ");
        sb.append("Origin: ");
        sb.append(player.getCurrentRoom());
        sb.append("Direction: ");
        sb.append(direction);
        sb.append(" -> ");

        int currentRoom = player.getCurrentRoom();
        if (this.passagesInWorld.isValidPassage(currentRoom, direction)) {
            sb.append("valid\n");
            returnvalue = true;
        } else {
            returnvalue = false;
        }


        return returnvalue;
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
        //   Main.log("change rooom from " + player.getCurrentRoom() + " direction " + direction);
        int currentRoom = player.getCurrentRoom();
        int destinationRoom = this.passagesInWorld.getDestination(currentRoom, direction);
        player.setCurrentRoom(destinationRoom);
    }

}
