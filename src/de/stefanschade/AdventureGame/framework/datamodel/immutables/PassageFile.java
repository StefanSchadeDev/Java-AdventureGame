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

    // the return object
    private Map<Integer, PassagesByOrigin> exitsByOriginTMP = new HashMap<>();
    private String filename;

    // temporary information on parsing operation exceeding single line scope
    private Set<Integer> currentOriginAlreadyProcessed = new HashSet<>();
    private Map<String, Passage> mapDirectionExitTMP = new HashMap<>();
    private Integer roomOfOriginLastLine = null;
    private boolean firstLineOfBlockFlag = true;
    private boolean eofReachedFlag = false;
    private int currentLine = 0;

    public PassageFile(String filename) {
        this.filename = filename;
    }

    public PassageMap readMapFromFile() throws IOException {

        BufferedReader br = Files.newBufferedReader(Paths.get(filename));

        while (!eofReachedFlag) {
            parseNextLine(br.readLine());
        }

        PassageMap returnValue = new PassageMap(exitsByOriginTMP);

        LOGGER.log(Level.FINEST, "XXX checking room 1: " + returnValue.validateRoomOfOrigin(1));

        return returnValue;
    }

    public void parseNextLine(String inputLine) throws IOException {

        boolean previousBlockFinishedFlag;
        Integer currentRoomOfOrigin = null;
        String currentDirectionString = null;
        Integer currentDestinationRoom = null;

        LOGGER.log(Level.FINEST, "parsing line " + currentLine++);

        // eof reached
        if (inputLine == null) {
            LOGGER.log(Level.FINEST, "eof reached");
            eofReachedFlag = true;
            previousBlockFinishedFlag = true;
        } else {
            if (inputLine.trim().isEmpty() || inputLine.startsWith("#")) {
                LOGGER.log(Level.FINEST, "ignoring comment");
                return;
            }

            String[] inputCell = inputLine.split(CSV_SEPERATOR);
            currentRoomOfOrigin = Integer.parseInt(inputCell[0]);
            currentDirectionString = inputCell[1].trim();
            currentDestinationRoom = Integer.parseInt(inputCell[2]);

            LOGGER.log(Level.FINEST,
                    "parsed: " + currentRoomOfOrigin +
                            " / " + currentDirectionString +
                            " / " + currentDestinationRoom);

            firstLineOfBlockFlag = (currentRoomOfOrigin != roomOfOriginLastLine);
            previousBlockFinishedFlag = (firstLineOfBlockFlag && currentOriginAlreadyProcessed.size() > 0);

            if (currentOriginAlreadyProcessed.contains(currentRoomOfOrigin)) {
                if (firstLineOfBlockFlag) {
                    LOGGER.log(Level.WARNING, "Block for Origin #" + currentRoomOfOrigin
                            + " already processed, ignoring line ");
                    return;
                }
            } else {
                currentOriginAlreadyProcessed.add(currentRoomOfOrigin);
            }
        }
        // after  block, construct an immutable instance of PassagesByOrigin and append it to exitsByOriginTMP
        if (previousBlockFinishedFlag) {
            exitsByOriginTMP.put(roomOfOriginLastLine,
                    new PassagesByOrigin(roomOfOriginLastLine, new HashMap<String, Passage>(mapDirectionExitTMP)));
        }
        // at the end of the file, construct an immutable instance of field exitsByOrigin
        if (eofReachedFlag) {
            return;
        }
        // if processing new origin block, flush the temporary object
        if (firstLineOfBlockFlag) {
            mapDirectionExitTMP = new HashMap<String, Passage>();
        }
        mapDirectionExitTMP.put(currentDirectionString, new Passage(currentDestinationRoom));
        //reset flags before parsing the next line
        roomOfOriginLastLine = currentRoomOfOrigin;
        firstLineOfBlockFlag = false;

        return;
    }
}