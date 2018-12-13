package com.bucheng.structure.net.netty2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName NettyClient
 * @Author buchengyin
 * @Date 2018/12/12 18:59
 **/
public class NettyClient {
    public static void main(String[] args) {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast("timeoutHandler",new IdleStateHandler(0,1,0, TimeUnit.SECONDS));
                    ch.pipeline().addLast("decoder1", new FixLengthDecoder());
                    ch.pipeline().addLast("decoder2",new StringDecoder());
                    ch.pipeline().addFirst("encode1",new StringEncoder());
                    ch.pipeline().addFirst("encode2",new FixLengthEncoder());
                    ch.pipeline().addLast("myHandler", new SimpleChannelInboundHandler<String>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                            System.out.println("Revice:"+msg);
//                            ctx.writeAndFlush("nice");
                        }

                        @Override
                        public void channelActive(final ChannelHandlerContext ctx) throws Exception {
                            System.out.println("------------>完成连接");
                            super.channelActive(ctx);
                        }

                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            super.userEventTriggered(ctx, evt);
                            if(evt instanceof IdleStateEvent){
                                IdleStateEvent event = (IdleStateEvent) evt;
                                if(event.state().equals(IdleState.WRITER_IDLE)){
                                    ctx.writeAndFlush("ping");
                                }
                            }
                        }
                    });
                }
            });
            ChannelFuture future = bootstrap.connect("127.0.0.1", 9090).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            workGroup.shutdownGracefully();
        }
    }
}
