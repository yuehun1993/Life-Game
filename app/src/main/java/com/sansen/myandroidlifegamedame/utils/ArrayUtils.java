package com.sansen.myandroidlifegamedame.utils;

import android.util.Log;
import android.widget.Toast;

/**
 * 关于数组的一些操作类。
 */

public class ArrayUtils {
    private static ArrayUtils arrayUtils;

    //单例
    public static ArrayUtils getInstance(){
        if(arrayUtils == null){
            synchronized (ArrayUtils.class){
                if(arrayUtils == null){
                    arrayUtils = new ArrayUtils();
                }
            }
        }
        return arrayUtils;
    }

    //二维数组，转稀疏数组。返回值可能为null
    public int[][] arrToSparse(int[][] arr){
        int[][] sparseArr = null;
        int sum =0;//二维数组里总共有多少数据。
        for (int i = 0; i<arr.length;i++){
            for (int j = 0; j<arr[0].length ; j++){
                if(arr[i][j] != 0){
                    sum++;
                }
            }
        }
        //稀疏数组
        sparseArr = new int[sum+1][3];
        sparseArr[0][0] = arr.length;
        sparseArr[0][1] = arr[0].length;
        sparseArr[0][2] = sum;
        int row = 0;//第几条数据。
        for (int i = 0;i<arr.length; i++){
            for (int j = 0;j<arr[0].length;j++){
                if(arr[i][j] != 0){
                    row++;
                    sparseArr[row][0] = i;//所在行
                    sparseArr[row][1] = j;//所在列
                    sparseArr[row][2] = arr[i][j];//数据是什么。
                }
            }
        }
        if(arr != null) {
            sparseArr = new int[arr.length][arr[0].length];

        }
        return sparseArr;
    }
    // 稀疏数组转成而二维数组,返回值可能为null.
    public int[][] sparseToArr(int[][] sparse){
        int[][] arr = null;
        if(sparse != null) {
            arr = new int[sparse[0][0]][sparse[0][1]];
            for (int i = 1;i<sparse.length;i++){
                //和上面的转换正好相反。
                arr[sparse[i][0]][sparse[i][1]] = sparse[i][2];
            }
        }else{
            Log.e("xioa_arrayUtils_err:","稀疏数组解析出错。");
        }
        return arr;
    }
    //稀疏数组转字符串，第几条数据，一维二维分隔符使用‘，’，值后面的分隔符使用‘x’


    //字符串转稀疏数组，第几条数据，一维二维分隔符为‘，’，值后面的分隔符使用‘x’

    //避免重复计算，直接将二维数组转成稀疏数组的字符串。
    //String每次改变都要生成新的String对象，StringBuffer是线程安全的，改变并不会生成新的对象，StringBuilder比StringBuffer快，是线程非安全的。
    public String arrToSparseStr(int[][] arr){
        int[][] sparseArr = null;
        StringBuilder sparseString = new StringBuilder();
        int sum =0;//二维数组里总共有多少数据。
        for (int i = 0; i<arr.length;i++){
            for (int j = 0; j<arr[0].length ; j++){
                if(arr[i][j] != 0){
                    sum++;
                }
            }
        }
        //稀疏数组
        sparseArr = new int[sum+1][3];
        sparseArr[0][0] = arr.length;
        sparseArr[0][1] = arr[0].length;
        sparseArr[0][2] = sum;
        sparseString.append((sum+1)+","+0+","+arr.length+","+arr[0].length+","+sum+"x");//传入源数组的行列，总个数。
        int row = 0;//第几条数据。
        for (int i = 0;i<arr.length; i++){
            for (int j = 0;j<arr[0].length;j++){
                if(arr[i][j] != 0){
                    row++;
                    sparseArr[row][0] = i;//所在行
                    sparseArr[row][1] = j;//所在列
                    sparseArr[row][2] = arr[i][j];//数据是什么。
                    sparseString.append((sum+1)+","+row+","+i+","+j+","+arr[i][j]+"x");//组成字符串
                }
            }
        }
        if(arr != null) {
            sparseArr = new int[arr.length][arr[0].length];

        }
        if(sparseString!=null) {
            return sparseString.toString();
        }else{
            return null;
        }
    }
    //根据String字符串得到它代表的二维数组。
    public int[][] sparseStrToArr(String str){
        int[][] sparseArr = null;//稀疏数组
        int[][] arr = null;//二维数组。
        //将str分隔，
        String[] strArr = str.split("x");
        String[] strArrHead = str.split(",");
        if(strArrHead[0] != null){
            int position = Integer.parseInt(strArrHead[0]);//总共几条
            if(position > 0){
                sparseArr = new int[position][3];
            }
        }
        for (int i = 0; i<strArr.length;i++){
            if(strArr[i] != null &&strArr[i].length()>0) {
                String[] inside = strArr[i].split(",");
                if(inside.length>1) {
                    //这里可能会报错，当数据异常的时候，无法成功转成int的时候。
                    int position = Integer.parseInt(inside[1]);//第几条
                    int row = Integer.parseInt(inside[2]);
                    int column = Integer.parseInt(inside[3]);
                    int value = Integer.parseInt(inside[4]);
                    sparseArr[position][0] = row;
                    sparseArr[position][1] = column;
                    sparseArr[position][2] = value;
                }
            }
        }
        if(sparseArr.length > 0){
            arr = sparseToArr(sparseArr);
        }

        return arr;

    }
    //以数组中最左上角的数据为零点，之前的空间全部置空。（保持形状不变，去除所有没用的空间）
    public int[][] ArrToReduce(int[][] arr){
        int[][] reduceArr = null;
        //所以，我需要知道这个新的数组的大小。   根绝for循环，我可以拿到最上面和最左边的两个点。确定左上角的坐标。
//        StringBuilder sparseString = new StringBuilder();
        int upSpot = 0;//最上面的点
        int leftSpot = 0;//最左边的点
        int rightSpot = 0;//最右边的点
        int downSpot = 0; //最下边的点。
        int sum =0;//二维数组里总共有多少数据。
        int maxX = arr.length*arr[0].length;
        int[][] tempArr = new int[maxX][2];
        for (int i = 0; i<arr.length;i++){
            for (int j = 0; j<arr[0].length ; j++){
                if(arr[i][j] != 0){
                    tempArr[sum] = new int[]{i,j};
                    sum++;
                    if(upSpot == 0){
                        upSpot = i;
                    }else {
                        if (upSpot>i){
                            upSpot = i;
                        }
                    }
                    if(downSpot == 0){
                        downSpot = i;
                    }else{
                        if(downSpot<i){
                            downSpot = i;
                        }
                    }
                    if(leftSpot == 0){
                        leftSpot = j;
                    }else{
                        if(leftSpot>j){
                            leftSpot = j;
                        }
                    }
                    if (rightSpot == 0){
                        rightSpot = j;
                    }else{
                        if(rightSpot < j){
                            rightSpot = j;
                        }
                    }
                }
            }
        }
        reduceArr = new int[downSpot - upSpot +1][rightSpot-leftSpot+1];
        for(int i = 0;i<sum ;i++){
            reduceArr[tempArr[i][0]-upSpot][tempArr[i][1]-leftSpot] = 1;
        }

        return reduceArr;
    }
}
