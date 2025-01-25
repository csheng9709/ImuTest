package com.van.utils;

import com.van.comm.VanPos;
import com.van.jni.VanProj;

public class Projection {
    private static boolean mInit = false;

    public static boolean isInit() {
        return mInit;
    }

    /**
     * 初始化参考原点，调用转换前必须先调用此函数
     * @param longitude 经度
     * @param latitude  纬度
     * @return 成功与否
     */
    public static synchronized boolean init(double longitude, double latitude, int falseEasting, int falseNorthing) {
        // 如果已初始化，直接返回true
        if (mInit) {
            return true;
        }

        // 调用VanProj类的init方法进行初始化，并将结果赋值给mInit
        mInit = VanProj.init(longitude, latitude, falseEasting, falseNorthing);
        return mInit;
    }

    public static synchronized void unInit() {
        mInit = false;
    }

    // 将地理坐标转换为平面坐标
    public synchronized static boolean toPlane(VanPos pos) {
        if (!mInit) {
            return false;
        }

        return VanProj.lonlat2xy(pos.values());
    }

    // 将平面坐标转换为地理坐标
    public synchronized static boolean toGeode(VanPos pos) {
        if (!mInit) {
            return false;
        }

        return VanProj.xy2lonlat(pos.values());
    }
}
