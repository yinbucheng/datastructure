package com.bucheng.structure.net.nio;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName NioUtils
 * @Author buchengyin
 * @Date 2018/12/11 15:25
 **/
public class NioUtils {

    public static Selector reboudSelector(Selector selector, AtomicInteger count) {
        try {
            System.out.println("------------------------->rebuid selector<-------------------------");
            Set<SelectionKey> keys = selector.keys();
            Selector newSelector = Selector.open();
            if (keys != null) {
                for (SelectionKey key : keys) {
                    SelectableChannel channel = (SelectableChannel) key.attachment();
                    channel.register(newSelector, key.interestOps(), channel);
                    key.cancel();
                }
            }
            count.getAndSet(0);
            return newSelector;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String currentTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }
}
