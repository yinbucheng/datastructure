package com.bucheng.structure.net.netty2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName FixLengthDecoder
 * @Author buchengyin
 * @Date 2018/12/12 17:41
 * 这里是将netty中的ByteBuf转变为byte[]
 * --------------
 * 100|XXXXXXXXX|
 * ______________
 **/
public class FixLengthDecoder extends ByteToMessageDecoder {
    //这里表示占据的byte位,用的是一个Int类型进行存放
    private int headLength = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable())
            return;
        //先获取头，头是固定长度会记录，消息体的长度
        int len = in.readableBytes();
        if (len < headLength)
            return;
        //标记读地址如果没能够读取完就恢复标记
        in.markReaderIndex();
        int contentLength = in.readInt();
        if (len < contentLength + 4) {
            in.resetReaderIndex();
            return;
        }
       byte[] buffer = new byte[contentLength];
        in.readBytes(buffer);
        out.add(buffer);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
