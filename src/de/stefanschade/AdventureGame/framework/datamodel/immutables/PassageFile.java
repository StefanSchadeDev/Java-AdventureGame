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

public class PassageFile {

    // static constants
    private static final Logger LOGGER = Logger.getLogger(PassageFile.class.getName());
    private static final String CSV_SEPERATOR = ";";
    // object has to be instantiated for each parse
    private boolean objectAlreadyUsed = false;
    // containers collect data to populate immutable in one go
    private Map<Integer, Directions> tmpDataForPassageMap = new HashMap<>();
    private Map<String, Passage> tmpDataForDirections = new HashMap<>();

    // temporary information on parsing operation exceeding single line scope
    private Set<Integer> roomsOfOriginAlreadyProcessed = new HashSet<>();
    private Integer roomOfOriginLastLine = null;
    private boolean firstLineOfBlockFlag = true;
    private boolean eofReachedFlag = false;
    private boolean previousBlockFinishedFlag = false;

    // fields to store the parse info of each line
    private Integer currentRoomOfOrigin = null;
    private String currentDirectionString = null;
    private Integer currentDestinationRoom = null;

    private String filename;
    private int currentLine = 0;

    public PassageFile(String filename) {
        this.filename = filename;
    }

    public PassageMap readMapFromFile() throws IOException {
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
                populateTemporaryObjects();  //  also necessary at EOF
                prepareTempFieldsForNextParse(); // does not harm at EOF
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO Exception when accessing PassageFile " + filename);
            throw e;
        } finally {
            br.close();
        }
        return new PassageMap(tmpDataForPassageMap);
    }

    private void ensureThisObjectOnlyUsedOnce() {
        if (objectAlreadyUsed) {
            throw new IllegalArgumentException("PassageFile Object has to be instantiated for each parse operation");
        } else {
            objectAlreadyUsed = true;
        }
    }

    private void setTempFieldsAccordingToCurrentParse() {
        firstLineOfBlockFlag = (currentRoomOfOrigin != roomOfOriginLastLine);
        previousBlockFinishedFlag = (firstLineOfBlockFlag && roomsOfOriginAlreadyProcessed.size() > 0);

        if (roomsOfOriginAlreadyProcessed.contains(currentRoomOfOrigin)) {
            if (firstLineOfBlockFlag) {

                String errormsg = "file " + filename + " room of origin " + currentRoomOfOrigin + " in line " + currentLine
                        + " has already been processed. Once a block is finished, new entries are not allowed";

                LOGGER.log(Level.SEVERE, errormsg);
                throw new IllegalArgumentException(errormsg);
            }
        } else {
            roomsOfOriginAlreadyProcessed.add(currentRoomOfOrigin);
        }
    }

    private boolean isComment(String currentLine) {
        return (currentLine.trim().isEmpty() || currentLine.startsWith("#"));
    }

    private void populateTemporaryObjects() {

        if (previousBlockFinishedFlag) {  // roomOfOrigin is completed
            // instantiate immutable for this roomOfOrigin and add it to the temporary Map
            Directions dir = new Directions(roomOfOriginLastLine, tmpDataForDirections);
            tmpDataForPassageMap.put(roomOfOriginLastLine, dir);
            // flush temporary object
            tmpDataForDirections = new HashMap<>();
        }

        // in any case process current line
        tmpDataForDirections.put(currentDirectionString, new Passage(currentDestinationRoom));

    }

    private void prepareTempFieldsForNextParse() {
        roomOfOriginLastLine = currentRoomOfOrigin;
        firstLineOfBlockFlag = false;
    }

    private boolean isEOF(String currentLine) {

        if (currentLine == null) {
            LOGGER.log(Level.FINE, filename + " EOF reached");
            eofReachedFlag = true;
            previousBlockFinishedFlag = true;
            return true;
        } else {
            return false;
        }
    }

    private void parse(String currentLine) {

        LOGGER.log(Level.FINEST, "parsing line " + this.currentLine++);
        String[] inputCell = currentLine.split(CSV_SEPERATOR);

        currentRoomOfOrigin = Integer.parseInt(inputCell[0]);
        currentDirectionString = inputCell[1].trim();
        currentDestinationRoom = Integer.parseInt(inputCell[2]);

        return;
    }
}