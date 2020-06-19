package com.nabiki.ctp.md.jni;

import com.nabiki.ctp.md.internal.*;

public class MdCtpNatives {
    native public static int createChannel();

    native public static void destroyChannel();

    native public static void waitOnChannel(int channelId);

    native public static void signalChannel(int channelId);

    native public static void readChannel(int channelId, MdChannelData data);

    native public static void writeChannel(int channelId, MdChannelData data);

    native public static int createMdSession(LoginProfile profile, int channelId);

    native public static void destroyMdSession(int mdSessionid);
}
