package io.lipinski.board.engine;

import com.google.common.collect.ImmutableList;
import io.lipinski.board.Direction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class represents move on the board
 */
public class Move {

    private ImmutableList<Direction> directions;


    public Move(Collection<Direction> direction) {
        directions = ImmutableList.<Direction>builder()
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
