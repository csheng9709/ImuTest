package com.van.imu.core;

import com.van.comm.GnssPos;
import com.van.comm.ImuVal;
import com.van.comm.Location;
import com.van.comm.VanPos;
import com.van.utils.Projection;
import com.van.utils.Utils;

public final class VanWork {
    private static final String TAG = "VanWork";
    private final GnssPos mGnssPos;
    private final ImuVal mImuVal;
    private long imuTime;

    private static class SingletonInstance {
        private static final VanWork instance = new VanWork();
    }

    public static VanWork instance() {
        return SingletonInstance.instance;
    }

    private VanWork() {
        mGnssPos = new GnssPos();
        mImuVal = new ImuVal();
    }

    public boolean getImu(ImuVal val) {
        if (Utils.uptime() - imuTime > 200) {
            return false;
        }

        synchronized (this) {
            val.gyroX = mImuVal.gyroX;
            val.gyroY = mImuVal.gyroY;
            val.gyroZ = mImuVal.gyroZ;
            val.accelX = mImuVal.accelX;
            val.accelY = mImuVal.accelY;
            val.accelZ = mImuVal.accelZ;
        }

        return true;
    }

    public void onGnss(Location location) {
        VanPos pos = new VanPos(location.getLongitude(), location.getLatitude());
        Projection.toPlane(pos);

        synchronized (this) {
            mGnssPos.state = location.getState();
            mGnssPos.time = Utils.uptime();
            mGnssPos.heading = location.getDirection();   // 度
            mGnssPos.speed = location.getSpeed();       // km/h
            mGnssPos.x = pos.x();
            mGnssPos.y = pos.y();
            mGnssPos.z = location.getAltitude();
        }
    }

    public void onImu(ImuVal val) {
        synchronized (this) {
            mImuVal.gyroX = val.gyroX;
            mImuVal.gyroY = val.gyroY;
            mImuVal.gyroZ = val.gyroZ;
            mImuVal.accelX = val.accelX;
            mImuVal.accelY = val.accelY;
            mImuVal.accelZ = val.accelZ;
        }

        imuTime = Utils.uptime();

        calc();
    }

    private void calc() {

    }
}
