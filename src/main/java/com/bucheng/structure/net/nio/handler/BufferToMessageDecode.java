package com.bucheng.structure.net.nio.handler;

import java.nio.ByteBuffer;

/**
 * @ClassName BufferToMessageDecode
 * @Author buchengyin
 * @Date 2018/12/11 16:39
 **/
public interface BufferToMessageDecode <T>{
       T decode(ByteBuffer buffer);
}
