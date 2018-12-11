package com.bucheng.structure.net.nio.handler;

import java.nio.ByteBuffer;

/**
 * @ClassName MessageToBufferEncode
 * @Author buchengyin
 * @Date 2018/12/11 16:42
 **/
public interface MessageToBufferEncode<T> {
    ByteBuffer encode(T object);
}
