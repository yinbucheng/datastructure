package com.bucheng.structure.net.nio;

import com.bucheng.structure.net.nio.handler.LineStringFrameDecode;
import com.bucheng.structure.net.nio.handler.StringLineEncode;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class NIOServer {
    //这两个Selector专门用来注册ACCPET事件
    private static volatile Selector bossSelector = null;
    //这个用来处理READ事件
    private static volatile Selector workSelector = null;
    private static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {
        bossSelector = Selector.open();
        workSelector = Selector.open();
        ServerSocketChannel server = null;
        try {
            server = ServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress("127.0.0.1", 9090);
            server.socket().bind(address);
            server.configureBlocking(false);
            server.register(bossSelector, SelectionKey.OP_ACCEPT, server);
            //这个线程专门负责处理新连接
            Thread thread1 = new Thread(new Runnable() {

                public void run() {
                    try {
                        while (bossSelector.select() > 0) {
                            for (SelectionKey sk : bossSelector.selectedKeys()) {
                                bossSelector.selectedKeys().remove(sk);
                                if (sk.readyOps() == SelectionKey.OP_ACCEPT) {
                                    ServerSocketChannel nioServer = (ServerSocketChannel) sk.attachment();
                                    SocketChannel sc = nioServer.accept();
                                    sc.configureBlocking(false);
                                    //将客户端绑定到selector上面并注册事件
                                    sc.register(workSelector, SelectionKey.OP_READ, sc);
                                    Thread.sleep(2000);
                                    StringLineEncode encode = new StringLineEncode();
                                    sc.write(encode.encode("获取到客户端请求连接,当前时间为:" + NioUtils.currentTime()));
                                    //再次向selector上面注册事件
                                    sk.interestOps(SelectionKey.OP_ACCEPT);
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("error:" + e);
                    }
                }
            });

            //这个线程专门负责处理读事件
            Thread thread2 = new Thread(new Runnable() {

                public void run() {
                    try {
                        for (int i = 0; i < Integer.MAX_VALUE; i++) {
                            long pretime = System.currentTimeMillis();
                            if (workSelector.select(10) > 0) {
                                try {
                                    Set<SelectionKey> selectionKeys = workSelector.selectedKeys();
                                    for (SelectionKey sk : selectionKeys) {
                                        workSelector.selectedKeys().remove(sk);
                                        if (sk.readyOps() == SelectionKey.OP_READ) {
                                            count.getAndSet(0);
                                            SocketChannel channel = (SocketChannel) sk.attachment();
                                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                                            try {
                                                LineStringFrameDecode decode = new LineStringFrameDecode();
                                                while (channel.read(buffer) > 0) {
                                                    String content = decode.decode(buffer);
                                                    System.out.println("Revice:" + content);
                                                }
                                                sk.interestOps(SelectionKey.OP_READ);
                                                Thread.sleep(3000);
                                                StringLineEncode encode = new StringLineEncode();
                                                channel.write(encode.encode("已经获取客户端消息,当前时间:" + NioUtils.currentTime()));
                                            } catch (Exception e) {
                                                //取消客户端上面事件
                                                sk.cancel();
                                                System.err.println("error:" + e);
                                                break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("ERROR:" + e);
                                }
                            } else {
                                long endtime = System.currentTimeMillis();
                                if (endtime - pretime < 10) {
                                    int number = count.incrementAndGet();
                                    if (number > 520) {
                                        workSelector = NioUtils.reboudSelector(workSelector, count);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("error:" + e);
                    }
                }
            });
            thread1.start();
            thread2.start();
            System.out.println("------------------>server start 9090");
            thread1.join();
            thread2.join();
        } finally {
            if (server != null) {
                server.close();
            }
        }
    }

}
