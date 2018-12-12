package com.bucheng.structure.net.netty2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @ClassName FixLengthEncoder
 * @Author buchengyin
 * @Date 2018/12/12 18:45
 * 这里是将快要写入到netty中ByteBuf添加一个头而头就是内容的长度，头的长度为4个字节
 **/
public class FixLengthEncoder extends MessageToByteEncoder<byte[]> {

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
       if(msg==null||msg.length==0)
           return;
       int length = msg.length;
       out.writeInt(length);
       out.writeBytes(msg);
    }
}
