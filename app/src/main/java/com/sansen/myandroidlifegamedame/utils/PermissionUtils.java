package com.sansen.myandroidlifegamedame.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class PermissionUtils {
    private static PermissionUtils permission;
    private static String[] PERMISSION = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };
    private static int REQUEST_CODE_CONTACT = 100;

    public static PermissionUtils getInstance(){
        if(permission == null){
            synchronized (PermissionUtils.class){
                if(permission == null){
                    permission = new PermissionUtils();
                }
            }
        }
        return permission;
    }
    public void applyPermission(Activity context){
//        int myPermission = ActivityCompat.checkSelfPermission(context,)
        if(Build.VERSION.SDK_INT >=23 && Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){
            for(String str : PERMISSION){
                if(context.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED){
                    context.requestPermissions(PERMISSION,REQUEST_CODE_CONTACT);
                }
            }
        }
    }
}
