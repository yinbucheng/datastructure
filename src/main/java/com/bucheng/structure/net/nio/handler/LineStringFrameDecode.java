package com.bucheng.structure.net.nio.handler;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @ClassName LineStringFrameDecode
 * @Author buchengyin
 * @Date 2018/12/11 16:43
 **/
public class LineStringFrameDecode implements BufferToMessageDecode<String> {

    private Charset charset = Charset.forName("UTF-8");

    public String decode(ByteBuffer buffer) {
        buffer.flip();
        String contnent = charset.decode(buffer) + "";
        if(contnent.endsWith("\r\n")){
            contnent=contnent.substring(0,contnent.length()-2);
        }else if(contnent.endsWith("\n")){
            contnent=contnent.substring(0,contnent.length()-1);
        }
        return contnent;
    }
}
