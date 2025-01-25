package com.van.imu.location;

import android.os.SystemClock;

import com.van.comm.Location;
import com.van.imu.core.VanWork;
import com.van.jni.VanRadio;
import com.van.utils.Utils;

// GnssManager类管理GNSS（全球导航卫星系统）定位功能，
// 实现了NMEA解析和数据处理监听器接口
public final class GnssManager implements NmeaParser.OnLocationListener, VanRadio.OnDataListener {
    // NMEA解析器
    private final NmeaParser mParser;
    // 当前的位置信息
    private final Location mLocation;
    // 上次位置更新的时间戳
    private volatile long mUpdateTime;
    // 系统时间是否已经同步标志
    private boolean mSetSysTime;

    // 单例模式，确保全局只有一个GnssManager实例
    private static class SingletonInstance {
        private static final GnssManager instance = new GnssManager();
    }

    // 获取GnssManager的单例实例
    public static GnssManager instance() {
        return SingletonInstance.instance;
    }

    // 私有构造函数，初始化Location对象和NMEA解析器
    private GnssManager() {
        mLocation = new Location();

        mParser = new NmeaParser();
        mParser.setOnLocationListener(this); // 设置位置监听器
    }

    public void open() {
        mUpdateTime = 0;
        VanRadio.setOnDataListener(this);
    }

    public void close() {
        VanRadio.setOnDataListener(null);
    }

    @Override
    public void OnData(byte[] data) {
        mParser.onNmea(data, data.length);
    }


    @Override
    public void onLocation(Location location) {
        if (0 == location.getState()) {
            return;
        }

        mUpdateTime = Utils.uptime();

        if (!mSetSysTime) {
            long refTime = location.getTime();
            long curTime = System.currentTimeMillis();

            if (refTime-curTime > 1000) {
                SystemClock.setCurrentTimeMillis(refTime);
            }

            mSetSysTime = true;
        }

        if (location.hasDirection()) {
            // 校正方向,GPS方向是顺时针,与我们的坐标系相反
            location.setDirection(-location.getDirection());
            VanWork.instance().onGnss(location);
        }

        synchronized (this) {
            mLocation.set(location);
        }
    }

    // 获取当前位置，如果更新时间小于1秒，则返回最新位置
    public Location getLocation() {
        Location location = null;

        if (Utils.uptime() - mUpdateTime < 1000) {
            synchronized (this) {
                location = new Location(mLocation);
            }
        }

        return location;
    }

    public boolean getLocation(Location location) {
        if (Utils.uptime() - mUpdateTime < 1000) {
            synchronized (this) {
                location.set(mLocation);
            }

            return true;
        }

        return false;
    }
}
