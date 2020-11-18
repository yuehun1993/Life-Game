package com.sansen.myandroidlifegamedame.callback;

public interface CoordinateCallBack {
    //添加的x,y轴坐标。
    public void coordinateAddClick(int x,int y);

    public void setMap(int row, int column);

    public void removePort(int x ,int y);

}
