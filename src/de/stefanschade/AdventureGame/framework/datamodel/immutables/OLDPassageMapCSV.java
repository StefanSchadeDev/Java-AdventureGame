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

@Deprecated
class OLDPassageMapCSV {

    // static constants
    private static final Logger LOGGER = Logger.getLogger(OLDPassageMapCSV.class.getName());
    private static final String CSV_SEPERATOR = ";";

    // object has to be instantiated for each parse
    private boolean objectAlreadyUsed = false;

    // containers collect data to populate immutable in one go
    private Map<Integer, PassagesOneRoom> tmpDataForPassageMap = new HashMap<>();
    private Map<String, Passage> tmpDataForDirections = new HashMap<>();

    // temporary information on parsing operation exceeding single line scope
    private Set<Integer> blockKeysAlreadyProcessed = new HashSet<>();
    private Integer blockKeyLastLine = null;
    private boolean firstLineOfBlockFlag = true;
    private boolean eofReachedFlag = false;
    private boolean previousBlockFinishedFlag = false;

    // fields to store the parse info of each line
    private Integer blockKeyCurrentLine = null;
    private String currentDirectionString = null;
    private Integer currentDestinationRoom = null;

    private final String filename;
    private int lineCounter = 0;

    OLDPassageMapCSV(String filename) {
        this.filename = filename;
    }

    PassageMap getFromCSV() throws IOException {

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
        return new PassageMap(tmpDataForPassageMap);
    }

    private void ensureThisObjectOnlyUsedOnce() {
        if (objectAlreadyUsed) {
            throw new IllegalArgumentException("OLDPassageMapCSV Object has to be instantiated for each parse operation");
        } else {
            objectAlreadyUsed = true;
        }
    }

    private void setTempFieldsAccordingToCurrentParse() {
        firstLineOfBlockFlag = (blockKeyCurrentLine != blockKeyLastLine);
        previousBlockFinishedFlag = (firstLineOfBlockFlag && blockKeysAlreadyProcessed.size() > 0);

        if (blockKeysAlreadyProcessed.contains(blockKeyCurrentLine)) {
            if (firstLineOfBlockFlag) {

                String errormsg = "file " + filename + " room of origin " + blockKeyCurrentLine + " in line " + lineCounter
                        + " has already been processed. Once a block is finished, new entries are not allowed";

                LOGGER.log(Level.SEVERE, errormsg);
                throw new IllegalArgumentException(errormsg);
            }
        } else {
            blockKeysAlreadyProcessed.add(blockKeyCurrentLine);
        }
    }

    private boolean isComment(String currentLine) {
        return (currentLine.trim().isEmpty() || currentLine.startsWith("#"));
    }

    private void populateTemporaryObjectsAfterParse() {

        if (previousBlockFinishedFlag) {  // block of data is completed
            // instantiate immutable for this block of data and add it to the temporary Map
            PassagesOneRoom dir = new PassagesOneRoom(blockKeyLastLine, tmpDataForDirections);
            tmpDataForPassageMap.put(blockKeyLastLine, dir);
            // flush temporary object
            tmpDataForDirections = new HashMap<>();
        }
        // in any case process current line
        tmpDataForDirections.put(currentDirectionString, new Passage(currentDestinationRoom));
    }

    private void prepareTempFieldsForNextParse() {
        blockKeyLastLine = blockKeyCurrentLine;
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

        LOGGER.log(Level.FINEST, "parsing line " + this.lineCounter++);
        String[] inputCell = currentLine.split(CSV_SEPERATOR);
        blockKeyCurrentLine = Integer.parseInt(inputCell[0]);
        currentDirectionString = inputCell[1].trim();
        currentDestinationRoom = Integer.parseInt(inputCell[2]);
        return;
    }
}