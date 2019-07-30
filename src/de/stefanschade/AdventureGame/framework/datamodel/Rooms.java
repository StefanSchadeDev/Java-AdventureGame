package de.stefanschade.AdventureGame.framework.datamodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Rooms {
    private static final String CSV_SEPERATOR = ";";
    private final String name;
    private final Map<Integer, RoomEntry> roomMapEntry;
    private final Logger logger = Logger.getLogger(Rooms.class.getName());

    Rooms(String filename) {
        StringBuffer logmsg = new StringBuffer();
        logmsg.append("Constructor called -> ");
        logmsg.append("Filename:");
        if (filename == null) {
            logmsg.append("null / FILENAME HAS TO BE SPECIFIED");
            logger.log(Level.SEVERE, logmsg.toString());
            System.exit(-2);
        } else if (filename.isEmpty()) {
            logmsg.append("empty string / FILENAME HAS TO BE SPECIFIED");
            logger.log(Level.SEVERE, logmsg.toString());
            System.exit(-2);
        }
        logmsg.append(filename);
        logger.log(Level.INFO, logmsg.toString());
        logmsg = new StringBuffer();
        this.name = "Rooms Object read from file: " + filename;

        Map<Integer, RoomEntry> roomMapEntrytmp = new HashMap<>();
        Set<Integer> roomsAlreadyProcessed = new HashSet<>();
        String inputLine;
        int line = 0;
        try {
            Path path = Paths.get(filename);
            BufferedReader br = Files.newBufferedReader(path);
            while ((inputLine = br.readLine()) != null) {
                line++;
                logmsg.append(" #");
                logmsg.append(line);
                logmsg.append(" -> ");
                if (inputLine.trim().isEmpty() || inputLine.startsWith("#")) {
                    logmsg.append("empty");
                    continue; // ignore empty lines and comments
                }
                Integer roomNumber = null;
                String roomName = null;
                String roomDescription = null;
                String[] inputCell = inputLine.split(CSV_SEPERATOR);
                try {
                    roomNumber = Integer.parseInt(inputCell[0]);
                    if (roomsAlreadyProcessed.contains(roomNumber)) {
                        logmsg.append("Room ID (");
                        logmsg.append(inputCell[0]);
                        logmsg.append(") already processed\n");
                        logger.log(Level.SEVERE, logmsg.toString());
                        System.exit(-2);
                    } else {
                        roomsAlreadyProcessed.add(roomNumber);
                        logger.log(Level.FINE, "Room " + Integer.toString(roomNumber));
                    }
                    roomName = inputCell[1].trim();
                    roomDescription = inputCell[2].trim();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    logmsg.append("Room ID not of type Integer!");
                    logger.log(Level.SEVERE, logmsg.toString(), e);
                    System.exit(-2);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    logmsg.append("insufficient parameters in line");
                    logger.log(Level.SEVERE, logmsg.toString(), e);
                    System.exit(-2);
                }

                logmsg.append("Room ID: ");
                logmsg.append(roomNumber);
                logmsg.append(" Name: ");
                logmsg.append(roomName);
                logmsg.append(" Description: ");
                logmsg.append(roomDescription);
                logger.log(Level.INFO, logmsg.toString());
                logmsg = new StringBuffer();
                roomMapEntrytmp.put(roomNumber, new RoomEntry(roomNumber, roomName, roomDescription));
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "IOException while loading rooms ");
            System.exit(-2);
        }
        this.roomMapEntry = roomMapEntrytmp;
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

    static final class RoomEntry {
        private final int roomID;
        private final String name;
        private final String description;
        private final Logger logger = Logger.getLogger("RoomEntry");

        RoomEntry(int roomID, String name, String description) {
            if (roomID < 0) {
                logger.log(Level.SEVERE, "Instantiation of Object failed, RoomID ("
                        + roomID + ") must be positive");
                System.exit(-2);
            }
            this.roomID = roomID;
            this.name = name;
            this.description = description;
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
}