package io.lipinski.board.engine;


import io.lipinski.board.Direction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("Sanity checks of board state")
class SanityChecksBoard {

    private BoardInterface2 board;
    private static int STARTING_BALL_POSITION = 58;


    @BeforeAll
    static void setUpBall() {
        STARTING_BALL_POSITION = 58;
    }

    @BeforeEach
    void setUp() throws Exception {
        this.board = new Board2();
    }


    @Test
    @DisplayName("Starting ball position is 58")
    void startingBallPosition() {

        assertEquals(STARTING_BALL_POSITION,
                this.board.getBallPosition());

    }

    @Test
    @DisplayName("Returning new instance of board after return")
    @Disabled
    void afterMoveReturnNewBoard() {


        //When:
        BoardInterface2 afterMove = board.executeMove(Direction.N);

        //Then:
        assertNotEquals(afterMove, board);

    }

}
