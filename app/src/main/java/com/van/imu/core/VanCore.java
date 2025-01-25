package com.van.imu.core;

import com.van.imu.location.GnssManager;
import com.van.utils.Projection;

public class VanCore {

    // 内部静态类，用于实现单例模式
    private static class SingletonInstance {
        // 静态实例，确保VanCore类只创建一个实例
        private static final VanCore instance = new VanCore();
    }

    // 获取单例实例的方法
    public static VanCore instance() {
        return SingletonInstance.instance;
    }

    // 私有构造方法，防止外部直接创建实例
    private VanCore() {
        // 初始化投影坐标系，参数为经度、纬度、X偏移量和Y偏移量
        Projection.init(117.23, 34.31, 0, 0);
    }

    public void start() {
        GnssManager.instance().open();
        VanCan.instance().open();
    }

    public void stop() {
        VanCan.instance().close();
        GnssManager.instance().close();
    }
}
