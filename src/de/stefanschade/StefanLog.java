package de.stefanschade;

public class StefanLog {

    private static StefanLog instance = null;
    private int loglevel = 0;

    private StefanLog() {
    }

    public static StefanLog getInstance() {
        if (instance == null) {
            instance = new StefanLog();
        }
        return instance;
    }

    public void setLoglevel(int loglevel) {
        this.loglevel = loglevel;
    }

    public void log(String msg, int level) {
        if (this.loglevel >= level) {
            System.out.println(msg);
        }
    }

}