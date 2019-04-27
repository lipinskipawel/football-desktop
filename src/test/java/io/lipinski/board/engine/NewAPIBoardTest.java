package io.lipinski.board.engine;

import io.lipinski.board.Direction;
import io.lipinski.board.engine.exceptions.IllegalMoveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Running new API Board tests")
class NewAPIBoardTest {

    private BoardInterface2 board;


    @BeforeEach
    void setUp() throws Exception {
        this.board = new Board2();
    }

    @Test
    @DisplayName("Make a proper full move")
    void makeAMove() {

        //When:
        if (this.board.isMoveAllowed(Direction.N))
            this.board.executeMove(Direction.N);

        //Then:
        int actualBallPosition = board.getBallPosition();
        assertEquals(49, actualBallPosition);

    }


    @Test
    @DisplayName("Make a one full move and then undo")
    void makeAMoveAndUndoMove() {

        //When:
        this.board.executeMove(Direction.N);
        this.board.undoMove();

        //Then:
        int actualBallPosition = board.getBallPosition();
        assertEquals(49, actualBallPosition);


    }

    // TODO return to this tests when executeMove returns Board
    @Test
    @Disabled
    @DisplayName("Make a one full move and don't allow to move backwards")
    void notAllowToMakeAMove() {

        //When:
        this.board.executeMove(Direction.N);
        if (this.board.isMoveAllowed(Direction.S))
            this.board.executeMove(Direction.S);

        //Then:
        assertEquals(49, this.board.getBallPosition());
    }


    @Test
    @Disabled
    @DisplayName("Make a one full move and go back directly to previous position")
    void notAllowIllegalMove() {

        assertThrows(IllegalMoveException.class,
                () -> {
                    this.board.executeMove(Direction.N);
                    this.board.executeMove(Direction.S);
                },
                "Move back to previous position is illegal.");

    }


}
