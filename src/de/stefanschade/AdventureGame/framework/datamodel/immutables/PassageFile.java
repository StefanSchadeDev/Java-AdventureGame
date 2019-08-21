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

public class PassageFile {

    private static final Logger logger = Logger.getLogger(PassageFile.class.getName());
    private static final String CSV_SEPERATOR = ";";

    public static PassageMap readMapFromFile(String filename) {

        Map<Integer, PassagesByOrigin> exitsByOriginTMP = new HashMap<>();
        Map<String, Passage> mapDirectionExitTMP = new HashMap<>();
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
                // after origin block, construct an immutable instance of PassagesByOrigin and append it to exitsByOriginTMP
                if (flagOriginBlockWasFinished) {
                    exitsByOriginTMP.put(lastRoomOfOrigin, new PassagesByOrigin(lastRoomOfOrigin, new HashMap<String, Passage>(mapDirectionExitTMP)));
                }
                // at the end of the file, construct an immutable instance of field exitsByOrigin
                if (flagParseFinished) {
                    continue;
                }
                // if processing new origin block, flush the temporary object
                if (flagFirstLineOfOriginBlock) {
                    mapDirectionExitTMP = new HashMap<String, Passage>();
                }
                mapDirectionExitTMP.put(currentDirectionString, new Passage(currentDestinationRoom));
                //reset flags before parsing the next line
                lastRoomOfOrigin = currentRoomOfOrigin;
                flagFirstLineOfOriginBlock = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "IOException while loading rooms ");
            System.exit(-2);
        }

        return new PassageMap(exitsByOriginTMP);

    }
}
