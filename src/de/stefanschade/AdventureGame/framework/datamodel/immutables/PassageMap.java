package de.stefanschade.AdventureGame.framework.datamodel.immutables;

import net.jcip.annotations.Immutable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Immutable
public class Passages {
    private String name;
    private Map<Integer, ExitsForOneOrigin> exitsByOrigin;
    private final Logger logger = Logger.getLogger(Passages.class.getName());
    private static final String CSV_SEPERATOR = ";";


    public Passages(String filename) {
        if (filename == null) {
            logger.log(Level.SEVERE, "Passages constructor was called with filename = null");
            System.exit(-2);
        } else if (filename.isEmpty()) {
            logger.log(Level.SEVERE, "Passages constructor was called with empty filename");
            System.exit(-2);
        }
        this.name = filename;
        // Used to collect information prior to constructor call
        Map<Integer, ExitsForOneOrigin> exitsByOriginTMP = new HashMap<>();
        Map<String, Exit> mapDirectionExitTMP = new HashMap<>();
        // parsing info
        Set<Integer> roomsOfOriginAlreadyProcessed = new HashSet<>();
        Integer lastRoomOfOrigin = null;
        boolean flagOriginBlockWasFinished = false;
        boolean flagFirstLineOfOriginBlock = true;
        boolean flagParseFinished = false;
        String inputLine;

        int line = 0;
        try {
            Path path = Paths.get(filename);
            BufferedReader br = Files.newBufferedReader(path);
            while (!flagParseFinished) {
                inputLine = br.readLine();
                Integer currentRoomOfOrigin = null;
                String currentDirectionString = null;
                Integer currentDestinationRoom = null;
                line++;

                // eof found
                if (inputLine == null) {
                    logger.log(Level.INFO, "Parsing file " + filename + " -> EOF");
                    flagParseFinished = true;
                    flagOriginBlockWasFinished = true;
                } else {
                    if (inputLine.trim().isEmpty() || inputLine.startsWith("#")) {
                        continue;
                    }
                    String[] inputCell = inputLine.split(CSV_SEPERATOR);
                    try {
                        currentRoomOfOrigin = Integer.parseInt(inputCell[0]);
                        currentDirectionString = inputCell[1].trim();
                        currentDestinationRoom = Integer.parseInt(inputCell[2]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        logger.log(Level.SEVERE, "Format error in line " + line
                                + " of file: " + filename);
                        System.exit(-2);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        logger.log(Level.SEVERE, "Insufficent parameters supplied in line " + line
                                + " of file: " + filename);
                        System.exit(-2);
                    }
                    flagFirstLineOfOriginBlock = (currentRoomOfOrigin != lastRoomOfOrigin);
                    flagOriginBlockWasFinished = (flagFirstLineOfOriginBlock &&
                            roomsOfOriginAlreadyProcessed.size() > 0) || flagParseFinished;

                    if (roomsOfOriginAlreadyProcessed.contains(currentRoomOfOrigin)) {
                        if (flagFirstLineOfOriginBlock) {
                            logger.log(Level.WARNING, "Block for Origin #" + currentRoomOfOrigin
                                    + " already processed, ignoring line " + line);
                            continue; // ignore this line of the csv-file
                        }
                    } else {
                        roomsOfOriginAlreadyProcessed.add(currentRoomOfOrigin);
                    }
                }
                // after origin block, construct an immutable instance of ExitsForOneOrigin and append it to exitsByOriginTMP
                if (flagOriginBlockWasFinished) {
                    exitsByOriginTMP.put(lastRoomOfOrigin, new ExitsForOneOrigin(lastRoomOfOrigin, new HashMap<String, Exit>(mapDirectionExitTMP)));
                }
                // at the end of the file, construct an immutable instance of field exitsByOrigin
                if (flagParseFinished) {
                    this.exitsByOrigin = new HashMap<Integer, ExitsForOneOrigin>(exitsByOriginTMP);
                    continue;
                }
                // if processing new origin block, flush the temporary object
                if (flagFirstLineOfOriginBlock) {
                    mapDirectionExitTMP = new HashMap<String, Exit>();
                }
                mapDirectionExitTMP.put(currentDirectionString, new Exit(currentDestinationRoom));
                //reset flags before parsing the next line
                lastRoomOfOrigin = currentRoomOfOrigin;
                flagFirstLineOfOriginBlock = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "IOException while loading rooms ");
            System.exit(-2);
        }
    }

    private ExitsForOneOrigin getPassage(int i) {
        if (!exitsByOrigin.containsKey(i)) {
            logger.log(Level.SEVERE, "ExitsForOneOrigin " + i + " not found in map " + exitsByOrigin);
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

    // static class, as we need the constructor before we have an instance of the immutable enclosing class.
    // Association is modelled via the map in the enclosing class
    @Immutable
    static final class Exit {
        private final int destination;

        int getDestination() {
            return destination;
        }

        Exit(int destination) {
            this.destination = destination;
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

    @Immutable
    static final class ExitsForOneOrigin {
        private final Map<String, Exit> mapDirectionToDestination;
        private final int originRoomID;


        ExitsForOneOrigin(int roomOfOrigin, Map<String, Exit> exits) {
            if (exits == null) {
                this.mapDirectionToDestination = new HashMap<>();
            } else {
                this.mapDirectionToDestination = new HashMap<>(exits);
            }
            this.originRoomID = roomOfOrigin;
        }

        String getDirections() {
            StringBuffer sb = new StringBuffer();
            Set<String> keys = this.mapDirectionToDestination.keySet();
            Iterator<String> it = keys.iterator();
            if (this.mapDirectionToDestination.size() == 0) {
                sb.append("There is no exit here");
            } else if (this.mapDirectionToDestination.size() == 1) {
                sb.append("There is an exit to the ");
                sb.append(it.next());
            } else {
                sb.append("There are exits to the ");
                for (int i = 1; i < this.mapDirectionToDestination.size(); i++) {
                    sb.append(it.next());
                    if (i == this.mapDirectionToDestination.size() - 1) {
                        sb.append(" and ");
                    } else {
                        sb.append(", ");
                    }
                }
                sb.append(it.next());
            }
            sb.append("!");
            return sb.toString();
        }

        boolean isValidPassage(String direction) {
            return this.mapDirectionToDestination.containsKey(direction);
        }

        int getDestination(String direction) {
            if (this.mapDirectionToDestination.containsKey(direction)) {
                Exit psg = this.mapDirectionToDestination.get(direction);
                return psg.getDestination();
            } else {
                return -1;
            }
        }
    }
}