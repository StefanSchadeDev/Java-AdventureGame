package de.stefanschade;

public class StefanLog {

    StefanLog instance = null;

    private StefanLog() {
    }

    public StefanLog getInstance() {
        if (instance == null) {
            this.instance = new StefanLog();
        }
        return instance;
    }

    public void log(LEVEL level, String msg, Throwable exe) {


    }



}