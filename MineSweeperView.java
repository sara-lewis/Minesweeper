package us.ait.android.minesweeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import us.ait.android.minesweeper.MineSweeperModel;

public class MineSweeperView extends View {

    private Paint paintBackground;
    private Paint paintLine;
    private short mineWidthHeight = MineSweeperModel.mineWidthHeight;
    private Bitmap bitmapBg;
    private Bitmap flagStar;
    private Bitmap mine;
    private boolean isLost = false;
    TextView toggleInfo;
    private Paint paintFont;

    public MineSweeperView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // setting paintBackground object
        paintBackground = new Paint();
        paintBackground.setColor(Color.GRAY);
        paintBackground.setStyle(Paint.Style.FILL);

        // setting paintLine object
        paintLine = new Paint();
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(5);

        paintFont = new Paint();
        paintFont.setTextSize(1000/mineWidthHeight);

        bitmapBg = BitmapFactory.decodeResource(getResources(), R.drawable.bomb);
        flagStar = BitmapFactory.decodeResource(getResources(), R.drawable.flag);
        mine = BitmapFactory.decodeResource(getResources(), R.drawable.mine);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        scaleImages();
        canvas.drawRect(0, 0, getWidth(), getHeight(), paintBackground);
        drawGameArea(canvas);
        drawPlayers(canvas);
    }

    private void drawPlayers(Canvas canvas) {
        for(int i = 0; i < mineWidthHeight; i++){
            for(int j = 0; j < mineWidthHeight; j++){
                // check all conditions (flagged, shown, etc.) and then draw on the screen as prompted
                if(MineSweeperModel.getInstance().returnContents(i, j).getCoverage() == Mine.UNCOVERED){
                    checkBombs(canvas, i, j);
                }else if(MineSweeperModel.getInstance().returnContents(i, j).getCoverage() == Mine.FLAGGED){
                    // draw flag
                    canvas.drawBitmap(flagStar, i*getWidth()/mineWidthHeight + 5, j*getHeight()/mineWidthHeight + 5, null);
                }else{
                    canvas.drawBitmap(mine, i*getWidth()/mineWidthHeight + 5, j*getHeight()/mineWidthHeight + 5, null);
                }
            }
        }
    }

    private void setColorSurroundingBombs(int adjacentBombs){
        paintFont.setColor(Color.WHITE);
        setColorConditional(adjacentBombs, 1, R.color.color1);
        setColorConditional(adjacentBombs, 2, R.color.color2);
        setColorConditional(adjacentBombs, 3, R.color.color3);
        setColorConditional(adjacentBombs, 4, R.color.color4);
    }

    private void setColorConditional(int adjacentBombs, int testNumber, int id){
        if(adjacentBombs == testNumber){
            paintFont.setColor(getResources().getColor(id));
        }
    }

    private void checkBombs(Canvas canvas, int i, int j) {
        if(MineSweeperModel.getInstance().returnContents(i, j).getHasBomb() == Mine.BOMB){
            canvas.drawBitmap(bitmapBg, i*getWidth()/mineWidthHeight + 5, j*getHeight()/mineWidthHeight + 5, null);
        }else{
            int adjacentBombs = MineSweeperModel.getInstance().returnContents(i, j).getNumSurroundingBombs();
            setColorSurroundingBombs(adjacentBombs);
            canvas.drawText(String.valueOf(adjacentBombs), i*getWidth()/mineWidthHeight + 30,
                    j*getHeight()/mineWidthHeight + 90, paintFont);

        }
    }


    private void drawGameArea(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), paintLine);
        for(int i = 0; i < mineWidthHeight; i++){
            canvas.drawLine(i * getWidth() / mineWidthHeight, 0, i * getWidth() / mineWidthHeight, getHeight(), paintLine);
            canvas.drawLine(0, i * getHeight() / mineWidthHeight, getWidth(), i * getHeight() / mineWidthHeight, paintLine);
        }
    }

    private void scaleImages(){
        int widthHeight = getWidth()/mineWidthHeight - 10;
        bitmapBg = Bitmap.createScaledBitmap(bitmapBg, widthHeight, widthHeight, false);
        flagStar = Bitmap.createScaledBitmap(flagStar, widthHeight, widthHeight, false);
        mine = Bitmap.createScaledBitmap(mine, widthHeight, widthHeight, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isLost){
            int xLocation = 0, yLocation = 0;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // figure out whether touched square should be flagged or opened and do so
                xLocation = (int) event.getX() / (getWidth() / mineWidthHeight);
                yLocation = (int) event.getY() / (getHeight() / mineWidthHeight);
                update2DArray(xLocation, yLocation);
            }
            evaluateLoss(xLocation, yLocation);
            showWinner();
            invalidate();
        }
        return true;
    }

    private void showSurroundingMines(int xLocation, int yLocation){
        if(MineSweeperModel.getInstance().returnContents(xLocation, yLocation).getNumSurroundingBombs() == 0
                && MineSweeperModel.getInstance().returnContents(xLocation, yLocation).getCoverage() != Mine.FLAGGED){
            for(int x = xLocation - 1; x <= (xLocation + 1); x++){
                for(int y = yLocation - 1; y <= (yLocation + 1); y++){
                    if(x >= 0 && x < mineWidthHeight && y >= 0 && y < mineWidthHeight){
                        MineSweeperModel.getInstance().returnContents(x, y).setCoverage(Mine.UNCOVERED);
                    }
                }

            }
        }
    }

    private void updateFlags(int xLocation, int yLocation){
        boolean isFlagMode = ((MainActivity) getContext()).returnFlagMode();
        if(isFlagMode){
            // flag mode
            MineSweeperModel.getInstance().returnContents(xLocation, yLocation).setCoverage(Mine.FLAGGED);
        }else{
            // try field mode
            MineSweeperModel.getInstance().returnContents(xLocation, yLocation).setCoverage(Mine.UNCOVERED);
        }
    }

    private void update2DArray(int xLocation, int yLocation) {
        updateFlags(xLocation, yLocation);
        showSurroundingMines(xLocation, yLocation);
    }

    public void resetGame(){
        // set isCovered to COVERED, hasBomb to EMPTY, and numSurroundingBombs to zero and then invalidate
        for(int i = 0; i < mineWidthHeight; i++){
            for(int j = 0; j < mineWidthHeight; j++){
                MineSweeperModel.getInstance().returnContents(i, j).setCoverage(Mine.COVERED);
                MineSweeperModel.getInstance().returnContents(i, j).setHasBomb(Mine.EMPTY);
                MineSweeperModel.getInstance().returnContents(i, j).setNumSurroundingBombs((short)0);
            }
        }
        MineSweeperModel.getInstance().placeMines();
        ((MainActivity) getContext()).setText(getContext().getString(R.string.default_mode_message));
        ((MainActivity) getContext()).toggleFlagToggle();
        isLost = false;
        invalidate();
    }

    private void evaluateLoss(int xPos, int yPos){
        // figure out whether the player has lost (opened square w/bomb)
        if(xPos != 0 && yPos != 0){
            if(MineSweeperModel.getInstance().returnContents(xPos, yPos).getHasBomb() == Mine.BOMB &&
                    MineSweeperModel.getInstance().returnContents(xPos, yPos).getCoverage() == Mine.UNCOVERED){
                // GAME IS LOST
                ((MainActivity) getContext()).showMessage(getContext().getString(R.string.loss_message));
                isLost = true;
                showBombs();
            }
        }
    }

    private void showBombs(){
        for(int i = 0; i < mineWidthHeight; i++){
            for(int j = 0; j < mineWidthHeight; j++){
                if(MineSweeperModel.getInstance().returnContents(i, j).getHasBomb() == Mine.BOMB){
                    MineSweeperModel.getInstance().returnContents(i, j).setCoverage(Mine.UNCOVERED);
                }
            }
        }
    }

    private boolean hasWon(){
        // figure out if the player has won (all squares opened except those with bombs, all squares with bombs flagged)
        for(int i = 0; i < mineWidthHeight; i++){
            for(int j = 0; j < mineWidthHeight; j++){
                if(MineSweeperModel.getInstance().returnContents(i, j).getHasBomb() == Mine.BOMB){
                    // must be flagged to win
                    if(MineSweeperModel.getInstance().returnContents(i, j).getCoverage() != Mine.FLAGGED){
                        return false;
                    }
                }else{
                    // must be uncovered to win
                    if(MineSweeperModel.getInstance().returnContents(i, j).getCoverage() != Mine.UNCOVERED){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void showWinner(){
        if(hasWon()){
            ((MainActivity) getContext()).showMessage(getContext().getString(R.string.win_message));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int d = w == 0 ? h : h == 0 ? w : w < h ? w : h;
        setMeasuredDimension(d, d);
    }
}
