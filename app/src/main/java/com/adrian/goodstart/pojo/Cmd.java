package com.adrian.goodstart.pojo;

import java.util.Arrays;

/**
 * Created by adrian on 16-3-20.
 */
public class Cmd {

    private Long id;
    private byte[] cmd;
    private String ip;
    private Integer type;   //命令类型。0表示开，1表示关
    private String ssid;

    public Cmd() {
    }

    public Cmd(Long id, byte[] cmd, String ip, Integer type, String ssid) {
        this.id = id;
        this.cmd = cmd;
        this.ip = ip;
        this.type = type;
        this.ssid = ssid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getCmd() {
        if (cmd == null) {
            cmd = new byte[0];
        }
        return cmd;
    }

    public void setCmd(byte[] cmd) {
        this.cmd = cmd;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    @Override
    public String toString() {
        return "Cmd{" +
                "id=" + id +
                ", cmd=" + Arrays.toString(cmd) +
                ", ip='" + ip + '\'' +
                ", type=" + type +
                ", ssid='" + ssid + '\'' +
                '}';
    }
}
