package de.stefanschade.AdventureGame.framework.datamodel.immutables;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ImmutableCSV<C> {

    protected static final Logger LOGGER = Logger.getLogger(ImmutableCSV.class.getName());
    protected static final String CSV_SEPERATOR = ";";

    protected final String filename;
    protected int lineCounter = 0;

    protected boolean objectAlreadyUsed = false;
    protected boolean eofReachedFlag;
    protected boolean previousBlockFinishedFlag;

    public ImmutableCSV(String filename) {
        this.filename = filename;
    }

    public C getFromCSV() throws IOException {

        LOGGER.log(Level.INFO, "parsing file: " + filename);

        ensureThisObjectOnlyUsedOnce();
        BufferedReader br = null;
        try {
            br = Files.newBufferedReader(Paths.get(filename));
            while (!eofReachedFlag) {
                LOGGER.log(Level.INFO, "parsing line: " + lineCounter++);
                String currentLine = br.readLine();
                if (!isEOF(currentLine)) {
                    if (isComment(currentLine)) continue;
                    String[] inputCell = currentLine.split(CSV_SEPERATOR);
                    parse(inputCell);
                    setTempFieldsAccordingToCurrentParse();
                }
                populateTemporaryObjectsAfterParse();  //  also necessary at EOF to process last line
                prepareTempFieldsForNextParse(); // does not harm at EOF
            }
        } finally {
            br.close();
        }

        C returnvalue = constructReturnObjectAfterParse();

        return returnvalue;
    }

    protected void ensureThisObjectOnlyUsedOnce() {
        if (objectAlreadyUsed) {
            throw new IllegalArgumentException("OLDPassageMapCSV Object has to be instantiated for each parse operation");
        } else {
            objectAlreadyUsed = true;
        }
    }

    protected boolean isEOF(String currentLine) {
        if (currentLine == null) {
            LOGGER.log(Level.FINE, filename + " EOF reached");
            eofReachedFlag = true;
            return true;
        } else {
            return false;
        }
    }

    protected boolean isComment(String currentLine) {
        return (currentLine.trim().isEmpty() || currentLine.startsWith("#"));
    }

    protected abstract void parse(String[] inputCell);

    protected abstract void setTempFieldsAccordingToCurrentParse();

    protected abstract void populateTemporaryObjectsAfterParse();

    protected abstract void prepareTempFieldsForNextParse();

    protected abstract C constructReturnObjectAfterParse();
}