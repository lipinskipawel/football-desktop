package io.lipinski.board.engine;

import io.lipinski.board.Direction;

import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

//TODO refactor this class with PointUtils
// put them into the same package and play with package scope
@Immutable
class Point2 {

    private int position;
    private Map<Direction, Boolean> availableDirections;


    Point2(int position) {
        this.position = position;
        this.availableDirections = initAvailableDirections();
    }

    private Point2(int position, Map<Direction, Boolean> availableDirections) {
        this.position = position;
        this.availableDirections = availableDirections.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    int getPosition() {
        return this.position;
    }

    boolean isAvailable(final Direction destination) {
        return this.availableDirections.get(destination);
    }

    /**
     *
     * @param direction
     * @return
     */
    Point2 notAvailableDirection(Direction direction) {
        int position = this.position;
        Map<Direction, Boolean> collect = this.availableDirections.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        collect.put(direction, Boolean.FALSE);
        return new Point2(position, collect);
    }

    // TODO this should be private
    void notAvailableDirections(Direction... directions) {
        for (Direction direction : directions) {
            this.availableDirections.remove(direction);
        }
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
