package com.sansen.myandroidlifegamedame.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StoreUtils {
    private static StoreUtils store;

    public static StoreUtils getInstance(){
        if (store == null){
            synchronized (StoreUtils.class){
                if (store == null) {
                    store = new StoreUtils();
                }
            }
        }
        return store;
    }

    /**
     * 保存到公共文件夹里。
     * @param context
     * @param fileName
     * @param content
     * @param subDir
     */
    public void saveTxt2Public(Context context,String fileName,String content,String subDir){
        String subDirection;
        if(!TextUtils.isEmpty(subDir)){
            if(subDir.endsWith("/")){
                subDirection = subDir.substring(0,subDir.length()-1);
            }else {
                subDirection = subDir;
            }
        }else {
            subDirection = "Documents";
        }
        //判断文件名是否存在。
        Cursor cursor = searchTxtFromPublic(context, subDir, fileName);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                Uri uri = Uri.withAppendedPath(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), "" + id);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), id);
                if (uri != null) {
                    OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                    if (outputStream != null) {
                        outputStream.write(content.getBytes());
                        outputStream.flush();
                        outputStream.close();
                    }
                }
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, subDirection);
            } else {

            }
            //设置文件类型
            contentValues.put(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_NONE);
            Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), contentValues);
            if (uri != null) {
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    outputStream.write(content.getBytes());
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 搜索公共目录普通文件
     */
    private static Cursor searchTxtFromPublic(Context context, String filePath, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
//            Log.e(TAG, "searchTxtFromPublic: fileName is null");
            return null;
        }
        if (!filePath.endsWith("/")) {
            filePath = filePath + "/";
        }

        String queryPathKey = MediaStore.Files.FileColumns.RELATIVE_PATH;
        String selection = queryPathKey + "=? and " + MediaStore.Files.FileColumns.DISPLAY_NAME + "=?";
        Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                new String[]{MediaStore.Files.FileColumns._ID, queryPathKey, MediaStore.Files.FileColumns.DISPLAY_NAME},
                selection,
                new String[]{filePath, fileName},
                null);

        return cursor;
    }

    /**
     *保存到应用专属的文件夹里。 这个是保存游戏记录。
     * @param context
     * @param fileName 文件名
     * @param content 想要保存的内容。
     */
    public void saveTxt(Context context ,String fileName,String content){
        File dirpath = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dirpath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        }else {//android版本需要在4.4以上
            dirpath = Environment.getExternalStorageDirectory().getAbsoluteFile();
        }
        String fileString = dirpath + File.separator + "lifeGame";
        File txtFileDirctory = new File(fileString);
        if(!txtFileDirctory.exists()){//判断文件夹是否存在，不存在则创建。
            txtFileDirctory.mkdirs();
        }
        SimpleDateFormat format = new SimpleDateFormat("dd-HH-mm-ss");
        Date date = new Date(System.currentTimeMillis());
        String time =format.format(date);
        File txtFile = new File(fileString + File.separator + fileName+time+".txt");

        FileOutputStream outStream = null;
        try {
            if(!txtFile.exists()){
                txtFile.createNewFile();
            }
//            outStream = context.openFileOutput(String.valueOf(txtFile),Context.MODE_PRIVATE);
            outStream = new FileOutputStream(txtFile);
            outStream.write(content.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(outStream != null) {
                    outStream.flush();
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * b保存到module的文件夹中。
     */
    public void moduleSaveTxt(Context context,String fileName,String content){
        File dirpath = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dirpath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        }else {//android版本需要在4.4以上
            dirpath = Environment.getExternalStorageDirectory().getAbsoluteFile();
        }
        String fileString = dirpath + File.separator + "lifeGameModule";
        File txtFileDirctory = new File(fileString);
        if(!txtFileDirctory.exists()){//判断文件夹是否存在，不存在则创建。
            txtFileDirctory.mkdirs();
        }
        SimpleDateFormat format = new SimpleDateFormat("dd-HH-mm-ss");
        Date date = new Date(System.currentTimeMillis());
        String time =format.format(date);
        File txtFile = new File(fileString + File.separator + fileName+time+".txt");

        FileOutputStream outStream = null;
        try {
            if(!txtFile.exists()){
                txtFile.createNewFile();
            }
//            outStream = context.openFileOutput(String.valueOf(txtFile),Context.MODE_PRIVATE);
            outStream = new FileOutputStream(txtFile);
            outStream.write(content.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(outStream != null) {
                    outStream.flush();
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 获取文件夹内所有文件的列表
     */
    public File[] getTxtList(Context context){
        File[] fileArr = null;
        File dirpath = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dirpath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        }else {//android版本需要在4.4以上
            dirpath = Environment.getExternalStorageDirectory().getAbsoluteFile();
        }
        String fileString = dirpath + File.separator + "lifeGame";
        File txtFileDirctory = new File(fileString);
        if(!txtFileDirctory.exists()){
            return null;//文件夹不存在，直接返回null， 表示当前并没有存档。
        }
        fileArr = txtFileDirctory.listFiles();
        return fileArr;
    }
    /**
     * 获取Module文件夹内所有文件的列表
     */
    public File[] getModuleTxtList(Context context){
        File[] fileArr = null;
        File dirpath = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dirpath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        }else {//android版本需要在4.4以上
            dirpath = Environment.getExternalStorageDirectory().getAbsoluteFile();
        }
        String fileString = dirpath + File.separator + "lifeGameModule";
        File txtFileDirctory = new File(fileString);
        if(!txtFileDirctory.exists()){
            return null;//文件夹不存在，直接返回null， 表示当前并没有存档。
        }
        fileArr = txtFileDirctory.listFiles();
        return fileArr;
    }
    /**
     * 读取文件中的数据
     */
    public String readTxt(Context context,File file){
        String data = null;
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
            byte[] temp = new byte[512];
            StringBuilder sb = new StringBuilder();
            int len = 0;
            while ((len = inputStream.read(temp)) > 0){
                sb.append(new String(temp,0,len));
            }
            data = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream !=null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }


}
