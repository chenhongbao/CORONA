package com.nabiki.ctp.trader.jni;

import com.nabiki.ctp.trader.internal.*;

public class CtpNatives {
    native public static int createChannel();

    native public static void destroyChannel();

    native public static void waitOnChannel(int channelId);

    native public static void signalChannel(int channelId);

    native public static void readChannel(int channelId, TraderChannelData data);

    native public static void writeChannel(int channelId, TraderChannelData data);

    native public static int createTraderSession(LoginProfile profile, int channelId);

    native public static void destroyTraderSession(int traderSessionId);
}
