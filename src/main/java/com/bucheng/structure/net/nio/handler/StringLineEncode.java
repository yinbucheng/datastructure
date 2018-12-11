package com.bucheng.structure.net.nio.handler;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @ClassName StringLineEncode
 * @Author buchengyin
 * @Date 2018/12/11 16:50
 **/
public class StringLineEncode implements MessageToBufferEncode<String> {
    Charset charset = Charset.forName("UTF-8");
    public ByteBuffer encode(String content) {
        if(!content.endsWith("\n")){
            content+="\r\n";
        }
        return charset.encode(content);
    }
}
