package com.bucheng.structure.net.netty2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @ClassName StringDecoder
 * @Author buchengyin
 * @Date 2018/12/12 18:50
 * 将byte[]数组转变为字符串
 **/
public class StringDecoder extends MessageToMessageDecoder<byte[]> {

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
        if(msg==null||msg.length==0)
            return;
        String content = new String(msg,"utf-8");
        out.add(content);
    }
}
