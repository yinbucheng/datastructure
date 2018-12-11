package com.bucheng.structure.net.netty.client;

import com.bucheng.structure.net.netty.server.MessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName NettyClient
 * @Author buchengyin
 * @Date 2018/12/11 15:53
 **/
public class NettyClient {
    public static void main(String[] args) {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(workGroup);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast("timeout",new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
                    ch.pipeline().addLast("decode1", new LineBasedFrameDecoder(1024));
                    ch.pipeline().addLast("decode2", new StringDecoder());
                    ch.pipeline().addFirst("encode1", new LineEncoder());
                    ch.pipeline().addFirst("encode2", new StringEncoder());
                    ch.pipeline().addLast("myHandler", new ClientMessageHandler());
                }
            });

            ChannelFuture sync = bootstrap.connect("127.0.0.1", 9090).sync();
            sync.addListener(new GenericFutureListener<Future<? super Void>>() {
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("启动成功");
                    }
                }
            });
            sync.channel().closeFuture().sync();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
