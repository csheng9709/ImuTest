package com.van.comm;

/**
 * 矢量点
 */
public class VectorPos extends VanPos {
    private final float angle;

    // 构造函数，使用x和y坐标以及角度初始化矢量点
    public VectorPos(double x, double y, float angle) {
        super(x, y);
        this.angle = angle;
    }

    // 构造函数，使用VanPos对象和角度初始化矢量点
    public VectorPos(VanPos pos, float angle) {
        super(pos);
        this.angle = angle;
    }

    public float angle() {
        return angle;
    }
}
