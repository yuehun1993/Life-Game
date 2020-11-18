package com.sansen.myandroidlifegamedame.utils;


/**
 *直接写页面里太乱了，所以把功能类的东西，都集合到这个工具类里。到时候修改，查看也简单。
 */
public class MapUtils {
    private static MapUtils mapUtils;

    //单例，避免重复调用这个类
    public static MapUtils getInstance(){
        if(mapUtils == null){
            synchronized (MapUtils.class){
                if (mapUtils == null){
                    mapUtils = new MapUtils();
                }
            }
        }
        return mapUtils;
    }

    /**
     * 根据上一次活着的点，来判断下一回合又有谁会活着。
     * @param nowLife 传进来现在活着的点。
     * @return
     */
    public int[][] nextLife(int[][] nowLife){
        int[][] life = null;
        if(nowLife == null || nowLife.length<1 ){
            return life;
        }
        life = new int[nowLife.length][nowLife[0].length];
        //进行逻辑判断。生命游戏的规则是当周围有两个点的时候，则保持不变，当周围有三个点，则该点为活。周围有四个及以上或小于两个则，死。
        //第一步，拿到需要进行判断的点。1.1拿到点，1.2去重。1.3进行判断并返回。
//        if(nowLife)
        int[][] temporaryArr = new int[nowLife.length][nowLife[0].length];
        //取得点及其周边八个位置。
        for(int i =0; i<nowLife.length;i++){
            for (int j = 0 ;j<nowLife[0].length;j++){
                if(nowLife[i][j] != 0 ){
                    //设置需要进行判断的点的二维数组值为1.
                    temporaryArr[i][j] = 1;
                    if(i-1>=0) {
                        temporaryArr[i - 1][j] = 1;//左边
                    }
                    if(j-1>=0){
                        temporaryArr[i][j-1] = 1;//上边
                    }
                    if(i-1>=0 && j-1>=0){
                        temporaryArr[i-1][j-1] = 1;//左上。
                    }
                    if(i+1<nowLife.length){
                        temporaryArr[i+1][j] = 1;//右边
                    }
                    if(j+1<nowLife[0].length){
                        temporaryArr[i][j+1] = 1;//下边
                    }
                    if(j+1<nowLife[0].length && i+1<nowLife.length){
                        temporaryArr[i+1][j+1] = 1;//左下
                    }
                    if(i-1>=0 && j+1<nowLife[0].length){
                        temporaryArr[i-1][j+1] = 1;//右下
                    }
                    if(i+1<nowLife.length && j-1>=0){
                        temporaryArr[i+1][j-1] = 1;//左上
                    }
                }
            }
        }
        //temporaryArr就是所有需要进行判断的点。
        //进行判断，并将活着的点付给life.
        for(int i = 0; i<temporaryArr.length;i++){
            for (int j = 0;j<temporaryArr[0].length;j++){
                int sumLifeNeighbor = 0;
                if(i-1>=0 && nowLife[i - 1][j] == 1) {//左边
                    sumLifeNeighbor++;
                }
                if(j-1>=0 && nowLife[i][j-1] == 1){//上边
                    sumLifeNeighbor++;
                }
                if(i-1>=0 && j-1>=0 && nowLife[i-1][j-1] == 1){//左上。
                    sumLifeNeighbor++;
                }
                if(i+1<nowLife.length && nowLife[i+1][j] == 1){//右边
                    sumLifeNeighbor++;
                }
                if(j+1<nowLife[0].length && nowLife[i][j+1] == 1){//下边
                    sumLifeNeighbor++;
                }
                if(j+1<nowLife[0].length && i+1<nowLife.length && nowLife[i+1][j+1] == 1){//左下
                    sumLifeNeighbor++;
                }
                if(i-1>=0 && j+1<nowLife[0].length &&nowLife[i-1][j+1] == 1){//右下
                    sumLifeNeighbor++;
                }
                if(i+1<nowLife.length && j-1>=0 && nowLife[i+1][j-1] == 1){//左上
                    sumLifeNeighbor++;
                }

                if(sumLifeNeighbor == 3) {
                    life[i][j] = 1;
                }else if(sumLifeNeighbor == 2){
                    life[i][j] = nowLife[i][j];
                }else{
                    life[i][j] = 0;
                }
            }
        }
        return life;
    }
}
