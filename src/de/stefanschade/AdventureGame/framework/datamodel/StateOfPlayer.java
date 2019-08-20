package de.stefanschade.AdventureGame.framework.datamodel;

import de.stefanschade.AdventureGame.framework.datamodel.immutables.World;

public class StateOfPlayer {

    private int currentRoom = World.START_ROOM;
    private boolean quit = false;

    public int getCurrentRoom() {
        return currentRoom;
    }

    public boolean isQuit() {
        return quit;
    }

    public void setCurrentRoom(int currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void quit() {

        // rückfrage auslösen

        this.quit = true;
    }
}
