package io.lipinski.board;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BoardTest {

    private BoardInterface board;

    @Before
    public void setUp() throws Exception {
        this.board = new Board();
    }


    @Test
    public void moveBallToNorthUsingPoint() {

        //Given:
        Point destBallPosition = new Point(49);

        //When:
        board.tryMakeMove(destBallPosition);

        //Then:
        int actualBallPosition = board.getBallPosition();
        assertEquals(destBallPosition.getPosition(), actualBallPosition);

    }

    @Test
    public void moveBallToNorthUsingDirections() {

        //Given:
        Point destBallPosition = new Point(49);

        //When:
        board.tryMakeMove(Direction.N);

        //Then:
        int actualBallPosition = board.getBallPosition();
        assertEquals(destBallPosition.getPosition(), actualBallPosition);

    }


    @Test
    public void moveBallAndUndoTheMove() {

        //Given:
        int ballPositionAtStart = board.getBallPosition();
        Point point = new Point(60);

        //When:
        board.tryMakeMove(point);
        board.undoMove();

        //Then:
        assertEquals(ballPositionAtStart, 58);
    }


}
