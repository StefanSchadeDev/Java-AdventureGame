package de.stefanschade.AdventureGame.framework.datamodel.immutables;

import de.stefanschade.AdventureGame.framework.datamodel.StateOfPlayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class World {

    public static final int START_ROOM = 1;
    // init of all loggers
    private static final Logger LOGGER = Logger.getLogger(World.class.getName());
    private static final String FILE_ROOMS = "./resources/Rooms.csv";
    private static final String FILE_PASSAGES = "./resources/Passages.csv";

    private final RoomMap roomsInWorld;
    private final PassageMap passagesInWorld;

    public World() throws IOException {
        roomsInWorld = RoomFile.readMapFromFile(FILE_ROOMS);
        passagesInWorld = PassageFile.readMapFromFile(FILE_PASSAGES);
    }

    private static void captureRoom
            (Map<Integer, Room> rooms, Map<Integer, PassagesByOrigin> passages, Integer
                    id, Integer north, Integer east, Integer west, Integer south, String name, String description) {
        Map<String, Passage> exits = new HashMap<>();
        if (north != null) exits.put("north", new Passage(north));
        if (east != null) exits.put("east", new Passage(east));
        if (west != null) exits.put("west", new Passage(west));
        if (south != null) exits.put("south", new Passage(south));
        Room room;
        room = new Room(id, name, description);
        rooms.put(id, room);
        passages.put(id, new PassagesByOrigin(id, exits));
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
