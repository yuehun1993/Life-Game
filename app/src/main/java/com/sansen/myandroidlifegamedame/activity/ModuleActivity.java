package com.sansen.myandroidlifegamedame.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sansen.myandroidlifegamedame.R;
import com.sansen.myandroidlifegamedame.callback.CommonCallBack;
import com.sansen.myandroidlifegamedame.callback.CoordinateCallBack;
import com.sansen.myandroidlifegamedame.databinding.ActivityModuleBinding;
import com.sansen.myandroidlifegamedame.module.Classic;
import com.sansen.myandroidlifegamedame.utils.MyDialogUtils;

//

/**
 * 分两种，一种是通过坐标绘制，一种是通过手指点击绘制。
 * 绘制之后的模块，可以通过模块功能，和坐标位置放到地图里。
 */
public class ModuleActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityModuleBinding databind;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databind = DataBindingUtil.setContentView(this, R.layout.activity_module);
        init();
    }
    private void init(){
        databind.moduleBtCoordinate.setOnClickListener(this);
        databind.moduleBtDraw.setOnClickListener(this);
        databind.moduleBtConfig.setOnClickListener(this);
        databind.moduleBtSave.setOnClickListener(this);
        databind.moduleBtErase.setOnClickListener(this);
        databind.moduleBtClean.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.module_bt_coordinate:
                //通过输入坐标来编写module
                //这里应该是调用dialog,dialog里面可以输入x,y坐标，有三个按钮，一个是添加，一个是去除，一个是完成。
                //这个dialog可能需要判断，输入的x,y范围是不是在当前的坐标内。
                MyDialogUtils.getInstance().showAddCoordinateDialog(this,databind.moduleGv.getRow(),databind.moduleGv.getColumn(),addClick);
                break;
            case R.id.module_bt_draw:
                //通过绘制来编写module
                //点击之后，弹出dialog,输入横轴和竖轴数量，并使地图进入可编辑（点击）状态。
                MyDialogUtils.getInstance().showModuleHandPaintDialog(this,commonCallBack,"是否开启手绘",0);
                break;
            case R.id.module_bt_config:
                MyDialogUtils.getInstance().showModuleConfigMapSize(this,addClick);
                break;
            case R.id.module_bt_erase:
                //手动擦除。
                MyDialogUtils.getInstance().showModuleHandPaintDialog(this,commonCallBack,"是否开启手动擦除",1);
                break;
            case R.id.module_bt_save:
                //保存模板
                MyDialogUtils.getInstance().showSaveModuleDialog(this,databind.moduleGv.getAlive());
//                ArrToReduce
                break;
            case R.id.module_bt_clean:
                //清空布局
                databind.moduleGv.setAlive(databind.moduleGv.getEmptyArrAy());
                break;
        }
    }

    //得到要添加的x,y坐标，添加到地图上。
    private CoordinateCallBack addClick = new CoordinateCallBack() {
        @Override
        public void coordinateAddClick(int x, int y) {
            //添加坐标
            addSpot(x,y);
        }

        @Override
        public void setMap(int row, int column) {
            //设置地图,先清空地图
            databind.moduleGv.setAlive(databind.moduleGv.getEmptyArrAy());
            setRowColumn(row);
        }

        @Override
        public void removePort(int x, int y) {
            //移除地图上的点。
            removeSpot(x, y);
        }
    };
    //判断时候可以绘制的callback
    private CommonCallBack commonCallBack = new CommonCallBack() {
        @Override
        public void commonClick(boolean isPaint) {
            if (isPaint){
                //可以绘制
                databind.moduleGv.setTouchPaint(true);
            }else{
                //不可以绘制
                databind.moduleGv.setTouchPaint(false);
            }
        }

        @Override
        public void eraseClick(boolean isErase) {
            if (isErase){
                databind.moduleGv.setErasePaint(true);
            }else{
                databind.moduleGv.setErasePaint(false);

            }
        }
    };
    /**
     * 添加玩家自定义的点
     */
    private void addSpot(int x,int y){
        //用来添加单个的点
        int[][] alive = databind.moduleGv.getAlive();
        alive = Classic.getInstance().addOnePort(alive,x,y);
        databind.moduleGv.setAlive(alive);
    }

    private void removeSpot(int x ,int y){
        int[][] alive = databind.moduleGv.getAlive();
        alive = Classic.getInstance().removeOnePort(alive,x,y);
        databind.moduleGv.setAlive(alive);
    }
    //设置行和列
    private void setRowColumn(int row ){
        databind.moduleGv.setRowColumn(row,row);
    }
}
