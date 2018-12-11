package com.bucheng.structure;

import com.bucheng.structure.utils.SortUtils;

public class DatastructureApplication {

	public static void main(String[] args) {
		int[] array = {1,5,7,9,2,4,0,4,2,1};
		SortUtils.fastSort(array,0,array.length-1);
		for(int i=0;i<array.length;i++){
			System.out.print(array[i]+" ");
		}
	}
}
