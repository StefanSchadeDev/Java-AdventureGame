package de.stefanschade.AdventureGame.framework.datamodel.immutables;

import net.jcip.annotations.Immutable;

import java.util.logging.Level;
import java.util.logging.Logger;

@Immutable
final class Room {
    private final int roomID;
    private final String name;
    private final String description;
    private final Logger logger = Logger.getLogger("RoomEntry");

    Room(int roomID, String name, String description) {
        if (roomID < 0) {
            logger.log(Level.SEVERE, "Instantiation of Object failed, RoomID ("
                    + roomID + ") must be positive");
            System.exit(-2);
        }
        this.roomID = roomID;
        this.name = name;
        this.description = description;
    }

    int getID() {
        return roomID;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "RoomEntry #" + this.roomID + " [" + this.name + "]";
    }
}