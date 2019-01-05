package us.ait.android.minesweeper;

public class Mine {

    // all variables and methods for whether or not the mine is covered (or if it's flagged)
    public static final short COVERED = 0;
    public static final short UNCOVERED = 1;
    public static final short FLAGGED = 2;
    private short isCovered = COVERED;

    public short getCoverage(){
        return isCovered;
    }

    public void setCoverage(short content){
        isCovered = content;
    }

    // all variables and methods for whether or not the mine contains a bomb
    public static final short EMPTY = 0;
    public static final short BOMB = 1;
    private short hasBomb = EMPTY;

    public short getHasBomb(){
        return hasBomb;
    }

    public void setHasBomb(short content){
        hasBomb = content;
    }

    // all variables and methods for the number of surrounding bombs, if the mine is not a bomb itself
    public static final short ISBOMB = 10;
    private short numSurroundingBombs = 0;

    public short getNumSurroundingBombs(){
        return numSurroundingBombs;
    }

    public void setNumSurroundingBombs(short content){
        numSurroundingBombs = content;
    }



}
