package com.sansen.myandroidlifegamedame.module;

public class Classic {
    //预写几个经典的模块 目前是滑翔机，
    private static Classic classic;

    public static Classic getInstance(){
        if(classic == null){
            synchronized (Classic.class){
                if(classic == null){
                    classic = new Classic();
                }
            }
        }
        return classic;
    }
    /**
     * 在原有的基础上，添加新的模块。
     */
    public int[][] superpositionModule(int[][] nowGame,int[][] newModule){
        for(int i = 0;i<newModule.length;i++){
            for(int j = 0 ;j<newModule[0].length;j++){
                if(newModule[i][j] != 0){
                    nowGame[i][j] = newModule[i][j];
                }
            }
        }
        return nowGame;
    }
    /**
     * 修改新模块的位置
     */
    public int[][] superpositionModule(int[][] nowGame,int[][] newModule ,int initX,int initY){

        for (int i = 0;i<newModule.length;i++){
            for (int j = 0;j<newModule[0].length;j++){
                if(newModule[i][j] != 0){
                    if(initX+i<nowGame.length &&initY+j<nowGame[0].length){
                        nowGame[initX+i][initY+j] = 1;
                    }
                }
            }
        }
        return nowGame;
    }
    /**
     * 增加单个的点。
     */
    public int[][] addOnePort(int[][] nowlife,int initX,int initY){
        if(initY<=nowlife.length &&initX<=nowlife[0].length) {
            nowlife[initX][initY] = 1;
        }
        return nowlife;
    }
    /**
     * 删除单个的点
     */
    public int[][] removeOnePort(int[][] nowlife,int initX,int initY){
        if(initY<nowlife.length &&initX<nowlife[0].length) {
            nowlife[initX][initY] = 0;
        }
        return nowlife;
    }

    /**
     * 滑翔机
     * @param row 行
     * @param column 列
     * @return
     */
    public int[][] glider(int row,int column){
        int[][] lifeGame = new int[row][column];
//        lifeGame[]
        lifeGame[0][3] = 1;
        lifeGame[1][1] = 1;
        lifeGame[1][3] = 1;
        lifeGame[2][2] = 1;
        lifeGame[2][3] = 1;
        return lifeGame;
    }
    /**
     * 滑翔机
     * @param glider 已经有数据的数组，判断需要在外部做，在里面做的话，宽和高不好添加。
     * @return
     */
    public int[][] glider(int[][] glider){
//        lifeGame[]
        glider[0][3] = 1;
        glider[1][1] = 1;
        glider[1][3] = 1;
        glider[2][2] = 1;
        glider[2][3] = 1;
        return glider;
    }
    /**
     * 滑翔机
     * @param glider 已经有数据的数组，判断需要在外部做，在里面做的话，宽和高不好添加。
     * @return
     */
    public int[][] glider(int[][] glider,int initX,int initY){
//        lifeGame[]
        if(initX+2 < glider.length && initY+3 < glider[0].length){
            glider[initX+0][initY+3] = 1;
            glider[initX+1][initY+1] = 1;
            glider[initX+1][initY+3] = 1;
            glider[initX+2][initY+2] = 1;
            glider[initX+2][initY+3] = 1;
        }
        return glider;
    }
    /**
     * 滑翔机
     * @param row 行
     * @param column 列
     * @param initX 初始x坐标
     * @param initY 初始y坐标
     * @return
     */
    public int[][] glider(int row,int column ,int initX,int initY){
        int[][] lifeGame = new int[row][column];
        if(initX+2 < row && initY+3 < column){
            lifeGame[initX+0][initY+3] = 1;
            lifeGame[initX+1][initY+1] = 1;
            lifeGame[initX+1][initY+3] = 1;
            lifeGame[initX+2][initY+2] = 1;
            lifeGame[initX+2][initY+3] = 1;
        }

        return lifeGame;

    }
}
