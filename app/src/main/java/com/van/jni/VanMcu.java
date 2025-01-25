package com.van.jni;

public final class VanMcu {
    static {
        System.loadLibrary("sysmcu");
    }

    public interface OnAccListener {
        void OnAcc(int val);    // val 1-ACC ON, 0-ACC OFF.
    }

    public interface OnCanListener {
        void OnCan(CanMsg msg);
    }

    public interface OnInputListener {
        void OnInput(int index, int val);   // val 1-High, 0-Low.
    }

    public interface OnDebugListener {
        void OnDebug(String str);
    }

    public static class CanMsg {
        public int channel;
        public int id;
        public byte[] data;
    }

    public static int CAN_EFF_FLAG = 0x80000000;
    public static int CAN_RTR_FLAG = 0x40000000;
    private static OnAccListener mAccListener;
    private static OnCanListener mCanListener;
    private static OnInputListener mInputListener;
    private static OnDebugListener mDebugListener;
    private static int mFilter;

    private static final int ACC    = 0x01;
    private static final int CAN    = 0x02;
    private static final int INPUT  = 0x04;
    private static final int DEBUG  = 0x08;

    public static void setOnAccListener(OnAccListener l) {
        mAccListener = l;
        int filter = mFilter;

        if (null != l) {
            filter |= ACC;
        } else {
            filter &= ~ACC;
        }

        if (filter != mFilter) {
            mFilter = filter;
            setCallback(filter);
        }
    }

    public static void setOnCanListener(OnCanListener l) {
        mCanListener = l;
        int filter = mFilter;

        if (null != l) {
            filter |= CAN;
        } else {
            filter &= ~CAN;
        }

        if (filter != mFilter) {
            mFilter = filter;
            setCallback(filter);
        }
    }

    public static void setOnInputListener(OnInputListener l) {
        mInputListener = l;
        int filter = mFilter;

        if (null != l) {
            filter |= INPUT;
        } else {
            filter &= ~INPUT;
        }

        if (filter != mFilter) {
            mFilter = filter;
            setCallback(filter);
        }
    }

    public static void setOnDebugListener(OnDebugListener l) {
        mDebugListener = l;
        int filter = mFilter;

        if (null != l) {
            filter |= DEBUG;
        } else {
            filter &= ~DEBUG;
        }

        if (filter != mFilter) {
            mFilter = filter;
            setCallback(filter);
        }
    }

    public static native String getVersion();
    public static native int getAccState();
    public static native boolean PowerCtrl(int val);
    public static native int getPowerVoltage();
    public static native int getTemperature();
    public static native int getCanCount();
    public static native int getCanSpeed(int channel);
    public static native boolean setCanSpeed(int channel, int value);
    public static native boolean CanWrite(int channel, int id, byte[] data);
    public static native boolean CanFilterCtrl(int channel, int type); // 0-disable filter, 1-hardware filter, 2-software filter
    public static native boolean CanHwFilterAdd(int channel, int id, int mask);
    public static native boolean CanHwFilterClear(int channel);
    public static native boolean CanSwFilterAdd(int channel, int id, int mask);
    public static native boolean CanSwFilterClear(int channel);
    public static native int getBlockCount();
    public static native boolean BlockWrite(int index, byte[] data);
    public static native boolean BlockRead(int index, byte[] buf, int length);
    public static native int getInputCount();
    public static native int InputGet(int index);
    public static native int getOutputCount();
    public static native int OutputGet(int index);
    public static native boolean OutputSet(int index, int value);
    public static native boolean UpdateFirmware(String path);

    private static native boolean setCallback(int filter);

    public static void onCallback(int type, byte[] data) {
        switch (type) {
            case ACC:
                if (null != mAccListener) {
                    mAccListener.OnAcc(data[0]);
                }
                break;
            case CAN:
                if (null != mCanListener) {
                    int id = getInt(data, 1, 4);

                    CanMsg msg = new CanMsg();
                    msg.channel = data[0];
                    msg.id = id;

                    if (data[5] > 0) {
                        msg.data = new byte[data[5]];
                        System.arraycopy(data, 6, msg.data, 0, msg.data.length);
                    }

                    mCanListener.OnCan(msg);
                }
                break;
            case INPUT:
                if (null != mInputListener) {
                    mInputListener.OnInput(data[0], data[1]);
                }
                break;
            case DEBUG:
                if (null != mDebugListener) {
                    mDebugListener.OnDebug(new String(data));
                }
                break;
        }
    }

    private static int getInt(byte[] buf, int pos, int size) {
        int value = 0;

        switch (size) {
            case 1:
                value = buf[pos] & 0xFF;
                break;
            case 2:
                value = ((buf[pos] & 0xFF) << 8) + (buf[pos+1] & 0xFF);
                break;
            case 4:
                value = ((buf[pos] & 0xFF) << 24) + ((buf[pos+1] & 0xFF) << 16)
                        + ((buf[pos+2] & 0xFF) << 8) + (buf[pos+3] & 0xFF);
                break;
            default:
                break;
        }

        return value;
    }
}
