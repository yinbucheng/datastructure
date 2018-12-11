package com.bucheng.structure.net.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class NIOClient {
    static Charset charset = Charset.forName("UTF-8");
    private static volatile Selector selector;
    private static volatile AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) {
        SocketChannel socketChannel = null;
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            //这里将java的nio客户端注册到Selector上面并将客户端存放到attachment中
            socketChannel.register(selector, SelectionKey.OP_CONNECT, socketChannel);
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 9090));
            System.out.println("--------------->客户端启动");
            for (; ; ) {
                long pretime = System.currentTimeMillis();
                if (selector.select(10) > 0) {
                    for (SelectionKey sk : selector.selectedKeys()) {
                        selector.selectedKeys().remove(sk);
                        if (sk.readyOps() == SelectionKey.OP_CONNECT) {
                            try {
                                SocketChannel client = (SocketChannel) sk.attachment();
                                client.finishConnect();
                                sk.interestOps(SelectionKey.OP_READ);
                                Thread.sleep(2000);
                                client.write(charset.encode("我们已经完成3次握手,时间为:"+NioUtils.currentTime()+"\r\n"));
                            } catch (Exception e) {
                                System.err.println("--------->连接失败:" + e);
                            }
                        }
                        if (sk.readyOps() == SelectionKey.OP_READ) {
                            count.getAndSet(0);
                            SocketChannel client = (SocketChannel) sk.attachment();
                            String content = "";
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            while (client.read(buffer) > 0) {
                                buffer.flip();
                                content += charset.decode(buffer);
                            }
                            System.out.println("Revice:" + content);
                            sk.interestOps(SelectionKey.OP_READ);
                            Thread.sleep(2000);
                            client.write(charset.encode("我是客户端消息，当前时间:"+NioUtils.currentTime()+"\r\n"));
                        }
                    }
                } else {
                    long endTime = System.currentTimeMillis();
                    if(endTime-pretime<10) {
                        int number = count.incrementAndGet();
                        if (number >= 512) {
                           selector = NioUtils.reboudSelector(selector,count);
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
