package com.van.imu.location;

import android.text.TextUtils;

import com.van.comm.Location;
import com.van.utils.Utils;

import java.nio.charset.Charset;
import java.util.Calendar;

class NmeaParser {
    public static final int BUF_SIZE = 2048;

    public interface OnLocationListener {
        void onLocation(Location location);
    }

    private OnLocationListener mOnLocationListener;
    private final byte[] mBuf = new byte[BUF_SIZE];
    private boolean mGetHead;
    private int mLength;
    private final Location mLocation;
    private final GGA mGGA;
    private final RMC mRMC;

    private float mDirection;
    private long mGGATime;
    private long mRMCTime;
    private long mHDTTime;
    private int  mFlag;
    private long mFirstTime;

    public NmeaParser() {
        mLocation = new Location();
        mGGA = new GGA();
        mRMC = new RMC();
    }

    public void setOnLocationListener(OnLocationListener listener) {
        mOnLocationListener = listener;
    }

    public void reset() {
        mGetHead = false;
        mLength = 0;
    }

    public void onNmea(byte[] data, int length) {
        for (int i=0; i<length; ++i) {
            if ('$' == data[i]) {
                reset();

                mGetHead = true;
                mBuf[mLength++] = data[i];
                continue;
            }

            if (!mGetHead) {
                continue;
            }

            mBuf[mLength++] = data[i];
            if (mLength >= BUF_SIZE) {
                reset();
                continue;
            }

            if ('\n' == data[i]) {
                parse(mBuf, mLength);
                reset();
            }
        }
    }

    private void parse(byte[] line, int length) {
        String str = verify(line, length);
        if (null == str) {
            return;
        }

        String[] items = str.split(",", -1);
        if (0 == items.length) {
            return;
        }

        if (items[0].contains("GGA")) {
            mFlag |= 1;
            parseGGA(items);
        } else if (items[0].contains("RMC")) {
            mFlag |= 2;
            parseRMC(items);
        } else if (items[0].contains("HDT")) {
            mFlag |= 4;
            parseHDT(items);
        }
    }

    private void parseGGA(String[] items) {
        mGGA.reset();

        if (items.length < 14) {
            mLocation.reset();
            return;
        }

        mGGA.time = items[1];
        if (TextUtils.isEmpty(mGGA.time)) {
            mLocation.reset();
            return;
        }

        mGGA.latitude = items[2];
        if (!TextUtils.isEmpty(mGGA.latitude)) {
            if (0 == items[3].compareTo("S")) {
                mGGA.latitude = "-" + mGGA.latitude;
            }
        }

        mGGA.longitude = items[4];
        if (!TextUtils.isEmpty(mGGA.longitude)) {
            if (0 == items[5].compareTo("W")) {
                mGGA.longitude = "-" + mGGA.longitude;
            }
        }

        mGGA.altitude = items[9];

        String state = items[6];
        if (!TextUtils.isEmpty(state)) {
            mGGA.state = Integer.parseInt(state);
        }

        String satellites = items[7];
        if (!TextUtils.isEmpty(satellites)) {
            mGGA.satellites = Integer.parseInt(satellites);
        }

        mGGA.accuracy = items[8];

        String age = items[13];
        if (!TextUtils.isEmpty(age)) {
            mGGA.age = Float.parseFloat(age);
        }

        mGGATime = Utils.uptime();
        if (0 == mFirstTime) {
            mFirstTime = mGGATime;
        }
    }

    private void parseRMC(String[] items) {
        mRMC.reset();

        if (items.length < 13) {
            return;
        }

        mRMC.time = items[1];
        mRMC.date = items[9];

        if (TextUtils.isEmpty(mRMC.time) || TextUtils.isEmpty(mRMC.date)) {
            return;
        }

        mRMC.latitude = items[3];
        if (!TextUtils.isEmpty(mRMC.latitude)) {
            if (0 == items[4].compareTo("S")) {
                mRMC.latitude = "-" + mRMC.latitude;
            }
        }

        mRMC.longitude = items[5];
        if (!TextUtils.isEmpty(mRMC.longitude)) {
            if (0 == items[6].compareTo("W")) {
                mRMC.longitude = "-" + mRMC.longitude;
            }
        }

        mRMC.speed = items[7];
        mRMC.heading = items[8];

        String state = items[2];
        if (!TextUtils.isEmpty(state)) {
            mRMC.state = state.charAt(0);
        }

        String mode = items[12];
        if (!TextUtils.isEmpty(mode)) {
            mRMC.mode = mode.charAt(0);
        }

        mRMCTime = Utils.uptime();
        if (3==mFlag && (mRMCTime-mFirstTime > 350)) {
            genLocation();
        }
    }

