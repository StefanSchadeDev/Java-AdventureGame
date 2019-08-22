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

    // static constants
    private static final Logger LOGGER = Logger.getLogger(RoomFile.class.getName());
    private static final String CSV_SEPERATOR = ";";
    private final String filename;

    // object has to be instantiated for each parse
    private boolean objectAlreadyUsed = false;

    // fields to store the parse info of each line
    private Integer roomNumber = null;
    private String roomName = null;
    private String roomDescription = null;
    private int currentLine = 0;

    // containers collect data to populate immutable in one go
    private Map<Integer, Room> tmpRoomMap = new HashMap<>();
    private Room tmpRoom;

    // temporary information on parsing operation exceeding single line scope
    private Set<Integer> roomsAlreadyProcessed = new HashSet<>();
    private boolean eofReachedFlag = false;

    RoomFile(String filename) {
        this.filename = filename;
    }

    RoomMap readMapFromFile() throws IOException {

        LOGGER.log(Level.INFO, "parsing file: " + filename);

        ensureThisObjectOnlyUsedOnce();
        BufferedReader br = null;
        try {
            br = Files.newBufferedReader(Paths.get(filename));
            while (!eofReachedFlag) {
                String currentLine = br.readLine();
                if (!isEOF(currentLine)) {
                    if (isComment(currentLine)) continue;

                    parse(currentLine);
                    setTempFieldsAccordingToCurrentParse();
                }
                populateTemporaryObjectsAfterParse();  //  also necessary at EOF to process last line
                prepareTempFieldsForNextParse(); // does not harm at EOF
            }
        } finally {
            br.close();
        }
        return new RoomMap(tmpRoomMap);
    }

    private void parse(String currentLine) {
        LOGGER.log(Level.FINEST, "parsing line " + this.currentLine++);
        String[] inputCell = currentLine.split(CSV_SEPERATOR);
        roomNumber = Integer.parseInt(inputCell[0]);
        roomName = inputCell[1].trim();
        roomDescription = inputCell[2].trim();
    }

    private void prepareTempFieldsForNextParse() {
        tmpRoom = new Room(roomNumber, roomName, roomDescription);
    }

    private void setTempFieldsAccordingToCurrentParse() {

        if (roomsAlreadyProcessed.contains(roomNumber)) {

            String errormsg = "file " + filename + " room " + roomNumber + " in line " + currentLine
                    + " has already been processed. Double entries are not allowed";

            LOGGER.log(Level.SEVERE, errormsg);
            throw new IllegalArgumentException(errormsg);

        } else {
            roomsAlreadyProcessed.add(roomNumber);
        }
    }

    private boolean isEOF(String currentLine) {
        if (currentLine == null) {
            LOGGER.log(Level.FINE, filename + " EOF reached");
            eofReachedFlag = true;
            return true;
        } else {
            return false;
        }
    }

    private void populateTemporaryObjectsAfterParse() {
        tmpRoom = new Room(roomNumber,roomName,roomDescription);
        tmpRoomMap.put(tmpRoom.getID(), tmpRoom);
    }

    private void ensureThisObjectOnlyUsedOnce() {
        if (objectAlreadyUsed) {
            throw new IllegalArgumentException("PassageFile Object has to be instantiated for each parse operation");
        } else {
            objectAlreadyUsed = true;
        }
    }

    private boolean isComment(String currentLine) {
        return (currentLine.trim().isEmpty() || currentLine.startsWith("#"));
    }
}