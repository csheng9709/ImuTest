package com.van.jni;

public final class VanRadio {
    static {
        System.loadLibrary("sys_radio");
    }

    public interface OnDataListener {
        void OnData(byte[] data);
    }

    private static OnDataListener mDataListener;

    public static void setOnDataListener(OnDataListener l) {
        mDataListener = l;
        setCallback(null==l ? 0 : 1);
    }

    public static native String getRadioSn();
    public static native String getRadioVersion();
    public static native String getRadioTxFrequency();
    public static native boolean setRadioTxFrequency(String freq);
    public static native String getRadioRxFrequency();
    public static native boolean setRadioRxFrequency(String freq);
    public static native int getRadioBaud();
    public static native boolean setRadioBaud(int baud); /* 9600 19200 */
    public static native int getRadioPwr();
    public static native boolean setRadioPwr(boolean high);
    public static native String getRadioPrt();
    public static native boolean setRadioPrt(String prt); /* TRANSEOT TRIMTALK TRIMMK3 */
    public static native boolean gnssWrite(byte[] data, int size);
    public static native int getWorkMode();
    public static native boolean setWorkMode(int mode);
    public static native int getRoverTrans();
    public static native boolean setRoverTrans(int type);
    public static native int getBaseTrans();
    public static native boolean setBaseTrans(int type);
    public static native String getCorsParam();
    public static native boolean setCorsParam(String param);
    public static native String getBaseServer();
    public static native boolean setBaseServer(String param);
    public static native boolean sysRestart();
    public static native boolean setConfig(boolean cfg);
    private static native boolean setCallback(int filter);

    public static void onCallback(byte[] data) {
        if (null != mDataListener) {
            mDataListener.OnData(data);
        }
    }
}
