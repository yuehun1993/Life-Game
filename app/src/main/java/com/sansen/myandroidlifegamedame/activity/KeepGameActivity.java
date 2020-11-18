package com.sansen.myandroidlifegamedame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sansen.myandroidlifegamedame.MainActivity;
import com.sansen.myandroidlifegamedame.R;
import com.sansen.myandroidlifegamedame.adapter.DialogListAdapter;
import com.sansen.myandroidlifegamedame.databinding.ActivityKeepgameBinding;
import com.sansen.myandroidlifegamedame.utils.Constant;
import com.sansen.myandroidlifegamedame.utils.StoreUtils;

import java.io.File;

//保存的游戏列表
//item主要显示玩家定义的名称，这个保存创建的时间。
//超过十条的时候显示搜索按钮，提供搜索功能。增加排序功能，增加按时间搜索功能。
//打算将现在的预设功能放在这里，直接整成一个列表。
public class KeepGameActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ActivityKeepgameBinding databind;
    private File[] files = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databind = DataBindingUtil.setContentView(this, R.layout.activity_keepgame);
        init();
    }
    private void init(){
        files = StoreUtils.getInstance().getTxtList(this);
        DialogListAdapter adapter = new DialogListAdapter(this,files);
        databind.keepgameLv.setAdapter(adapter);
        databind.keepgameLv.setOnItemClickListener(this);

    }

    //点击事件。
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(files != null && files.length > 0){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("type",Constant.RESUME_GAME);
            intent.putExtra("file",files[position]);
            startActivity(intent);
            this.finish();
        }

    }
}
