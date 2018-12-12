package com.bucheng.structure.test;

import com.bucheng.structure.collection.CASLinkedList;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @ClassName CASTest
 * @Author buchengyin
 * @Date 2018/12/12 14:50
 **/
public class CASTest {
    public static void main(String[] args) {
        final CASLinkedList<Integer> list = new CASLinkedList<Integer>();
//       final  ConcurrentLinked list = new ConcurrentLinked();
        Executor executor = Executors.newFixedThreadPool(10);

        for(int i=0;i<4;i++){
            executor.execute(new Runnable() {
                public void run() {
                    System.out.println("romve thread start:"+Thread.currentThread().getName());
                    for(;;){
                        Object result = list.remove();
                        if(result!=null) {
                            System.out.println(Thread.currentThread().getName()+":"+result);
                        }
                    }
                }
            });
        }

        for(int i=0;i<5;i++){
            executor.execute(new Runnable() {
                public void run() {
                    System.out.println("add Thread start:"+Thread.currentThread().getName());
                    for(int i=0;i<1000;i++) {
                        list.add(i);
                    }
                }
            });
        }


    }
}
