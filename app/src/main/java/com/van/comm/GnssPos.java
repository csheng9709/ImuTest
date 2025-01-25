package com.van.comm;

public final class GnssPos {
    // 定位信息的状态
    public int state;
    public long time;
    // 定位信息的方向(单位：度)
    public double heading;
    // 定位信息的速度(单位：km/h)
    public double speed;
    // 定位信息的坐标
    public double x, y, z;
}
