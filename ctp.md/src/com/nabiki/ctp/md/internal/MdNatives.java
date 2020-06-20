package com.nabiki.ctp.md.internal;

import com.nabiki.ctp.md.struct.CThostFtdcReqUserLoginField;
import com.nabiki.ctp.md.struct.CThostFtdcUserLogoutField;

public class MdNatives {
    native public static int createChannel();

    native public static void destroyChannel(int channelId);

    native public static int waitOnChannel(int channelId, long millis);

    native public static void signalChannel(int channelId);

    native public static void readChannel(int channelId, MdChannelData data);

    native public static void writeChannel(int channelId, MdChannelData data);

    native public static int createMdSession(LoginProfile profile, int channelId);

    native public static void destroyMdSession(int mdSessionid);
    
    native public static int ReqUserLogin(int mdSessionid, CThostFtdcReqUserLoginField reqUserLoginField, int requestId);

    native public static int ReqUserLogout(int mdSessionid, CThostFtdcUserLogoutField userLogout, int requestId);

    native public static int SubscribeMarketData(int mdSessionid, String[] instrumentID, int count);

    native public static int UnSubscribeMarketData(int mdSessionid, String[] instrumentID, int count);
}
