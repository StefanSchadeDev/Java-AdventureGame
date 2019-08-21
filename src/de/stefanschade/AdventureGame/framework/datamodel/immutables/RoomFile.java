package de.stefanschade.AdventureGame.framework.datamodel.immutables;

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

public class RoomFile {

    private static final Logger logger = Logger.getLogger(RoomMap.class.getName());
    private static final String CSV_SEPERATOR = ";";

    static Map<Integer, Room> readMapFromFile(String filename) {
        StringBuffer logmsg = new StringBuffer();
        logmsg.append("parsing file: ");

        // ENSURE FILENAME IS SET AT ALL

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

        Map<Integer, Room> temporaryRoomMap = new HashMap<>();

        try {
            Path path = Paths.get(filename);
            BufferedReader br = Files.newBufferedReader(path);

            Set<Integer> roomsAlreadyProcessed = new HashSet<>();
            int line = 0;
            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                Room temporaryRoom = parseOneLine(roomsAlreadyProcessed, line, inputLine);
                if (temporaryRoom != null) temporaryRoomMap.put(temporaryRoom.getID(), temporaryRoom);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "IOException while loading rooms ");
            System.exit(-2);
        }

        return temporaryRoomMap;

    }

    private static Room parseOneLine(Set<Integer> roomsAlreadyProcessed, int line, String inputLine) {
        StringBuffer logmsg;
        line++;
        logmsg = new StringBuffer();
        logmsg.append(" #");
        logmsg.append(line);
        logmsg.append(" -> ");
        if (inputLine.trim().isEmpty() || inputLine.startsWith("#")) {
            logmsg.append("empty");
            return null;
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
        return new Room(roomNumber, roomName, roomDescription);
    }
}
