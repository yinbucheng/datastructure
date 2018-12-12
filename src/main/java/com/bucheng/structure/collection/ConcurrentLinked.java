package com.bucheng.structure.collection;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @ClassName ConcurrentLinked
 * @Author buchengyin
 * @Date 2018/12/7 13:44
 **/
public class ConcurrentLinked {
    private volatile Node head;
    private volatile Node tail;
    private static Unsafe unsafe;
    private static long headOffset;
    private static long tailOffset;
    private volatile AtomicLong size = new AtomicLong(0);

    static{
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
            headOffset = unsafe.objectFieldOffset(ConcurrentLinked.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(ConcurrentLinked.class.getDeclaredField("tail"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Node{
        private volatile Node next;
        private  Object item;


        public Node(Node next, Object item) {
            this.next = next;
            this.item = item;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Object getItem() {
            return item;
        }

        public void setItem(Object item) {
            this.item = item;
        }

    }


    public void add(Object value){
        size.getAndIncrement();
        Node node = new Node(null,value);
        Node p = tail;
        if(p!=null){
            if(unsafe.compareAndSwapObject(this,tailOffset,p,node)){
                p.next = node;
                return;
            }
        }
        enqu(node);
    }

    private void enqu(Node node){
        for (;;) {
            Node p = tail;
            if (p == null) {
                if (unsafe.compareAndSwapObject(this, tailOffset, p,node)) {
                    head = tail;
                    return;
                }
            } else {
                if (unsafe.compareAndSwapObject(this, tailOffset, p, node)) {
                    p.next = node;
                    return;
                }
            }
        }
    }

    public Object remove(){
        if(head==null)
            return null;
        return dequ();
    }

    public Object dequ(){
        for (;;){
            Node p = head;
            if(p==null)
                return null;
            if(p.next!=null&&unsafe.compareAndSwapObject(this,headOffset,p,p.next)){
                size.decrementAndGet();
                Object value = p.item;
                p.next = null;
                return value;
            }else if(p.next==null&&tail==null&&unsafe.compareAndSwapObject(this,headOffset,p,null)){
                size.decrementAndGet();
                Object value = p.item;
                return value;
            }
        }
    }

    public long size(){
        return size.get();
    }

}