package com.sansen.myandroidlifegamedame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.sansen.myandroidlifegamedame.bean.ConfigBean;
import com.sansen.myandroidlifegamedame.callback.ConfigDialogCallBack;
import com.sansen.myandroidlifegamedame.callback.DialogCallBack;
import com.sansen.myandroidlifegamedame.databinding.ActivityMainBinding;
import com.sansen.myandroidlifegamedame.module.Classic;
import com.sansen.myandroidlifegamedame.utils.ArrayUtils;
import com.sansen.myandroidlifegamedame.utils.Constant;
import com.sansen.myandroidlifegamedame.utils.MapUtils;
import com.sansen.myandroidlifegamedame.utils.MyDialogUtils;
import com.sansen.myandroidlifegamedame.utils.StoreUtils;
import com.sansen.myandroidlifegamedame.utils.cypher.SM4Utils;
import com.sansen.myandroidlifegamedame.view.GameView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

import static com.sansen.myandroidlifegamedame.utils.StoreUtils.getInstance;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //写几个常数，用来判断添加的是哪个模块。
    private static final int GLIDER = 1;
    private static final int OTHER = 2;

    //用来判断是哪个模块
    private int myModule = 0;
    private int[][] otherModule = null;//11.16给新模块设置位置需要用的
    private ActivityMainBinding databind;
    private Disposable suspend;//关于暂停的
    private int initX = 0; //模块需要添加到的X坐标
    private int initY = 0;//模块需要添加到的Y坐标
    private MyDialogUtils dialogUtils;
    private int steps = 5;//步数
    private int intervalTime = 500;//间隔时间   单位：毫秒

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //保持屏幕常亮。
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        databind = DataBindingUtil.setContentView(this,R.layout.activity_main);
        //初始化添加模块
        myModule = GLIDER;
        //根据得到的参数，判断某些参数应该怎么传。
        setInformation();
        init();
    }

    /**
     * 根据一个界面的选择， 设置基础信息。
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setInformation(){
        Intent intent = getIntent();
        //根据type判断下一步应该怎么做
        String type= intent.getStringExtra("type");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
         String nowTime =format.format(date);
         if(type !=null && type.equals(Constant.DEFAULT)){
             databind.gv.setBasicInformation("路人甲","某年某月某日",100,100);
         }else if(type.equals(Constant.CUSTOM)){
             String userName = intent.getStringExtra(Constant.NICKNAME);
             int row = intent.getIntExtra(Constant.ROW,100);
//             int column = intent.getIntExtra(Constant.COLUMN,100);
             databind.gv.setBasicInformation(userName,nowTime,row,row);

         }else if(type.equals(Constant.RESUME_GAME)){
             File myfile = (File) intent.getSerializableExtra("file");
             aboutKeepGame(myfile);
         }
    }

    /**
     * 给按钮添加点击事件。
     */
    private void init(){
        //databinding
        databind.btStart.setOnClickListener(this);
        databind.btStop.setOnClickListener(this);
        databind.btEnd.setOnClickListener(this);
        databind.btGetset.setOnClickListener(this);
        databind.btNext.setOnClickListener(this);
        databind.btAuto.setOnClickListener(this);
        databind.btConfig.setOnClickListener(this);
        databind.btKeep.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_start://添加。添加的时候需要先暂停。2
//                Toast.makeText(this, "添加", Toast.LENGTH_SHORT).show();
                runingLife(myModule,otherModule);
                break;
            case R.id.bt_stop://暂停Rxjava的自动循环。
                suspendGame();
                break;
            case R.id.bt_end://清空地图，结束游戏。
                endGame();
                break;
            case R.id.bt_getset://预设，可以将自定义的模块放到地图上。

                //弹出一个可滑动的list列表dialog,点击其中的某一个的时候，获取到对应的，数据。
                File[] files = StoreUtils.getInstance().getModuleTxtList(this);
                if(files.length<1){
                    Toast.makeText(this,"暂无模块",Toast.LENGTH_SHORT).show();
                }else {
                    dialogUtils = new MyDialogUtils();
                    dialogUtils.getInstance().showListDialog(this , dialogCallBack , files);
                }

                break;
            case R.id.bt_next://下一步  看下一个瞬间，没啥好说的。可以通过配置进行简单的修改
                for(int i = 0; i < steps; i++) {
                    getNextLife();
                }
                break;
            case R.id.bt_auto://自动  自动进行，默认是每0.5秒行动一次，可以通过配置修改。
                if(suspend == null || suspend.isDisposed()) {//避免多次调用自动进行。
                    Observable.interval(intervalTime, TimeUnit.MILLISECONDS).subscribe(
                            new Observer<Long>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {
                                    suspend = d;
                                }

                                @Override
                                public void onNext(@NonNull Long aLong) {
                                    getNextLife();
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            }
                    );
                }
                break;
            case R.id.bt_config://配置  可以配置下一步的具体步数，自动进行的时间间隔，模块加入地图的位置。
                //打算写一个自定义dialog  title： 配置， 有取消和确认按钮， 2 将现有的值，赋值进去。
                MyDialogUtils.getInstance().showConfigDialog(this,configCallBack,getConfigInformation());
                break;
            case R.id.bt_keep://保存 保存当前的状态，可以通过主页面里的保存的记录恢复。
                // 方法：需要提供name，自动获取保存的时间，然后进行一系列操作
                // （转成稀疏数组，将稀疏数组的数据通过,分隔，编成字符串，然后加密，创建txt文件，保存数据，保存到手机。）
                //还要添加一个删除储存的数据功能，一个是一键清除，一个是一条一条的清。
                //封装一个dialog,传入context,
                MyDialogUtils.getInstance().showCommonDialog(this,databind.gv.getAlive());
                //看个东西
//                String url = "http://www.hnsasen.cn/apk/SS_ZXJC.apk";
//                String fileName = url.substring(url.lastIndexOf("/")+1);
//                String[] arrName = fileName.split("\\.");
//                SimpleDateFormat format = new SimpleDateFormat("dd_HH_mm_ss");
//                Date date = new Date(System.currentTimeMillis());
//                String time =format.format(date);
//                String newName = arrName[0] + time +".apk";
//                Toast.makeText(this, ""+fileName+"   new:"+newName, Toast.LENGTH_SHORT).show();
                break;
        }
    }


    //用来添加地图上的因素。 TODO tag目前还没使用，我打算添加几个经典模块，使用tag进行判断到底是添加是谁。

    /**
     * 添加已有的模块  11.16增加可调整模块位置的功能
     * @param tag
     */
    private void runingLife(int tag,int[][] other){
        //用来添加已有的模块。
        int[][] alive = databind.gv.getAlive();
        if(other == null){
            tag = GLIDER;
        }
        switch (tag){
            case GLIDER:
                alive = Classic.getInstance().glider(alive,initX,initY);
                break;
            case OTHER:
                alive = Classic.getInstance().superpositionModule(alive,other,initX,initY);
                break;
        }

        databind.gv.setAlive(alive);
    }

    /**
     * 添加玩家自定义的模块
     * @param other
     */
    private void runingLife(int[][] other){
        //用来添加玩家自定义的模块
        int[][] alive = databind.gv.getAlive();
        //添加模块的时候，需要判断，自己设计的地图是否符合模块生存的需求。    目前的情况是直接将原本的清空，然后覆盖上新的模块，我想
        //我想改成，不改变原本的前提下，添加上新的模块，   并且，我想增加返回上一步功能。
//        alive = other;
        alive = Classic.getInstance().superpositionModule(alive,other);
        databind.gv.setAlive(alive);
    }
    //用来将下一轮的点，布到棋盘上
    private void nextLift(int[][] nextAlive){
        databind.gv.setAlive(nextAlive);
    }


    //得到地图上的点，下次的状态。
    private void getNextLife(){
        int[][] nextAlive = MapUtils.getInstance().nextLife(databind.gv.getAlive());
        nextLift(nextAlive);
    }
    //结束循环，清空地图
    private void endGame(){
        suspendGame();
        nextLift(databind.gv.getEmptyArrAy());
    }
    //暂停游戏
    private void suspendGame(){
        if(suspend != null && !suspend.isDisposed()){
            suspend.dispose();
        }
    }
    //设置模块的填放位置。
    private void setModulePosition(int x,int y){
        this.initX = x;
        this.initY = y;
    }
    //设置步数
    private void setSteps(int mSteps){
        this.steps = mSteps;
    }
    //设置间隔时间
    private void setIntervalTime(int time){
        this.intervalTime = time;
    }
    //调用读取txt里的数据。2、解密，3、将解密的数据转成二维数组。
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void readDecrypt2Arr(File file){
        //先把文件中的数据读取出来
        String txtData = StoreUtils.getInstance().readTxt(this,file);
        //解密
        String decryptStr = SM4Utils.decryptCBC(txtData,Constant.CYPHER);
        //toArr
        int[][] arr = ArrayUtils.getInstance().sparseStrToArr(decryptStr);
        //把模块给设置上。
        myModule = OTHER;
        otherModule = arr;
//        runingLife(arr);
    }
    //展示保存的游戏
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void openKeepGame(File file){
        //先把文件中的数据读取出来
        String txtData = StoreUtils.getInstance().readTxt(this,file);
        //解密
        String decryptStr = SM4Utils.decryptCBC(txtData,Constant.CYPHER);
        //toArr
        int[][] arr = ArrayUtils.getInstance().sparseStrToArr(decryptStr);
        //把模块给设置上。
        runingLife(arr);
    }
    //写个callback
    private DialogCallBack dialogCallBack = new DialogCallBack() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void dialogItemClick(int position, File[] files) {
            readDecrypt2Arr(files[position]);
        }
    };

    //配置的callback
    private ConfigDialogCallBack configCallBack = new ConfigDialogCallBack() {
        @Override
        public void configClick(int steps, int time, int initX, int initY) {
            setModulePosition(initX,initY);
            setSteps(steps);
            setIntervalTime(time);
        }
    };
    //从保存的游戏页面来到这里,需要调用的方法。
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void aboutKeepGame(File file){
        openKeepGame(file);
    }

    //得到此时的config信息
    private ConfigBean getConfigInformation(){
     ConfigBean bean = new ConfigBean();
     bean.setSteps(this.steps);
     bean.setIntervalTime(this.intervalTime);
     bean.setInitx(this.initX);
     bean.setInity(this.initY);

     return bean;
    }


    @Override
    protected void onPause() {
        super.onPause();
        suspendGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        endGame();
    }
}
