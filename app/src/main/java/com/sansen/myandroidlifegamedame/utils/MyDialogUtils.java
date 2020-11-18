package com.sansen.myandroidlifegamedame.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.sansen.myandroidlifegamedame.R;
import com.sansen.myandroidlifegamedame.adapter.DialogListAdapter;
import com.sansen.myandroidlifegamedame.bean.ConfigBean;
import com.sansen.myandroidlifegamedame.callback.CommonCallBack;
import com.sansen.myandroidlifegamedame.callback.ConfigDialogCallBack;
import com.sansen.myandroidlifegamedame.callback.CoordinateCallBack;
import com.sansen.myandroidlifegamedame.callback.DialogCallBack;
import com.sansen.myandroidlifegamedame.utils.cypher.SM4Utils;

import java.io.File;
import java.util.List;

public class MyDialogUtils {
    private static MyDialogUtils dialogUtils;
    private Dialog dialogList;

    public static MyDialogUtils getInstance(){
        if(dialogUtils == null){
            synchronized (MyDialogUtils.class){
                if(dialogUtils == null){
                    dialogUtils = new MyDialogUtils();
                }
            }
        }

        return dialogUtils;
    }

    /**
     * 普通的dialog  进行当前界面的保存。
     * @param context
     * @param arr
     */
    public void showCommonDialog(final Context context , final int[][] arr){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_common,null);
        dialog.setTitle("存档");
        dialog.setCancelable(false);//点击外部不消失。
        dialog.setView(dialogView);
        final EditText et_name = dialogView.findViewById(R.id.dialog_et);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String  name = et_name.getText().toString().trim();
                if(name != null && name.length()>0){
                    String dataStr = ArrayUtils.getInstance().arrToSparseStr(arr) == null
                                    ? "数据为空，保存失败。":ArrayUtils.getInstance().arrToSparseStr(arr);
                    //进行加密
                    String password = SM4Utils.encryptCBC(dataStr,Constant.CYPHER);
                    //储存到本地
                    StoreUtils.getInstance().saveTxt(context,name,password);
//                    StoreUtils.getInstance().saveTxt2Public(context,name+".txt",password,"Documents/lifeGame");
                    Log.e("xioa_dialog","名称。"+name+"   content:"+dataStr+"         "+password);
                }else{
                    Toast.makeText(context, "名称不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("xioa_dialog","点击了取消按钮。");
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    /**
     * 普通的dialog  进行module当前界面的保存。
     * @param context
     * @param arr
     */
    public void showSaveModuleDialog(final Context context , final int[][] arr){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_common,null);
        dialog.setTitle("存档");
        dialog.setCancelable(false);//点击外部不消失。
        dialog.setView(dialogView);
        final EditText et_name = dialogView.findViewById(R.id.dialog_et);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String  name = et_name.getText().toString().trim();
                if(name != null && name.length()>0){
                    int[][] newArr = ArrayUtils.getInstance().ArrToReduce(arr);
                    String dataStr = ArrayUtils.getInstance().arrToSparseStr(newArr) == null
                                    ? "数据为空，保存失败。":ArrayUtils.getInstance().arrToSparseStr(newArr);
                    //进行加密
                    String password = SM4Utils.encryptCBC(dataStr,Constant.CYPHER);
                    //储存到本地
                    StoreUtils.getInstance().moduleSaveTxt(context,name,password);
//                    StoreUtils.getInstance().saveTxt2Public(context,name+".txt",password,"Documents/lifeGame");
                    Log.e("xioa_dialog","名称。"+name+"   content:"+dataStr+"         "+password);
                }else{
                    Toast.makeText(context, "名称不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("xioa_dialog","点击了取消按钮。");
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    /**
     * 列表dialog, 本地已保存的模块。
     * @param context
     * @param callBack
     * @param files
     */
    public void showListDialog(Context context , final DialogCallBack callBack, final File[] files){
        dialogList = new Dialog(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_filelist,null);
        final TextView tvCancel = dialogView.findViewById(R.id.dialog_cancel);
        ListView mlist = (ListView)dialogView.findViewById(R.id.dialog_lv);
        DialogListAdapter adapter = new DialogListAdapter(context,files);
        mlist.setAdapter(adapter);
        mlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //打算写一个callback
                if(callBack != null){
                    callBack.dialogItemClick(position,files);
                }
                dissmissListDialog();
//                this.dialog.dismiss();
            }
        });
        //加一个取消按钮。
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dissmissListDialog();
            }
        });
