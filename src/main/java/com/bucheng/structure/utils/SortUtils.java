package com.bucheng.structure.utils;

/**
 * @ClassName SortUtils
 * @Author buchengyin
 * @Date 2018/12/11 13:58
 **/
public abstract class SortUtils {

    public static void fastSort(int[] array,int start,int end){
        if(array==null||array.length==0||start>=end)
            return;
        int temp = array[start];
        int begainIndex = start;
        int endIndex = end;
        for(;;){
            if(begainIndex>=endIndex){
                array[begainIndex]=temp;
                break;
            }
            while(begainIndex<endIndex&&array[endIndex]>=temp){
                endIndex--;
            }
            array[begainIndex]=array[endIndex];
            while(begainIndex<endIndex&&array[begainIndex]<temp){
                begainIndex++;
            }
            array[endIndex]=array[begainIndex];
        }
        fastSort(array,start,begainIndex-1);
        fastSort(array,begainIndex+1,end);
    }
}
