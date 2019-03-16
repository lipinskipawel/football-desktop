package io.lipinski.board.engine;

import io.lipinski.board.Direction;

import java.util.HashMap;
import java.util.Map;

class Point2 {

    private int position;
    private Map<Direction, Boolean> availableDirections;


    public Point2(int position) {
        this.position = position;
        this.availableDirections = initAvailableDirections();
    }


    public int getPosition() {
        return position;
    }

    boolean isAvailable(final Direction destination) {
        return availableDirections.get(destination);
    }


    private Map<Direction, Boolean> initAvailableDirections() {
        final Map<Direction, Boolean> res = new HashMap<>();
        res.put(Direction.N, Boolean.TRUE);
        res.put(Direction.NE, Boolean.TRUE);
        res.put(Direction.E, Boolean.TRUE);
        res.put(Direction.SE, Boolean.TRUE);
        res.put(Direction.S, Boolean.TRUE);
        res.put(Direction.SW, Boolean.TRUE);
        res.put(Direction.W, Boolean.TRUE);
        res.put(Direction.NW, Boolean.TRUE);
        return res;
    }
}
