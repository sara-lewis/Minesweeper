package us.ait.android.minesweeper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MineSweeperModel {
    public static final short mineWidthHeight = 10;
    // only one instance of the TicTacToeModel class can be created
    private static  MineSweeperModel mineSweeperModel = null;
    public static MineSweeperModel getInstance(){
        if(mineSweeperModel == null){
            mineSweeperModel = new MineSweeperModel();
        }
        return mineSweeperModel;
    }

    // creates model
    private Mine[][] model = new Mine[mineWidthHeight][mineWidthHeight];

    public void initializeModel(){
        for(int i = 0; i < mineWidthHeight; i++){
            for(int j = 0; j < mineWidthHeight; j++){
                model[i][j] = new Mine();
            }
        }
    }

    public void placeMines(){
        for(int i = 0; i < mineWidthHeight - 1; i++){
            Random rand = new Random();
            int position = rand.nextInt(mineWidthHeight*mineWidthHeight);
            int xPos = position / mineWidthHeight;
            int yPos = position % mineWidthHeight;
            model[xPos][yPos].setHasBomb(Mine.BOMB);
            model[xPos][yPos].setNumSurroundingBombs(Mine.ISBOMB);
            calculateSurroundings(xPos, yPos);
        }
    }

    public void calculateSurroundings(int bombXPos, int bombYPos){
        // set numSurroundingBombs for each mine by incrimenting the numSurroundingBombs variable
        for(int x = bombXPos - 1; x <= (bombXPos + 1); x++){
            for(int y = bombYPos - 1; y <= (bombYPos + 1); y++){
                if(x >= 0 && x < mineWidthHeight && y >= 0 && y < mineWidthHeight){
                    if(model[x][y].getHasBomb() != Mine.BOMB){
                        model[x][y].setNumSurroundingBombs((short)(model[x][y].getNumSurroundingBombs() + 1));
                    }
                }
            }

        }
    }

    public Mine returnContents(int xVal, int yVal){
        return(model[xVal][yVal]);
    }

}
