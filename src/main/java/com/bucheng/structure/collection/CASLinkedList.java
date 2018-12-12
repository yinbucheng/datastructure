package com.bucheng.structure.collection;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @ClassName CASLinkedList
 * @Author buchengyin
 * @Date 2018/12/12 14:35
 **/
public class CASLinkedList<T> {
    private volatile Node head;
    private volatile Node tail;
    private static long headOffset;
    private static long tailOffset;
    private static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            headOffset = unsafe.objectFieldOffset(CASLinkedList.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(CASLinkedList.class.getDeclaredField("tail"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    class Node<T> {
        T value;
        volatile Node next;

        public Node(T value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    public void add(T value) {
        Node<T> p = tail;
        if (p != null) {
            Node<T> node = new Node<T>(value, null);
            if (unsafe.compareAndSwapObject(this, tailOffset, p, node)) {
                p.next = node;
            }
        }
        enqu(value);
    }

    private void enqu(T value) {
        Node<T> node = new Node<T>(value, null);
        for (; ; ) {
            Node<T> p = tail;
            if (p == null) {
                if (unsafe.compareAndSwapObject(this, tailOffset, p, node)) {
                    System.out.println("-----------------init head");
                    head = node;
                    break;
                }
            } else {
                if (unsafe.compareAndSwapObject(this, tailOffset, p, node)) {
                    p.next = node;
                    break;
                }
            }
        }
    }


    public T remove() {
        if (head == null) {
            return null;
        }
        for (; ; ) {
            Node<T> p = head;
            if (p == null) {
                return null;
            }
            if (p.next != null && unsafe.compareAndSwapObject(this, headOffset, p, p.next)) {
                p.next = null;
                return p.value;
            } else if ( p.next == null&&tail==null && unsafe.compareAndSwapObject(this, headOffset, p, null)) {
                p.next = null;
                return p.value;
            }
        }
    }
}
