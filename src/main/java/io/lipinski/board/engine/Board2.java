package io.lipinski.board.engine;

import io.lipinski.board.Direction;



class Board2 implements BoardInterface2 {

    private Point2 ballPosition;


    Board2() {
        this.ballPosition = new Point2(58);
    }


    @Override
    public boolean isMoveAllowed(final Direction destination) {
        return this.ballPosition.isAvailable(destination);
    }

    @Override
    public void executeMove(final Direction destination) {

        int moveBall = destination.changeToInt();

        makeAMove(moveBall);

    }

    @Override
    public int getBallPosition() {
        return this.ballPosition.getPosition();
    }

    @Override
    public void undoMove() {

    }


    // TODO create list of Points and apply logic here
    // TODO logic like, change if move can be made
    private void makeAMove(int moveBall) {
        if (moveBall > 0)   this.ballPosition = new Point2(ballPosition.getPosition() - moveBall);
        else                this.ballPosition = new Point2(this.ballPosition.getPosition() + moveBall);
    }

}
