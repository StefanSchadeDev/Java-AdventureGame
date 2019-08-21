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

    public static PassageMap readMapFromFile(String filename) throws IOException {

        Map<Integer, PassagesByOrigin> exitsByOriginTMP = new HashMap<>();

        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);

        PassageFileParser passageFileParser = new PassageFileParser(exitsByOriginTMP);

        while (!passageFileParser.isEofReached()) {
            passageFileParser.parseNextLine(br.readLine());
        }
        return new PassageMap(exitsByOriginTMP);
    }

    private static class PassageFileParser {

        // the return object
        private Map<Integer, PassagesByOrigin> exitsByOriginTMP;

        // temporary structure
        private Map<String, Passage> mapDirectionExitTMP= new HashMap<>();

        // temporary information on parsing operation exceeding single line scope
        private Set<Integer> currentOriginAlreadyProcessed = new HashSet<>();
        private Integer roomOfOriginLastLine = null;
        private boolean firstLineOfBlockFlag = true;
        private boolean eofReachedFlag = false;

        // private int line;

        public PassageFileParser(Map<Integer, PassagesByOrigin> exitsByOriginTMP) {
            this.exitsByOriginTMP = exitsByOriginTMP;
        }

        public Map<String, Passage> getMapDirectionExitTMP() {
            return mapDirectionExitTMP;
        }

        public Integer getRoomOfOriginLastLine() {
            return roomOfOriginLastLine;
        }

        public boolean isFirstLineOfBlockFlag() {
            return firstLineOfBlockFlag;
        }

        public boolean isEofReached() {
            return eofReachedFlag;
        }

        public PassageFileParser parseNextLine(String inputLine) throws IOException {

            boolean previousBlockFinishedFlag;
            Integer currentRoomOfOrigin = null;
            String currentDirectionString = null;
            Integer currentDestinationRoom = null;
//            line++;

            // eof reached
            if (inputLine == null) {
                logger.log(Level.INFO, "eof reached");
                eofReachedFlag = true;
                previousBlockFinishedFlag = true;
            } else {
                if (inputLine.trim().isEmpty() || inputLine.startsWith("#")) {
                    return this; // line is a comment and therefore ignored
                }

                String[] inputCell = inputLine.split(CSV_SEPERATOR);
                currentRoomOfOrigin =  Integer.parseInt(inputCell[0]);
                currentDirectionString = inputCell[1].trim();
                currentDestinationRoom = Integer.parseInt(inputCell[2]);

                firstLineOfBlockFlag = (currentRoomOfOrigin != roomOfOriginLastLine);
                previousBlockFinishedFlag = (firstLineOfBlockFlag && currentOriginAlreadyProcessed.size() > 0);

                if (currentOriginAlreadyProcessed.contains(currentRoomOfOrigin)) {
                    if (firstLineOfBlockFlag) {
                        logger.log(Level.WARNING, "Block for Origin #" + currentRoomOfOrigin
                                + " already processed, ignoring line ");
                        return this;
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
                return this;
            }
            // if processing new origin block, flush the temporary object
            if (firstLineOfBlockFlag) {
                mapDirectionExitTMP = new HashMap<String, Passage>();
            }
            mapDirectionExitTMP.put(currentDirectionString, new Passage(currentDestinationRoom));
            //reset flags before parsing the next line
            roomOfOriginLastLine = currentRoomOfOrigin;
            firstLineOfBlockFlag = false;
            return this;
        }

    }
}
