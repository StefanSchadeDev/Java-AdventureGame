package de.stefanschade.AdventureGame.framework.datamodel.immutables;

import net.jcip.annotations.Immutable;

// static class, as we need the constructor before we have an instance of the immutable enclosing class.
// Association is modelled via the map in the enclosing class
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
