package de.stefanschade.AdventureGame.framework.datamodel.immutables;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class PassageMapCSV extends ImmutableCSV<PassageMap> {

    private int blockKeyLastLine;
    private int blockKeyCurrentLine;
    private String currentDirectionString;
    private int currentDestinationRoom;
    private boolean firstLineOfBlockFlag;
    private Set<Integer> blockKeysAlreadyProcessed = new HashSet<>();
    private Map<String, Passage> tmpDataForDirections = new HashMap<>();
    private Map<Integer, PassagesOneRoom> tmpDataForPassageMap = new HashMap<>();

    public PassageMapCSV(String filename) {
        super(filename);
    }

    @Override
    protected void parse(String[] inputCell) {
        blockKeyCurrentLine = Integer.parseInt(inputCell[0]);
        currentDirectionString = inputCell[1].trim();
        currentDestinationRoom = Integer.parseInt(inputCell[2]);
    }

    @Override
    protected void setTempFieldsAccordingToCurrentParse() {
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

    @Override
    protected void populateTemporaryObjectsAfterParse() {

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

    @Override
    protected void prepareTempFieldsForNextParse() {
        blockKeyLastLine = blockKeyCurrentLine;
        firstLineOfBlockFlag = false;
    }

    @Override
    protected PassageMap constructReturnObjectAfterParse() {
        return new PassageMap(tmpDataForPassageMap);
    }
}