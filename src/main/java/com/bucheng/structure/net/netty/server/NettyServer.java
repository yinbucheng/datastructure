package com.bucheng.structure.net.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName NettyServer
 * @Author buchengyin
 * @Date 2018/12/11 15:41
 **/
public class NettyServer {
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.group(bossGroup, workGroup);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast("timeout", new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast("lengthDecoder",new LengthFieldBasedFrameDecoder(1024,0,4,0,4));
                    ch.pipeline().addLast("stringDecoder",new StringDecoder());
                    ch.pipeline().addFirst("stringEncoder",new StringEncoder());
                    ch.pipeline().addFirst("lengthEncoder",new LengthFieldPrepender(4));
                    ch.pipeline().addLast("myHandler", new SimpleChannelInboundHandler<String>() {
                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                            System.out.println("Recvie:"+msg);
                            ctx.writeAndFlush("lalala");
                        }
                    });
                }
            });

            ChannelFuture sync = bootstrap.bind(9090).sync();
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
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
