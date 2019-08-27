package de.stefanschade.AdventureGame.framework.datamodel.immutables;

import net.jcip.annotations.Immutable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Immutable
final class PassagesOneRoom {
    private final Map<String, Passage> mapDirectionToDestination;
    private final int originRoomID;


    PassagesOneRoom(int roomOfOrigin, Map<String, Passage> exits) {
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
            Passage psg = this.mapDirectionToDestination.get(direction);
            return psg.getDestination();
        } else {
            return -1;
        }
    }
}
