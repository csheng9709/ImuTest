package com.van.comm;

import androidx.annotation.NonNull;

public class VanPos implements Cloneable {
    // 存储坐标的数组，0: x坐标/经度, 1: y坐标/纬度
    protected final double[] values = new double[2];

    public VanPos() {
    }

    public VanPos(VanPos pos) {
        set(pos.x(), pos.y());
    }

    public VanPos(double x, double y) {
        set(x, y);
    }

    // 设置x和y坐标的值
    public void set(double x, double y) {
        values[0] = x;
        values[1] = y;
    }

    // 获取x坐标的值
    public double x() {
        return values[0];
    }

    // 获取y坐标的值
    public double y() {
        return values[1];
    }

    // 获取包含x和y坐标的数组
    public double[] values() {
        return values;
    }

    // 克隆当前对象，返回一个新的VanPos对象，复制当前对象的x和y坐标
    @NonNull
    @Override
    public VanPos clone() {
        return new VanPos(values[0], values[1]);
    }

    // 计算当前点与给定点之间的距离
    public double distanceTo(VanPos pos) {
        double dx = pos.x() - values[0];
        double dy = pos.y() - values[1];

        return Math.sqrt(dx * dx + dy * dy);
    }

    public void offset(VanPos pos) {
        values[0] += pos.x();
        values[1] += pos.y();
    }

    public void minus(VanPos pos) {
        values[0] -= pos.x();
        values[1] -= pos.y();
    }

    // 计算当前点到给定点的角度（以弧度表示）
    public double angleTo(VanPos pos) {
        double dx = pos.x() - values[0];
        double dy = pos.y() - values[1];

        return Math.atan2(dy, dx);
    }

    // 根据给定的角度和距离，计算并返回新的偏移点
    public VanPos posTo(double angle, double distance) {
        double x = distance * Math.cos(angle);
        double y = distance * Math.sin(angle);

        return new VanPos(values[0] + x, values[1] + y);
    }
}
