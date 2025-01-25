package com.van.imu.core;

import com.van.comm.ImuVal;
import com.van.jni.VanMcu;
import com.van.utils.Utils;

public final class VanCan implements VanMcu.OnCanListener {
    private final int CHANNEL = 0;
    private final int[] filterIds;
    private static final int ID_181 = 0x181;
    private static final int ID_281 = 0x281;

    private volatile float pitch;
    private volatile float roll;
    private final ImuVal mImuVal;

    private static class SingletonInstance {
        private static final VanCan instance = new VanCan();
    }

    public static VanCan instance() {
        return SingletonInstance.instance;
    }

    private VanCan() {
        // 不能超14个
        filterIds = new int[] { ID_181, ID_281 };

        mImuVal = new ImuVal();
    }

    public void open() {
        VanMcu.setCanSpeed(CHANNEL, 125000);
        VanMcu.CanHwFilterClear(CHANNEL);
        VanMcu.CanFilterCtrl(CHANNEL, 1);

        for (int id : filterIds) {
            VanMcu.CanHwFilterAdd(CHANNEL, id, 0xFFFFFFFF);
        }

        VanMcu.setOnCanListener(this);
        tiltStart();
    }

    public void close() {
        VanMcu.setOnCanListener(null);
    }

    public void tiltStart() {
        write(0, new byte[]{0x01, 0x01});
    }

    public synchronized void write(int id, byte[] data) {
        if (null==data || data.length>8) {
            return;
        }

        VanMcu.CanWrite(CHANNEL, id, data);
    }

    public synchronized float getPitch() {
        return pitch;
    }

    public synchronized float getRoll() {
        return roll;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void OnCan(VanMcu.CanMsg msg) {
        int id = msg.id;
        byte[] data = msg.data;

        if ((msg.id&VanMcu.CAN_EFF_FLAG) != 0) {
            id &= 0x1FFFFFFF;
        } else {
            id &= 0x7FF;
        }

        /*String str = String.format(Locale.US, "channel=%d, Type=%s, ID=%08X, Data=%s",
                msg.channel, ((msg.id&VanMcu.CAN_EFF_FLAG) != 0) ? "Ext" : "Std",
                id, Utils.getHexString(data, data.length));
        System.out.println(str);*/

        switch (id) {
            case ID_181:
                parse181(data);
                break;
            case ID_281:
                parse281(data);
                break;
        }
    }

    public static void writeShort(byte[] data, int pos, short value) {
        data[pos++] = (byte) ((value >> 8) & 0xFF);
        data[pos] = (byte) (value & 0xFF);
    }

    public static int readU16(byte[] data, int pos) {
        return (data[pos] & 0xFF) | ((data[pos+1] & 0xFF) << 8);
    }

    private void parse181(byte[] data) {
        short x = (short) readU16(data, 0);
        short y = (short) readU16(data, 2);

        float pitch = x/100f;
        float roll = y/100f;

        synchronized (this) {
            this.pitch = -pitch;
            this.roll = roll;
        }

        short gyroX = (short) readU16(data, 4);
        short gyroY = (short) readU16(data, 6);

        mImuVal.gyroX = gyroX/100.0;
        mImuVal.gyroY = gyroY/100.0;

        /*System.out.printf("1 %.2f, %.2f, %.2f, %.2f\n",
                pitch, roll, gyroX/100.0, gyroY/100.0);*/
    }

    private void parse281(byte[] data) {
        short gyroZ = (short) readU16(data, 0);
        short accelX = (short) readU16(data, 2);
        short accelY = (short) readU16(data, 4);
        short accelZ = (short) readU16(data, 6);

        mImuVal.gyroZ  = gyroZ/100.0;
        mImuVal.accelX = accelX/100.0;
        mImuVal.accelY = accelY/100.0;
        mImuVal.accelZ = accelZ/100.0;

        VanWork.instance().onImu(mImuVal);

        /*System.out.printf("2 %.2f, %.2f, %.2f, %.2f\n",
                gyroZ/100.0, accelX/100.0, accelY/100.0, accelZ/100.0);*/
    }
}
