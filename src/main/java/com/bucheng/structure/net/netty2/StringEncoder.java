package com.bucheng.structure.net.netty2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @ClassName StringEncoder
 * @Author buchengyin
 * @Date 2018/12/12 18:53
 * 将字符串转变为ByteBuf
 **/
public class StringEncoder extends MessageToMessageEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
       byte[] buffer = msg.getBytes("utf-8");
       out.add(buffer);
    }
}
