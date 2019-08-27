package de.stefanschade.AdventureGame.framework.datamodel.immutables;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class RoomMapCSV extends ImmutableCSV<RoomMap> {

    // parse
    private int keyCurrentLine;
    private String roomName;
    private String roomDescription;
    private Set keysAlreadyProcessed = new HashSet<>();
    private Room tmpRoom;
    private Map<Integer, Room> tmpRoomMap = new HashMap<>();


    public RoomMapCSV(String filename) {
        super(filename);
    }


    @Override
    protected void parse(String[] inputCell) {
        keyCurrentLine = Integer.parseInt(inputCell[0]);
        roomName = inputCell[1].trim();
        roomDescription = inputCell[2].trim();
    }

    @Override
    protected void setTempFieldsAccordingToCurrentParse() {
        if (keysAlreadyProcessed.contains(keyCurrentLine)) {

            String errormsg = "file " + filename + " room " + keyCurrentLine + " in line " + lineCounter
                    + " has already been processed. Double entries are not allowed";

            LOGGER.log(Level.SEVERE, errormsg);
            throw new IllegalArgumentException(errormsg);

        } else {
            keysAlreadyProcessed.add(keyCurrentLine);
        }
    }

    @Override
    protected void populateTemporaryObjectsAfterParse() {
        tmpRoom = new Room(keyCurrentLine, roomName, roomDescription);
        tmpRoomMap.put(tmpRoom.getID(), tmpRoom);
    }

    @Override
    protected void prepareTempFieldsForNextParse() {
        // empty implementation is intended
    }


    @Override
    protected RoomMap constructReturnObjectAfterParse() {
        return new RoomMap(tmpRoomMap);
    }
}