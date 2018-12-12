package com.bucheng.structure.net.netty2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @ClassName NettyServer
 * @Author buchengyin
 * @Date 2018/12/12 17:37
 **/
public class NettyServer {
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.handler(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    super.channelActive(ctx);
                    System.out.println("-----server----active");
                }

                @Override
                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                    super.channelInactive(ctx);
                    System.out.println("------server---inactive");
                }
            });

            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast("fixLengthDecode", new FixLengthDecoder());
                    ch.pipeline().addLast("stringDecode", new StringDecoder());
                    ch.pipeline().addFirst("stringEncoder", new StringEncoder());
                    ch.pipeline().addFirst("fixLengthEncode", new FixLengthEncoder());
                    ch.pipeline().addLast("myHandler", new SimpleChannelInboundHandler<String>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                            System.out.println("Recive:" + msg);
                        }

                    });
                }
            });

            ChannelFuture future = bootstrap.bind(9090).sync();
            System.out.println("服务器9090已经启动");
            future.channel().closeFuture().sync();
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
