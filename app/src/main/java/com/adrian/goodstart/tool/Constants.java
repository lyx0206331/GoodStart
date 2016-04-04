package com.adrian.goodstart.tool;

/**
 * Created by ranqing on 15/12/30.
 */
public class Constants {
    public static final String IP = "192.168.10.1";
    public static final int PORT = 4567;

    public static final int REQ_MODE_PARAM = 0x00;
    public static final int START_SCANNING = 0x01;
    public static final int STOP_SCANNING = 0x02;
    public static final int REQ_PROGRESS = 0x03;
    /**
     * 超时消息
     */
    public static final int MSG_TIME_OUT = 4;

    public static long curMillis;
}
