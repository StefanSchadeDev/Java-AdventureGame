package de.stefanschade.AdventureGame.framework.datamodel.immutables;

import net.jcip.annotations.Immutable;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Immutable
public class RoomMap {

    private static final Logger logger = Logger.getLogger(RoomMap.class.getName());
    private final String name;
    private final Map<Integer, Room> roomMapEntry;


    RoomMap(String filename) {
        this.name = "RoomMap Object read from file: " + filename;
        this.roomMapEntry = RoomFile.readMapFromFile(filename);
    }

    private boolean validateRoomOfOrigin(int i) {
        boolean returnvalue = true;
        if (!roomMapEntry.containsKey(i)) {
            logger.log(Level.WARNING, "RoomEntry " + i + " negative, this denotes an error state! ");
            returnvalue = false;
        }
        if (!roomMapEntry.containsKey(i)) {
            logger.log(Level.WARNING, "RoomEntry " + i + " not found in map " + roomMapEntry);
            returnvalue = false;
        }
        return returnvalue;
    }

    String getName(int i) {
        if (validateRoomOfOrigin(i)) {
            return roomMapEntry.get(i).getName();
        }
        return null;
    }

    String getDescrpition(int i) {
        if (validateRoomOfOrigin(i)) {
            return roomMapEntry.get(i).getDescription();
        }
        return null;
    }

}