//        dialogList.setView(dialogView);
        dialogList.setCancelable(false);
        dialogList.setContentView(dialogView);
        if(!dialogList.isShowing()) {
            dialogList.show();
        }

    }

    /**
     *配置的dialog
     */
    public void showConfigDialog(Context context, final ConfigDialogCallBack callBack, ConfigBean bean){
        final Dialog dialog = new Dialog(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_config,null);
        //配置控件
        final EditText et_steps = dialogView.findViewById(R.id.dialog_config_et_steps);
        final EditText et_time = dialogView.findViewById(R.id.dialog_config_et_time);
        final EditText et_initx = dialogView.findViewById(R.id.dialog_config_et_initx);
        final EditText et_inity = dialogView.findViewById(R.id.dialog_config_et_inity);
        Button bt_cancel = dialogView.findViewById(R.id.dialog_config_bt_cancel);
        Button bt_determine = dialogView.findViewById(R.id.dialog_config_bt_determine);
        //根据实际情况填充数值
        if(bean != null){
            et_steps.setText((bean.getSteps() != 0 ? bean.getSteps() : "")+"");
            et_time.setText((bean.getIntervalTime() != 0 ? bean.getIntervalTime() : "")+"");
            et_initx.setText((bean.getInitx() != 0 ? bean.getInitx() : "")+"");
            et_inity.setText((bean.getInity() != 0 ? bean.getInity() : "")+"");
        }


        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进行判断，如果为空或者为null则设置成默认数值。steps:1 time 500 initx 0 inity 0
                 int steps = string2Int(et_steps.getText().toString().trim()) == 0 ? 1 : string2Int(et_steps.getText().toString().trim());
                 int time = string2Int(et_time.getText().toString().trim()) == 0 ? 500 : string2Int(et_time.getText().toString().trim());
                 int initx = string2Int(et_initx.getText().toString().trim());
                 int inity = string2Int(et_inity.getText().toString().trim());
                callBack.configClick(steps,time,initx,inity);
                dialog.dismiss();
            }
        });
        dialog.setContentView(dialogView);
        dialog.setCancelable(false);
        if(!dialog.isShowing()){
            dialog.show();
        }

    }
    public void showAddCoordinateDialog(final Context context, final int row, final int column, final CoordinateCallBack callBack){
        final Dialog dialog = new Dialog(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_addport,null);
        //配置控件
        final EditText et_x = dialogView.findViewById(R.id.dialog_addport_et_x);
        final EditText et_y = dialogView.findViewById(R.id.dialog_addport_et_y);
        Button bt_remove = dialogView.findViewById(R.id.dialog_addport_bt_remove);
        Button bt_add = dialogView.findViewById(R.id.dialog_addport_bt_add);
        Button bt_complete = dialogView.findViewById(R.id.dialog_addport_bt_complete);
        bt_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //首先判断当前位置是否有坐标
                int removex = string2Int2(et_x.getText().toString().trim());
                int removey = string2Int2(et_y.getText().toString().trim());
                if(removex<column &&removey<row && removex!=-1 && removey != -1) {
                    callBack.removePort(removex,removey);
                }else{
                    Toast.makeText(context,"坐标超出范围",Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加坐标,先判断坐标有没有超过限制，然后添加
                int addx = string2Int2(et_x.getText().toString().trim());
                int addy = string2Int2(et_y.getText().toString().trim());

                if(addx<column &&addy<row && addx!=-1 && addy != -1) {
                    callBack.coordinateAddClick(addx,addy);
                }else{
                    Toast.makeText(context,"坐标超出范围",Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.dismiss();
            }
        });
        dialog.setContentView(dialogView);
        dialog.setCancelable(false);
        if(!dialog.isShowing()){
            dialog.show();
        }
    }
    //配置地图的宽高
    public void showModuleConfigMapSize(final Context context, final CoordinateCallBack callBack){
        final Dialog dialog = new Dialog(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_module_map,null);
        final EditText et_row = dialogView.findViewById(R.id.dialog_module_map_et_row);
//        final EditText et_column = dialogView.findViewById(R.id.dialog_module_map_et_column);
        Button bt_cancel = dialogView.findViewById(R.id.dialog_module_map_bt_cancel);
        Button bt_determine = dialogView.findViewById(R.id.dialog_module_map_bt_determine);
        bt_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先判空，然后判断是否大于2.
                int row = string2Int2(et_row.getText().toString().trim());
//                int column = string2Int2(et_column.getText().toString().trim());
                if (row >1){
                    callBack.setMap(row,row);
                    dialog.dismiss();
                }else{
                    Toast.makeText(context,"请输入正常范围",Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(dialogView);
        dialog.setCancelable(false);
        if(!dialog.isShowing()){
            dialog.show();
        }
    }

    //用来判断是否开始手绘地图。

    /**
     *
     * @param context
     * @param callBack
     * @param title
     * @param tag   代表绘制还是擦除， 0绘制 1擦除
     */
    public void showModuleHandPaintDialog(Context context , final CommonCallBack callBack, String title, final int tag){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("请确定");
        dialog.setMessage(title);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(tag == 0) {
                    callBack.commonClick(true);
                }else {
                    callBack.eraseClick(true);
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (tag == 0) {
                    callBack.commonClick(false);
                }else{
                    callBack.eraseClick(false);
                }
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    //隐藏dialog的界面
    public void dissmissListDialog(){
        if(dialogList != null && dialogList.isShowing()){
            dialogList.dismiss();
        }
    }
    //判断数据不为null ,然后转成int型
    private int string2Int(String str){
        if(!TextUtils.isEmpty(str)){
            int mData = Integer.parseInt(str);
            return mData;
        }
        return 0;
    }
    //判断数据不为null ,然后转成int型
    private int string2Int2(String str){
        if(!TextUtils.isEmpty(str)){
            int mData = Integer.parseInt(str);
            return mData;
        }
        return -1;
    }
}
