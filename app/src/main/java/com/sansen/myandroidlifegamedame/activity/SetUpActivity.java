package com.sansen.myandroidlifegamedame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sansen.myandroidlifegamedame.MainActivity;
import com.sansen.myandroidlifegamedame.R;
import com.sansen.myandroidlifegamedame.databinding.ActivitySetupBinding;
import com.sansen.myandroidlifegamedame.utils.Constant;

/**
 * 设置页面，可以进行页面的设置，比如昵称，行和列的数，然后有进入游戏和直接游戏两个按钮。
 */
public class SetUpActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySetupBinding databind;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databind = DataBindingUtil.setContentView(this, R.layout.activity_setup);
        init();
    }
    private void init(){
        databind.setupBtJump.setOnClickListener(this);
        databind.setupBtStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setup_bt_jump:
                Intent intentjump = new Intent(this, MainActivity.class);
                intentjump.putExtra("type", Constant.DEFAULT);
                startActivity(intentjump);
                break;
            case R.id.setup_bt_start:
                if(getJudge()) {
                    Intent intentstart = new Intent(this, MainActivity.class);
                    intentstart.putExtra("type", Constant.CUSTOM);
                    intentstart.putExtra(Constant.NICKNAME, databind.setupEtUsername.getText().toString().trim());
                    intentstart.putExtra(Constant.ROW,string2Int(databind.setupEtMapx.getText().toString().trim()));
//                    intentstart.putExtra(Constant.COLUMN,string2Int(databind.setupEtMapy.getText().toString().trim()));
                    startActivity(intentstart);
                    this.finish();
                }else{
                    Toast.makeText(this, "请填写昵称！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //判断数据不为null ,然后转成int型
    private int string2Int(String str){
        if(!TextUtils.isEmpty(str)){
            int mData = Integer.parseInt(str);
            return mData;
        }
        return 100;
    }
    /**
     * 判断是否正常填写资料
     */
    private boolean getJudge(){
        boolean judge = false;
        if(!TextUtils.isEmpty(databind.setupEtUsername.getText().toString().trim())){
            judge = true;
        }
        return judge;
    }
}
