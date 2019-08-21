package de.stefanschade.AdventureGame.framework.datamodel.immutables;

import net.jcip.annotations.Immutable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Immutable
public class PassageMap {

    private static final Logger logger = Logger.getLogger(PassageMap.class.getName());
    private Map<Integer, PassagesByOrigin> exitsByOrigin;

    public PassageMap(Map<Integer, PassagesByOrigin> exitsByOrigintmp) {
        this.exitsByOrigin = new HashMap<>(exitsByOrigintmp);
    }

    private PassagesByOrigin getPassage(int i) {
        if (!exitsByOrigin.containsKey(i)) {
            logger.log(Level.SEVERE, "PassagesByOrigin " + i + " not found in map " + exitsByOrigin);
            return null;
        } else {
            return exitsByOrigin.get(i);
        }
    }

    boolean isValidPassage(int currentRoom, String direction) {
        if (validateRoomOfOrigin(currentRoom)) {
            return this.getPassage(currentRoom).isValidPassage(direction);
        } else {
            return false;
        }
    }

    int getDestination(int currentRoom, String direction) {
        if (validateRoomOfOrigin(currentRoom)) {
            return this.getPassage(currentRoom).getDestination(direction);
        } else {
            return -1;
        }

    }

    String getDirections(int currentRoom) {
        if (validateRoomOfOrigin(currentRoom)) {
            return this.getPassage(currentRoom).getDirections();
        } else {
            return null;
        }
    }

    boolean validateRoomOfOrigin(int i) {
        boolean returnvalue = true;
        if (!this.exitsByOrigin.containsKey(i)) {
            logger.log(Level.WARNING, "RoomOfOrigin " + i + " negative - This denotes an error state! ");
            returnvalue = false;
        }
        if (!this.exitsByOrigin.containsKey(i)) {
            logger.log(Level.WARNING, "RoomOfOrigin " + i + " not found!");
            returnvalue = false;
        }
        return returnvalue;
    }

}