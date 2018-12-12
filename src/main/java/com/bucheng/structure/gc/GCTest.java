package com.bucheng.structure.gc;

import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName GCTest
 * @Author buchengyin
 * @Date 2018/12/12 15:56
 **/
public class GCTest {
    public static void main(String[] args) {
        for (; ; ) {
            test(1024 * 1024 * 40);
//            System.out.println("----------------->gc invoke");
//            System.gc();
        }
    }

    private static void test(int size) {
        List<Integer> data = new LinkedList<Integer>();
        for (int i = 0; i <= size; i++) {
            data.add(i);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


