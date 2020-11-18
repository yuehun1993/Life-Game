package com.sansen.myandroidlifegamedame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sansen.myandroidlifegamedame.R;

/**
 * 11.05添加缩放和拖拽。
 */
public class GameView extends View {

    private String name;
    private String time;
    private int row;
    private int column;
    private int windowWidth;//窗口的宽。

    private Paint gamePaint;//棋盘的线
    private Paint lifeAlivePaint;//代表生命活着的点。
    private Paint textPaint;
    private int[][] chartDot;//地图上的点
    private int[][] alive;//保存上一次的点。
    private float blockWidth;//单个小方格的宽
    private float blockHigh;//单个小方格的高

    //缩放相关。
    private ScaleGestureDetector mScaleDetector;
    private Matrix myMatrix;    //用来完成缩放
    private int scaleMax = 10;//缩放最大倍数。
    private int scaleMin = 0;//缩放最小倍数。
    private boolean isScale = true;
//    private float scale = 1;
//    private float scaleTemp = 1;
    //点击地图得到点。
    private boolean touchPaint = false;
    //擦除地图上的点
    private boolean erasePaint = false;



    //在java代码里new的时候需要。
    public GameView(Context context) {
        super(context);
    }

    //在xml布局文件中使用时需要。
    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initAttrs(context,attrs);
        initPaint();
        initScaleDetector();
    }

    private void initAttrs(Context context,AttributeSet attrs){
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.life_game);
        name = typeArray.getString(R.styleable.life_game_name);
        time = typeArray.getString(R.styleable.life_game_time);
        row = typeArray.getInteger(R.styleable.life_game_row,10);
        column = typeArray.getInteger(R.styleable.life_game_column,10);
    }

    private void initPaint(){
        gamePaint = new Paint();
        gamePaint.setAntiAlias(true);//抗锯齿
        gamePaint.setStrokeWidth(1);//画笔宽
        gamePaint.setColor(getContext().getColor(R.color.gameview_line_color));
//        gamePaint.setColor(getContext().getColor(R.color.dialog_title_color));
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(3);
        textPaint.setTextSize(24);

        lifeAlivePaint = new Paint();
        lifeAlivePaint.setAntiAlias(true);
//        lifeAlivePaint.setColor(Color.GREEN);
        lifeAlivePaint.setColor(getContext().getColor(R.color.function_button_color));

        chartDot = new int[column][row];

    }
    //得到一个当前行和列的空二维数组
    public int[][] getEmptyArrAy(){
        int[][] newArray = new int[row][column];
        return newArray;
    }


    //设置活着的点
    public void setAlive(int[][] aliveDot){

        this.alive = aliveDot;
        chartDot = new int[row][column];
        chartDot = aliveDot;

        invalidate();
    }
    //得到目前的点
    public int[][] getAlive(){
        if(alive == null || alive.length<1 ||alive.length < row){
            alive = new int[row][column];
        }
        return alive;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        windowWidth = getWidth();
        blockHigh = (float) windowWidth / (row-1);//强转一下，不然拿到的是整数。
        blockWidth = (float) windowWidth / (column-1);

        canvas.concat(myMatrix);
        chessBoard(canvas);
        LifeType(canvas);
    }

    //棋盘地图
    private void chessBoard(Canvas canvas){
        canvas.drawColor(getResources().getColor(R.color.chessBg));
        float x,y;
        for (int i = 0; i < row; i++){
            y = i*blockHigh;
            canvas.drawLine(0,y,windowWidth,y,gamePaint);

        }
        for (int i= 0;i < column; i++){
            x = i*blockWidth;
            canvas.drawLine(x,0,x,windowWidth,gamePaint);
        }
        float timeWidth = textPaint.measureText("开始时间:"+time);
        float nameWidth = textPaint.measureText("用户名:"+name);
        float rowWidth = textPaint.measureText("y:"+getRow());
        float columnWidth = textPaint.measureText("x:"+getColumn());

        //如果是null或者为空的话，则不显示
        if(!TextUtils.isEmpty(name) && name.trim().length()>0) {
            canvas.drawText("用户名:" + name, windowWidth - nameWidth - 20, windowWidth + 40, textPaint);
            canvas.drawText("开始时间:" + time, windowWidth - timeWidth - 20, windowWidth + 70, textPaint);
            canvas.drawText("y:" + getRow(), windowWidth - rowWidth - 20, windowWidth + 100, textPaint);
            canvas.drawText("x:" + getColumn(), windowWidth - columnWidth - 20, windowWidth + 130, textPaint);
        }else {
            canvas.drawText("y:" + getRow(), windowWidth - rowWidth - 20, windowWidth + 40, textPaint);
            canvas.drawText("x:" + getColumn(), windowWidth - columnWidth - 20, windowWidth + 70, textPaint);
        }
    }

    //生命状态
    private void LifeType(Canvas canvas){

        for(int i = 0 ; i<row ; i++){
            for(int j = 0;j<column;j++){
                if(chartDot[i][j] != 0){
                    if(blockHigh<blockWidth) {
                        canvas.drawCircle(i * blockWidth, j * blockHigh, (float) blockHigh / 2, lifeAlivePaint);
                    }else{
                        canvas.drawCircle(i * blockWidth, j * blockHigh, (float) blockWidth / 2, lifeAlivePaint);
                    }
                }
            }
        }

    }
    /**
     * 缩放相关
     */
    //点击事件。
    private float downX = 0,downY = 0,lastX = 0,lastY = 0;
    private boolean pointer = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        if(!mScaleDetector.isInProgress()) {//如果是在进行缩放手势则返回true.
            //当不是进行缩放的时候，可以滑动地图
            int action = event.getAction();
            float x = event.getX();
            float y = event.getY();
            if(event.getPointerCount() > 1){//触控点数量，1是一个手指
                pointer = false;
            }
            switch (action){
                case MotionEvent.ACTION_DOWN:
                    downX = x;
                    downY = y;
                    pointer = true;
                    if(touchPaint) {
                        drawTouchPaint(x, y);
                    }
                    if(erasePaint){
                        eraseTouchPaint(x,y);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(touchPaint) {
                        drawTouchPaint(x, y);
                    }else if(erasePaint){
                        eraseTouchPaint(x,y);
                    }else{
                        if (!isScale) {
                            float dx = Math.abs(x - downX);
                            float dy = Math.abs(y - downY);
                            if ((dx > 10 || dy > 10) && pointer) {
                                dx = x - lastX;
                                dy = y - lastY;
                                myMatrix.postTranslate(dx, dy);
                                invalidate();
                            }
                        }
                    }
                    break;
            }
            lastX = x;
            lastY = y;
        }
        return true;
    }
    //
    private void initScaleDetector(){
        myMatrix = new Matrix();
//        if(!touchPaint) {
            mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
                @Override
                public boolean onScaleBegin(ScaleGestureDetector detector) {
                    isScale = true;
                    return true;
                }

                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    if(!touchPaint) {
                        float scaleFactor = detector.getScaleFactor();
                        float nowscale = getScale();//得到当前的缩放比。
                        if (scaleFactor * nowscale < scaleMin) {
                            scaleFactor = scaleMin / nowscale;
                        } else if (scaleFactor * nowscale > scaleMax) {
                            scaleFactor = scaleMax / nowscale;
                        }
                        myMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());//改变地图的缩放
                        invalidate();

                    }
                    return true;
                }

                @Override
                public void onScaleEnd(ScaleGestureDetector detector) {
                    isScale = false;
                }
            });
