package io.lipinski.board.engine;

import io.lipinski.board.Direction;
import io.lipinski.board.engine.exceptions.IllegalMoveException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Running new API Board tests")
class MovesTestOnBoard {

    private BoardInterface2 board;
    private static int STARTING_BALL_POSITION;
    private static int POSITION_AFTER_N_MOVE;
    private static int POSITION_AFTER_N_N_MOVE;


    @BeforeAll
    static void setUpBall() {
        STARTING_BALL_POSITION = 58;
        POSITION_AFTER_N_MOVE = 49;
        POSITION_AFTER_N_N_MOVE = 40;
    }

    @BeforeEach
    void setUp() throws Exception {
        this.board = new Board2();
    }

    @Nested
    @DisplayName("Make a move")
    class MakeAMove {

        @Test
        @DisplayName("Make a proper full move")
        void makeAMove() {

            //When:
            BoardInterface2 afterMove = board.executeMove(Direction.N);

            //Then:
            int actualBallPosition = afterMove.getBallPosition();
            assertEquals(POSITION_AFTER_N_MOVE, actualBallPosition);

        }

        @Test
        @DisplayName("Load one move")
        void loadMove() {

            //Given:
            Move move = new Move(Collections.singletonList(Direction.N));
            move.getMove();

            //When:
            BoardInterface2 boardAfterMove = board.loadMove(move);

            //Then:
            assertEquals(POSITION_AFTER_N_MOVE, boardAfterMove.getBallPosition());

        }

        @Test
        @DisplayName("Load a sequence of moves")
        void loadASequenceOfMoves() {

            //Given:
            Move firstMove = new Move(Collections.singletonList(Direction.N));
            Move secondMove = new Move(Collections.singletonList(Direction.N));

            //When:
            BoardInterface2 boardAfterMove =
                    board.loadMoves(
                            Arrays.asList(firstMove, secondMove)
                    );

            //Then:
            assertEquals(POSITION_AFTER_N_N_MOVE, boardAfterMove.getBallPosition());

        }

        // TODO return to this tests when executeMove returns Board
        @Test
        @Disabled
        @DisplayName("Make a one full move and don't allow to move backwards")
        void notAllowToMakeAMove() {

            //When:
            board.executeMove(Direction.N);
            if (board.isMoveAllowed(Direction.S))
                board.executeMove(Direction.S);

            //Then:
            assertEquals(POSITION_AFTER_N_MOVE, board.getBallPosition());
        }


        @Test
        @Disabled
        @DisplayName("Make a one full move and go back directly to previous position")
        void notAllowIllegalMove() {

            assertThrows(IllegalMoveException.class,
                    () -> {
                        board.executeMove(Direction.N);
                        board.executeMove(Direction.S);
                    },
                    "Move back to previous position is illegal.");

        }

    }

    @Nested
    @DisplayName("Undo move")
    class UndoAMove {


        @Test
        @DisplayName("Make a one full move and then undo")
        void makeAMoveAndUndoMove() {

            //When:
            board.executeMove(Direction.N);
            board.undoFullMove();

            //Then:
            int actualBallPosition = board.getBallPosition();
            assertEquals(STARTING_BALL_POSITION, actualBallPosition);


        }
    }


}
