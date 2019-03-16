package io.lipinski.board.engine;

import io.lipinski.board.Direction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class NewAPIBoardTest {

    private BoardInterface2 board;


    @Rule
    public ExpectedException exException = ExpectedException.none();


    @Before
    public void setUp() throws Exception {
        this.board = new Board2();
    }

    @Test
    public void makeAMove() {

        //When:
        if (this.board.isMoveAllowed(Direction.N))
            this.board.executeMove(Direction.N);

        //Then:
        int actualBallPosition = board.getBallPosition();
        assertEquals(49, actualBallPosition);

    }


    @Test
    public void makeAMoveAndUndoMove() {

        //When:
        this.board.executeMove(Direction.N);
        this.board.undoMove();

        //Then:
        int actualBallPosition = board.getBallPosition();
        assertEquals(49, actualBallPosition);


    }

    @Test
    public void notAllowToMakeAMove() {

        //When:
        this.board.executeMove(Direction.N);
        if (this.board.isMoveAllowed(Direction.S))
            this.board.executeMove(Direction.S);

        //Then:
        assertEquals(49, this.board.getBallPosition());
    }


    @Test
    public void notAllowIllegalMove() {

        //When:
        this.board.executeMove(Direction.N);
        this.board.executeMove(Direction.S);

        exException.expect(IllegalMoveException.class);
        exException.expectMessage("Can't make a move");

    }


}