    private void parseHDT(String[] items) {
        if (items.length < 3) {
            return;
        }

        String str = items[1];
        if (!TextUtils.isEmpty(str) && !"0.000000".equals(str) && !"0.0000".equals(str)) {
            mDirection = Float.parseFloat(str);
            mHDTTime = Utils.uptime();
        }

        genLocation();
    }

    private void genLocation() {
        if (TextUtils.isEmpty(mRMC.time)) {
            return;
        }

        if (Math.abs(mGGATime-mRMCTime) > 350) {
            return;
        }

        mLocation.reset();

        Calendar calendar = Calendar.getInstance();

        int date = 0;
        if (!TextUtils.isEmpty(mRMC.date)) {
            date = Integer.parseInt(mRMC.date);
        }
        int day = date / 10000;
        int month = date / 100 % 100;
        int year = date % 100 + 2000;

        double dTime = Double.parseDouble(mGGA.time);
        int time = (int)dTime;

        int hour = time / 10000;
        int minute = time / 100 % 100;
        int second = time % 100;
        int milliSec = (int)((dTime - time)*1000  + 0.5);

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, milliSec);

        mLocation.setTime(calendar.getTimeInMillis() + 8*60*60*1000);

        if (!TextUtils.isEmpty(mGGA.latitude)) {
            double latitude = Double.parseDouble(mGGA.latitude);
            mLocation.setLatitude(DmToD(latitude));
        }

        if (!TextUtils.isEmpty(mGGA.longitude)) {
            double longitude = Double.parseDouble(mGGA.longitude);
            mLocation.setLongitude(DmToD(longitude));
        }

        if (!TextUtils.isEmpty(mGGA.altitude)) {
            mLocation.setAltitude(Double.parseDouble(mGGA.altitude));
        }

        mLocation.setState(mGGA.state);
        mLocation.setSatellites(mGGA.satellites);

        if (!TextUtils.isEmpty(mGGA.accuracy)) {
            mLocation.setAccuracy(Float.parseFloat(mGGA.accuracy));
        }

        if (!TextUtils.isEmpty(mRMC.speed)) {
            float speed = Float.parseFloat(mRMC.speed);
            mLocation.setSpeed((float)(speed * 1.852)); // 节(kn) -> km/h
        }

        if (!TextUtils.isEmpty(mRMC.heading)) {
            mLocation.setHeading(Float.parseFloat(mRMC.heading));
        }

        mLocation.setDiffAge(mGGA.age);

        if (Math.abs(mGGATime-mHDTTime) < 350) {
            mLocation.setDirection(mDirection);
        }

        if (null != mOnLocationListener) {
            mOnLocationListener.onLocation(mLocation);
        }
    }

    private String verify(byte[] data, int length) {
        int pos;
        int xor = data[1] & 0xff;

        for (pos = 2; pos < length; ++pos) {
            if ('*' == data[pos]) {
                break;
            }

            xor ^= data[pos] & 0xff;
        }

        if (length - pos < 4) {
            return null;
        }

        int checkSum;

        try {
            String num = new String(mBuf, pos+1, 2, Charset.defaultCharset());
            checkSum = Integer.parseInt(num, 16);
        } catch (Exception e) {
            return null;
        }

        if (xor != checkSum) {
            System.out.printf("CheckSum Error %02X, %02X; %s\n", xor, checkSum, new String(data));
            return null;
        }

        return new String(mBuf, 0, pos, Charset.defaultCharset());
    }

    private double DmToD(double data) {
        int value = (int)(data / 100);

        return value + (data - value * 100) / 60;
    }

    static final class GGA {
        public String time;
        public String latitude;
        public String longitude;
        public String altitude;
        public String accuracy;
        public int satellites;
        public int state;
        public float age;   // 差分数据延时

        public GGA() {
            reset();
        }

        public void reset() {
            time = "";
            latitude = "";
            longitude = "";
            altitude = "";
            accuracy = "";
            satellites = 0;
            state = 0;
            age = -1;
        }
    }

    static final class RMC {
        public String time;
        public String date;
        public String latitude;
        public String longitude;
        public String heading;
        public String speed;
        public char state;
        public char mode;

        public RMC() {
            reset();
        }

        public void reset() {
            time = "";
            date = "";
            latitude = "";
            longitude = "";
            heading = "";
            speed = "";
            state = 'V';
            mode = 'N';
        }
    }
}