//        }
    }
    //获取当前已经缩放的比例  ps:好反人类，将当前的值，通过getvalues(xxx),放在了xxx里面。
    private float getScale(){
        float[] values = new float[9];
        myMatrix.getValues(values);
        if(values[Matrix.MSCALE_X] ==0 ){
            return 1f;
        }else {
            return values[Matrix.MSCALE_X];
        }
    }

    /**
     * 不想设置那么多的刷新页面，感觉初始的时候，只需要一个就行了，然后把信息都放在一个类里面，或者给他们
     * 排个序，最后调用使用了刷新的那个方法。
     */

    //得到行
    public int getRow(){
        if(row >1) {
            return row;
        }
        return 0;
    }
    //得到列
    public int getColumn(){
        if(column >1) {
            return column;
        }
        return 0;
    }
    //设置行、列、姓名、开始时间。
    public void setBasicInformation(String gameName,String startTime,int mRow,int mColumn){
        setName(gameName);
        setStartTime(startTime);
        setRowColumn(mRow,mColumn);
//        invalidate();
    }
    //设置姓名
    public void setName(String gameName){
        this.name = gameName;
        invalidate();
    }

    //设置这场游戏开始的时间。
    public void setStartTime(String startTime){
        this.time = startTime;
//        invalidate();
    }
    //设置棋盘的行和列。
    public void setRowColumn(int mRow,int mColumn){
        if(mRow > 0 && mColumn > 0){
            chartDot = new int[mRow][mColumn];
            row = mRow;
            column = mColumn;

            invalidate();
        }
    }
    //得到当前是否可触摸画点默认false
    public Boolean getTouchPaint(){
        return touchPaint;
    }
    //设置当前地图的可触摸画点功能。
    public void setTouchPaint(boolean draw){
        touchPaint = draw;
        erasePaint = false;
    }
    //设置是否擦除
    public void setErasePaint(boolean erase){
        erasePaint = erase;
        touchPaint = false;
    }
    //计算距离当前触摸的点最近的横纵线交点。并绘制。
    private void drawTouchPaint(float drawX , float drawY){
        //blockHigh\blockWidch 每一个小方块的高和宽。
        int intX =  (int) (drawX/blockWidth);
        int intY =  (int) (drawY/blockHigh);
        float fx = drawX/blockWidth;
        float fy = drawY/blockHigh;
        int targetX = 0;//目标点x
        int targetY = 0;//目标点y
        //我要找到最近的那个点的坐标，然后进行比较。
        if(fx-intX>(blockWidth/2)){
            targetX = intX+1;
        }else{
            targetX = intX;
        }
        if (fy-intY>(blockHigh/2)){
            targetY = intY+1;
        }else {
            targetY = intY;
        }
        //画到地图上。
        if(targetX < column && targetY < row) {
            int[][] addPort = getAlive();
            addPort[targetX][targetY] = 1;
            setAlive(addPort);
        }
    }
    //计算距离当前触摸的点最近的横纵线交点。并擦除。
    private void eraseTouchPaint(float drawX , float drawY){
        //blockHigh\blockWidch 每一个小方块的高和宽。
        int intX =  (int) (drawX/blockWidth);
        int intY =  (int) (drawY/blockHigh);
        float fx = drawX/blockWidth;
        float fy = drawY/blockHigh;
        int targetX = 0;//目标点x
        int targetY = 0;//目标点y
        //我要找到最近的那个点的坐标，然后进行比较。
        if(fx-intX>(blockWidth/2)){
            targetX = intX+1;
        }else{
            targetX = intX;
        }
        if (fy-intY>(blockHigh/2)){
            targetY = intY+1;
        }else {
            targetY = intY;
        }
        //画到地图上。
        if(targetX < column && targetY < row) {
            int[][] addPort = getAlive();
            addPort[targetX][targetY] = 0;
            setAlive(addPort);
        }
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

    }

}
