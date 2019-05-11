package io.lipinski.board.engine;

import com.google.common.collect.ImmutableList;
import com.google.common.math.PairedStats;
import io.lipinski.board.Direction;
import io.lipinski.board.engine.exceptions.IllegalMoveSizeException;
import io.lipinski.board.engine.internal.Pair;

import javax.crypto.IllegalBlockSizeException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// TODO This class probably will be removed as well as Pair class.
public class Move {

    private ImmutableList<Direction> directions;
    private final Pair<? extends Direction,
            ? extends Direction> move;


    public Move(List<Direction> direction) {
        if (direction.size() != 2) {
            throw new IllegalMoveSizeException("Not allow size of list " + direction.size() + ". Allow size is 2");
        }
        this.move = Pair.pair(direction.get(0), direction.get(1));
        this.directions = ImmutableList.<Direction>builder()
                .addAll(direction)
                .build();
    }

    public List<Direction> getMove() {
        List<String> all = new ArrayList<>();
        all.add("Original");
        List<String> coped = new ArrayList<>(all);
        Collections.copy(coped, all);
        coped.forEach(System.out::println);
        all.set(0, "Haha");
        System.out.println(all.get(0));
        System.out.println("Is ok? " + coped.get(0));
        return ImmutableList.copyOf(directions);
    }


}
