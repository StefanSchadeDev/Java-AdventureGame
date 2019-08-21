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
        Map<String, Passage> mapDirectionExitTMP = new HashMap<>();
        Set<Integer> roomsOfOriginAlreadyProcessed = new HashSet<>();

        Integer roomOfOriginLastLine = null;
        boolean flagOriginBlockWasFinished = false;
        boolean flagFirstLineOfOriginBlock = true;
        boolean eofReachedFlag = false;

        int line = 0;

        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);
        while (!eofReachedFlag) {

            PassageFileParser passageFileParser = new PassageFileParser(filename,
                    exitsByOriginTMP,
                    mapDirectionExitTMP,
                    roomsOfOriginAlreadyProcessed,
                    roomOfOriginLastLine,
                    flagFirstLineOfOriginBlock,
                    eofReachedFlag
            ).parseNextLine(br.readLine());

            mapDirectionExitTMP = passageFileParser.getMapDirectionExitTMP();
            roomOfOriginLastLine = passageFileParser.getRoomOfOriginLastLine();
            flagFirstLineOfOriginBlock = passageFileParser.isFirstLineOfOriginBlockFlag();
            eofReachedFlag = passageFileParser.isflagEOFwasReached();


        }
        return new PassageMap(exitsByOriginTMP);
    }

    private static class PassageFileParser {
        private String filename;

        // temporary objects to build up the parsed information before using it to instantiate the immutable objects
        private Map<Integer, PassagesByOrigin> exitsByOriginTMP;
        private Map<String, Passage> mapDirectionExitTMP;

        // temporary information on parsing operation exceeding single line scope
        private Set<Integer> roomsOfOriginAlreadyProcessed;
        private Integer roomOfOriginLastLine;
        private boolean firstLineOfOriginBlockFlag;
        private boolean eofReachedFlag;

        // private int line;

        public PassageFileParser(String filename,
                                 Map<Integer, PassagesByOrigin> exitsByOriginTMP,
                                 Map<String, Passage> mapDirectionExitTMP,
                                 Set<Integer> roomsOfOriginAlreadyProcessed,
                                 Integer roomOfOriginLastLine,
                                 boolean firstLineOfOriginBlockFlag,
                                 boolean eofReachedFlag) {

            this.filename = filename;
            this.exitsByOriginTMP = exitsByOriginTMP;
            this.mapDirectionExitTMP = mapDirectionExitTMP;
            this.roomsOfOriginAlreadyProcessed = roomsOfOriginAlreadyProcessed;
            this.roomOfOriginLastLine = roomOfOriginLastLine;
            this.firstLineOfOriginBlockFlag = firstLineOfOriginBlockFlag;
            this.eofReachedFlag = eofReachedFlag;
        }

        // todo> evaluate wether this method and the equivalent code sections in class RoomFile provide benefit and refactor
        private static Integer parseInt(String[] input, int order) {
            Integer returnValue;
            try {
                returnValue = Integer.parseInt(input[order]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Number format error in parse file");
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Insufficient parameters supplied in  file");
            }
            return returnValue;
        }

        private static String parseString(String[] input, int order) {
            String returnValue;
            try {
                returnValue = input[order].trim();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Number format error in passage file");
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Insufficient parameters supplied in passage file");
            }
            return returnValue;
        }

        public Map<String, Passage> getMapDirectionExitTMP() {
            return mapDirectionExitTMP;
        }

        public Integer getRoomOfOriginLastLine() {
            return roomOfOriginLastLine;
        }

        public boolean isFirstLineOfOriginBlockFlag() {
            return firstLineOfOriginBlockFlag;
        }

        public boolean isflagEOFwasReached() {
            return eofReachedFlag;
        }

        public PassageFileParser parseNextLine(String inputLine) throws IOException {

            boolean previousOriginBlockFinishedFlag;
            Integer currentRoomOfOrigin = null;
            String currentDirectionString = null;
            Integer currentDestinationRoom = null;
//            line++;

            // eof reached
            if (inputLine == null) {
                logger.log(Level.INFO, "Parsing file " + filename + " -> EOF");
                eofReachedFlag = true;
                previousOriginBlockFinishedFlag = true;
            } else {
                if (inputLine.trim().isEmpty() || inputLine.startsWith("#")) {
                    return this; // line is a comment and therefore ignored
                }

                String[] inputCell = inputLine.split(CSV_SEPERATOR);
                currentRoomOfOrigin = parseInt(inputCell, 0);
                currentDirectionString = parseString(inputCell, 1);
                currentDestinationRoom = parseInt(inputCell, 2);

                firstLineOfOriginBlockFlag = (currentRoomOfOrigin != roomOfOriginLastLine);
                previousOriginBlockFinishedFlag = (firstLineOfOriginBlockFlag && roomsOfOriginAlreadyProcessed.size() > 0);

                if (roomsOfOriginAlreadyProcessed.contains(currentRoomOfOrigin)) {
                    if (firstLineOfOriginBlockFlag) {
                        logger.log(Level.WARNING, "Block for Origin #" + currentRoomOfOrigin
                                + " already processed, ignoring line ");
                        return this;
                    }
                } else {
                    roomsOfOriginAlreadyProcessed.add(currentRoomOfOrigin);
                }
            }
            // after origin block, construct an immutable instance of PassagesByOrigin and append it to exitsByOriginTMP
            if (previousOriginBlockFinishedFlag) {
                exitsByOriginTMP.put(roomOfOriginLastLine,
                        new PassagesByOrigin(roomOfOriginLastLine, new HashMap<String, Passage>(mapDirectionExitTMP)));
            }
            // at the end of the file, construct an immutable instance of field exitsByOrigin
            if (eofReachedFlag) {
                return this;
            }
            // if processing new origin block, flush the temporary object
            if (firstLineOfOriginBlockFlag) {
                mapDirectionExitTMP = new HashMap<String, Passage>();
            }
            mapDirectionExitTMP.put(currentDirectionString, new Passage(currentDestinationRoom));
            //reset flags before parsing the next line
            roomOfOriginLastLine = currentRoomOfOrigin;
            firstLineOfOriginBlockFlag = false;
            return this;
        }

    }
}
