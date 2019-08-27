package de.stefanschade.AdventureGame.framework.datamodel.immutables;

import net.jcip.annotations.Immutable;

@Immutable
final class Passage {
    private final int destination;

    int getDestination() {
        return destination;
    }

    Passage(int destination) {
        this.destination = destination;
    }
}
