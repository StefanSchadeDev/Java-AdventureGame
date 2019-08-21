package de.stefanschade.AdventureGame.framework.datamodel.immutables;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RoomFile {

    private static final Logger logger = Logger.getLogger(RoomMap.class.getName());
    private static final String CSV_SEPERATOR = ";";

    static RoomMap readMapFromFile(String filename) throws IOException {
        logger.log(Level.INFO, "parsing file: " + filename);
        Map<Integer, Room> temporaryRoomMap = new HashMap<>();
        BufferedReader br = Files.newBufferedReader(Paths.get(filename));
        Set<Integer> roomsAlreadyProcessed = new HashSet<>();
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            Room temporaryRoom = parseOneLine(roomsAlreadyProcessed, inputLine);
            if (temporaryRoom != null) temporaryRoomMap.put(temporaryRoom.getID(), temporaryRoom);
        }

        return new RoomMap(temporaryRoomMap);
    }

    private static Room parseOneLine(Set<Integer> roomsAlreadyProcessed, String inputLine) {
        if (inputLine.trim().isEmpty() || inputLine.startsWith("#")) {
            return null;
        }
        Integer roomNumber = null;
        String roomName = null;
        String roomDescription = null;
        String[] inputCell = inputLine.split(CSV_SEPERATOR);
        try {

            roomNumber = Integer.parseInt(inputCell[0]);
            roomName = inputCell[1].trim();
            roomDescription = inputCell[2].trim();

            if (roomsAlreadyProcessed.contains(roomNumber)) {
                throw new IllegalArgumentException("Room ID " + inputCell[0] + "already processed");
            } else {
                roomsAlreadyProcessed.add(roomNumber);
                logger.log(Level.FINE, "Room " + Integer.toString(roomNumber));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Room ID not an int");
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Insufficient parameters for room");
        }
        return new Room(roomNumber, roomName, roomDescription);
    }
}