package com.adrian.goodstart.tool;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by adrian on 16-4-3.
 */
public class DevSearchClientUtil {

    private static final int PORT = 9876;

    private DatagramSocket detectSocket = null;
    private DatagramPacket outPacket = null;

    private ReceiveThread receiveThread;
    private SendThread sendThread;

    private NetTool netTool;
    private Context ctx;

    public DevSearchClientUtil(Context context) {
        ctx = context;
    }

    public void searchDev() {
        if (sendThread != null) {
            sendThread.cancel();
            sendThread = null;
            sendThread = new SendThread();
            sendThread.start();
        } else {
            sendThread = new SendThread();
            sendThread.start();
        }
        if (receiveThread == null) {
            receiveThread = new ReceiveThread();
            receiveThread.start();
        }
        if (netTool == null) {
            netTool = new NetTool(ctx);
        }
    }

    class SendThread extends Thread {
        @Override
        public void run() {
            LogUtil.e("SEARCH", "Send thread started.");
            try {
                if (detectSocket == null) {
                    detectSocket = new DatagramSocket(PORT);
                }

                int packetPort = 9999;

                // Broadcast address
                InetAddress hostAddress = InetAddress.getByName("255.255.255.255");
                String outMessage = "search_dev";
                byte[] buf = outMessage.getBytes();
                LogUtil.e("SEARCH", "Send " + outMessage + " to " + hostAddress);
                // Send packet to hostAddress:9999, server that listen
                // 9999 would reply this packet
                outPacket = new DatagramPacket(buf,
                        buf.length, hostAddress, packetPort);
                detectSocket.send(outPacket);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outPacket != null) {
                    outPacket = null;
                }
//                if (detectSocket != null) {
//                    detectSocket.close();
//                    detectSocket = null;
//                }
            }
        }

        public void cancel() {
            if (outPacket != null) {
                outPacket = null;
            }
//            if (detectSocket != null) {
//                detectSocket.close();
//                detectSocket = null;
//            }
        }
    }

    class ReceiveThread extends Thread {
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        DatagramSocket socket;
        @Override
        public void run() {

            LogUtil.e("SEARCH", "Receive thread started.");
            while (true) {
                try {
                    if (socket == null) {
                        socket = new DatagramSocket(10435);
                    }
                    detectSocket.receive(packet);
                    if (packet != null && !TextUtils.isEmpty(packet.getAddress().getHostAddress())) {
                        String rcvd = "Received from " + packet.getSocketAddress() + ", Data="
                                + new String(packet.getData(), 0, packet.getLength());
                        LogUtil.e("SEARCH", rcvd);
                        netTool.sendMsg(packet.getAddress().getHostAddress(), "scan" + netTool.getLocAddress() + " ( " + android.os.Build.MODEL + " ) ");

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
