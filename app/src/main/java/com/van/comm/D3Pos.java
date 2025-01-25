package com.van.comm;

import androidx.annotation.NonNull;

// D3Pos类继承自VanPos类，表示三维坐标点，增加了z轴坐标
public class D3Pos extends VanPos {
    private double z;

    public D3Pos() {
    }

    public D3Pos(D3Pos point) {
        values[0] = point.x();
        values[1] = point.y();
        this.z = point.z;
    }

    public D3Pos(double x, double y, double z) {
        set(x, y, z);
    }

    // 设置x, y, z坐标
    public void set(double x, double y, double z) {
        values[0] = x;
        values[1] = y;
        this.z = z;
    }

    public double z() {
        return z;
    }

    // 克隆当前对象，返回新的D3Pos对象
    @NonNull
    @Override
    public D3Pos clone() {
        super.clone();
        return new D3Pos(values[0], values[1], z);
    }
}
