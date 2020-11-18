package com.sansen.myandroidlifegamedame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sansen.myandroidlifegamedame.MainActivity;
import com.sansen.myandroidlifegamedame.R;
import com.sansen.myandroidlifegamedame.databinding.ActivityFunctionBinding;
import com.sansen.myandroidlifegamedame.utils.Constant;
import com.sansen.myandroidlifegamedame.utils.PermissionUtils;

/**
 * 玩家看到的首页，主要是几个跳转用的功能按钮，
 * 目前准备的有，直接开始，2、设置界面，3、保存的记录。再加一个4，用来预设自己想用的模块。
 * 还在想， 将经典的模块直接添加到游戏里面还是专门设置一个用来添加游戏的界面，
 * 先不管了，先把这几个已经准备要写的界面写出来
 * 其他：1目前先不到算复用代码，
 * 2、之后的版本都要保存历史版本，想玩那个版本就玩那个版本。这个是次要的，主要目的是见证一下它的历史。如果还有多个版本的话。………
 * *√*
 */
public class FunctionActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityFunctionBinding databind;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databind = DataBindingUtil.setContentView(this, R.layout.activity_function);
        init();
    }
    private void init(){
        databind.btFunctionStart.setOnClickListener(this);
        databind.btFunctionSetup.setOnClickListener(this);
        databind.btFunctionKeep.setOnClickListener(this);
        databind.btFunctionModule.setOnClickListener(this);
        databind.btFunctionAbout.setOnClickListener(this);
        //申请权限。
        PermissionUtils.getInstance().applyPermission(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_function_start://直接跳转到MainActivity界面，设置都是默认的。
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("type", Constant.DEFAULT);
                startActivity(intent);

                break;
            case R.id.bt_function_setup://设置
//                Toast.makeText(this,"功能正在开发中...",Toast.LENGTH_SHORT).show();
                Intent intentSetup = new Intent(this,SetUpActivity.class);
                startActivity(intentSetup);
                break;
            case R.id.bt_function_keep://保存的游戏
                //TODO 功能还缺少搜索。
                Intent intentKeep = new Intent(this,KeepGameActivity.class);
                startActivity(intentKeep);
//                Toast.makeText(this,"功能正在开发中...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_function_module://设置属于自己的模块
                //TODO 今天的重点，绘制view,和通过输入x,y
                Intent intentModule = new Intent(this,ModuleActivity.class);
                startActivity(intentModule);
                break;
            case R.id.bt_function_about://关于
                Toast.makeText(this,"功能正在开发中...",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
