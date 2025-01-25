package com.van.jni;

public final class VanProj {
    // 静态代码块，用于加载名为 "VanProj" 的本地库
    static {
        System.loadLibrary("VanProj");
    }

    public static native String getVersion();

    /**
     * 初始化参考原点，调用转换前必须先调用此函数
     *
     * @param longitude     中央子午线
     * @param latitude      基准纬度
     * @param falseEasting  东伪偏移
     * @param falseNorthing 北伪偏移
     * @return 是否成功
     */
    public static native boolean init(double longitude, double latitude, int falseEasting, int falseNorthing);

    /**
     * 经纬度转平面坐标
     *
     * @param values INOUT
     *               IN  values[0]:longitude, values[1]:latitude
     *               OUT values[0]:x, values[1]:y, x从西向东，y从南向北
     * @return 是否成功
     */
    public static native boolean lonlat2xy(double[] values);

    /**
     * 平面坐标转经纬度
     *
     * @param values INOUT
     *               IN  values[0]:x, values[1]:y
     *               OUT values[0]:longitude, values[1]:latitude
     * @return 是否成功
     */
    public static native boolean xy2lonlat(double[] values);
}
