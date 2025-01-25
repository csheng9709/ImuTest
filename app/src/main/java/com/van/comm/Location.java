package com.van.comm;

public class Location {
    /**
     * Bit mask for mFieldsMask indicating the presence of mAltitude.
     */
    private static final int HAS_ALTITUDE_MASK = 1;
    /**
     * Bit mask for mFieldsMask indicating the presence of mSpeed.
     */
    private static final int HAS_SPEED_MASK = 2;
    /**
     * Bit mask for mFieldsMask indicating the presence of mHeading.
     */
    private static final int HAS_HEADING_MASK = 4;
    /**
     * Bit mask for mFieldsMask indicating the presence of mHorizontalAccuracy.
     */
    private static final int HAS_HORIZONTAL_ACCURACY_MASK = 8;
    /**
     * Bit mask for mFieldsMask indicating location is from a mock provider.
     */
    private static final int HAS_MOCK_PROVIDER_MASK = 16;
    /**
     * Bit mask for mFieldsMask indicating the presence of mVerticalAccuracy.
     */
    private static final int HAS_VERTICAL_ACCURACY_MASK = 32;
    /**
     * Bit mask for mFieldsMask indicating the presence of mSpeedAccuracy.
     */
    private static final int HAS_SPEED_ACCURACY_MASK = 64;
    /**
     * Bit mask for mFieldsMask indicating the presence of mBearingAccuracy.
     */
    private static final int HAS_HEADING_ACCURACY_MASK = 128;

    private static final int HAS_DIRECTION_MASK = 256;

    private long mTime = 0;
    private int mFieldsMask = 0;
    private double mLatitude = 0.0;
    private double mLongitude = 0.0;
    private double mAltitude = 0.0f;
    private float mHeading = 0.0f;
    private float mDirection = 0.0f;    // 方位角，双天线时有效
    private float mSpeed = 0.0f;        // km/h
    private float mHorizontalAccuracyMeters = 0.0f; // 水平精度
    private float mVerticalAccuracyMeters = 0.0f;   // 垂直精度
    private float mSpeedAccuracyMetersPerSecond = 0.0f;
    private float mHeadingAccuracyDegrees = 0.0f;
    private int mSatellites = 0;
    private int mState = 0;
    private float mDiffAge = -1;    // 差分数据延时，RTK定位时有效

    public Location() {
    }

    public Location(Location l) {
        set(l);
    }

    public void set(Location l) {
        mTime = l.mTime;
        mFieldsMask = l.mFieldsMask;
        mLatitude = l.mLatitude;
        mLongitude = l.mLongitude;
        mAltitude = l.mAltitude;
        mHeading = l.mHeading;
        mDirection = l.mDirection;
        mSpeed = l.mSpeed;
        mHorizontalAccuracyMeters = l.mHorizontalAccuracyMeters;
        mVerticalAccuracyMeters = l.mVerticalAccuracyMeters;
        mSpeedAccuracyMetersPerSecond = l.mSpeedAccuracyMetersPerSecond;
        mHeadingAccuracyDegrees = l.mHeadingAccuracyDegrees;
        mSatellites = l.mSatellites;
        mState = l.mState;
        mDiffAge = l.mDiffAge;
    }

    public void reset() {
        mTime = 0;
        mFieldsMask = 0;
        mLatitude = 0;
        mLongitude = 0;
        mAltitude = 0;
        mHeading = 0;
        mDirection = 0;
        mSpeed = 0;
        mHorizontalAccuracyMeters = 0;
        mVerticalAccuracyMeters = 0;
        mSpeedAccuracyMetersPerSecond = 0;
        mHeadingAccuracyDegrees = 0;
        mSatellites = 0;
        mState = 0;
        mDiffAge = -1;
    }

    public long getTime() {
        return mTime;
    }

    /**
     * Set the UTC time of this fix, in milliseconds since January 1,
     * 1970.
     *
     * @param time UTC time of this fix, in milliseconds since January 1, 1970
     */
    public void setTime(long time) {
        mTime = time;
    }

    /**
     * Get the latitude, in degrees.
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Set the latitude, in degrees.
     */
    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    /**
     * Get the longitude, in degrees.
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * Set the longitude, in degrees.
     */
    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    /**
     * True if this location has an altitude.
     */
    public boolean hasAltitude() {
        return (mFieldsMask & HAS_ALTITUDE_MASK) != 0;
    }

    /**
     * Get the altitude if available, in meters above the WGS 84 reference
     * ellipsoid.
     *
     * <p>If this location does not have an altitude then 0.0 is returned.
     */
    public double getAltitude() {
        return mAltitude;
    }

    /**
     * Set the altitude, in meters above the WGS 84 reference ellipsoid.
     *
     * <p>Following this call {@link #hasAltitude} will return true.
     */
    public void setAltitude(double altitude) {
        mAltitude = altitude;
        mFieldsMask |= HAS_ALTITUDE_MASK;
    }

    /**
     * Remove the altitude from this location.
     *
     * <p>Following this call {@link #hasAltitude} will return false,
     * and {@link #getAltitude} will return 0.0.
     *
     * @deprecated use a new Location object for location updates.
     */
    @Deprecated
    public void removeAltitude() {
        mAltitude = 0.0f;
        mFieldsMask &= ~HAS_ALTITUDE_MASK;
    }

    /**
     * True if this location has a speed.
     */
    public boolean hasSpeed() {
        return (mFieldsMask & HAS_SPEED_MASK) != 0;
    }

    /**
     * Get the speed if it is available, in meters/second over ground.
     *
     * <p>If this location does not have a speed then 0.0 is returned.
     */
    public float getSpeed() {
        return mSpeed;
    }

    /**
     * Set the speed, in meters/second over ground.
     *
     * <p>Following this call {@link #hasSpeed} will return true.
     */
    public void setSpeed(float speed) {
        mSpeed = speed;
        mFieldsMask |= HAS_SPEED_MASK;
    }

    /**
     * Remove the speed from this location.
     *
     * <p>Following this call {@link #hasSpeed} will return false,
     * and {@link #getSpeed} will return 0.0.
     *
     * @deprecated use a new Location object for location updates.
     */
    @Deprecated
    public void removeSpeed() {
        mSpeed = 0.0f;
        mFieldsMask &= ~HAS_SPEED_MASK;
    }

    /**
     * True if this location has a heading.
     */
    public boolean hasHeading() {
        return (mFieldsMask & HAS_HEADING_MASK) != 0;
    }

    /**
     * Get the bearing, in degrees.
     *
     * <p>Heading is the horizontal direction of travel of this device,
     * and is not related to the device orientation. It is guaranteed to
     * be in the range (0.0, 360.0] if the device has a heading.
     *
     * <p>If this location does not have a heading then 0.0 is returned.
     */
    public float getHeading() {
        return mHeading;
    }

    public boolean hasDirection() {
        return (mFieldsMask & HAS_DIRECTION_MASK) != 0;
    }

    public float getDirection() {
        return mDirection;
    }

    /**
     * Set the heading, in degrees.
     *
     * <p>Heading is the horizontal direction of travel of this device,
     * and is not related to the device orientation.
     *
     * <p>The input will be wrapped into the range (0.0, 360.0].
     */
    public void setHeading(float heading) {
        while (heading < 0.0f) {
            heading += 360.0f;
        }
        while (heading >= 360.0f) {
            heading -= 360.0f;
        }
        mHeading = heading;
        mFieldsMask |= HAS_HEADING_MASK;
    }

    public void setDirection(float direction) {
        while (direction < 0.0f) {
            direction += 360.0f;
        }
        while (direction >= 360.0f) {
            direction -= 360.0f;
        }
        mDirection = direction;
        mFieldsMask |= HAS_DIRECTION_MASK;
    }

    /**
     * Remove the heading from this location.
     *
     * <p>Following this call {@link #hasHeading} will return false,
     * and {@link #getHeading} will return 0.0.
     *
     * @deprecated use a new Location object for location updates.
     */
    @Deprecated
    public void removeHeading() {
        mHeading = 0.0f;
        mFieldsMask &= ~HAS_HEADING_MASK;
    }

    /**
     * True if this location has a horizontal accuracy.
     */
    public boolean hasAccuracy() {
        return (mFieldsMask & HAS_HORIZONTAL_ACCURACY_MASK) != 0;
    }

    /**
     * Get the estimated horizontal accuracy of this location, radial, in meters.
     *
     * <p>We define horizontal accuracy as the radius of 68% confidence. In other
     * words, if you draw a circle centered at this location's
     * latitude and longitude, and with a radius equal to the accuracy,
     * then there is a 68% probability that the true location is inside
     * the circle.
     *
     * <p>This accuracy estimation is only concerned with horizontal
     * accuracy, and does not indicate the accuracy of bearing,
     * velocity or altitude if those are included in this Location.
     */
    public float getAccuracy() {
        return mHorizontalAccuracyMeters;
    }

    /**
     * Set the estimated horizontal accuracy of this location, meters.
     *
     * <p>See {@link #getAccuracy} for the definition of horizontal accuracy.
     *
     * <p>Following this call {@link #hasAccuracy} will return true.
     */
    public void setAccuracy(float horizontalAccuracy) {
        mHorizontalAccuracyMeters = horizontalAccuracy;
        mFieldsMask |= HAS_HORIZONTAL_ACCURACY_MASK;
    }

    /**
     * Remove the horizontal accuracy from this location.
     *
     * <p>Following this call {@link #hasAccuracy} will return false, and
     * {@link #getAccuracy} will return 0.0.
     *
     * @deprecated use a new Location object for location updates.
     */
    @Deprecated
    public void removeAccuracy() {
        mHorizontalAccuracyMeters = 0.0f;
        mFieldsMask &= ~HAS_HORIZONTAL_ACCURACY_MASK;
    }

    /**
     * True if this location has a vertical accuracy.
     */
    public boolean hasVerticalAccuracy() {
        return (mFieldsMask & HAS_VERTICAL_ACCURACY_MASK) != 0;
    }

    /**
     * Get the estimated vertical accuracy of this location, in meters.
     *
     * <p>We define vertical accuracy at 68% confidence.  Specifically, as 1-side of the
     * 2-sided range above and below the estimated altitude reported by {@link #getAltitude()},
     * within which there is a 68% probability of finding the true altitude.
     *
     * <p>In the case where the underlying distribution is assumed Gaussian normal, this would be
     * considered 1 standard deviation.
     *
     * <p>For example, if {@link #getAltitude()} returns 150, and
     * {@link #getVerticalAccuracyMeters()} returns 20 then there is a 68% probability
     * of the true altitude being between 130 and 170 meters.
     *
     * <p>If this location does not have a vertical accuracy, then 0.0 is returned.
     */
    public float getVerticalAccuracyMeters() {
        return mVerticalAccuracyMeters;
    }

    /**
     * Set the estimated vertical accuracy of this location, meters.
     *
     * <p>See {@link #getVerticalAccuracyMeters} for the definition of vertical accuracy.
     *
     * <p>Following this call {@link #hasVerticalAccuracy} will return true.
     */
    public void setVerticalAccuracyMeters(float verticalAccuracyMeters) {
        mVerticalAccuracyMeters = verticalAccuracyMeters;
        mFieldsMask |= HAS_VERTICAL_ACCURACY_MASK;
    }

    /**
     * Remove the vertical accuracy from this location.
     *
     * <p>Following this call {@link #hasVerticalAccuracy} will return false, and
     * {@link #getVerticalAccuracyMeters} will return 0.0.
     *
     * @removed
     * @deprecated use a new Location object for location updates.
     */
    @Deprecated
    public void removeVerticalAccuracy() {
        mVerticalAccuracyMeters = 0.0f;
        mFieldsMask &= ~HAS_VERTICAL_ACCURACY_MASK;
    }

    /**
     * True if this location has a speed accuracy.
     */
    public boolean hasSpeedAccuracy() {
        return (mFieldsMask & HAS_SPEED_ACCURACY_MASK) != 0;
    }

    /**
     * Get the estimated speed accuracy of this location, in meters per second.
     *
     * <p>We define speed accuracy at 68% confidence.  Specifically, as 1-side of the
     * 2-sided range above and below the estimated speed reported by {@link #getSpeed()},
     * within which there is a 68% probability of finding the true speed.
     *
     * <p>In the case where the underlying
     * distribution is assumed Gaussian normal, this would be considered 1 standard deviation.
     *
     * <p>For example, if {@link #getSpeed()} returns 5, and
     * {@link #getSpeedAccuracyMetersPerSecond()} returns 1, then there is a 68% probability of
     * the true speed being between 4 and 6 meters per second.
     *
     * <p>Note that the speed and speed accuracy is often better than would be obtained simply from
     * differencing sequential positions, such as when the Doppler measurements from GNSS satellites
     * are used.
     *
     * <p>If this location does not have a speed accuracy, then 0.0 is returned.
     */
    public float getSpeedAccuracyMetersPerSecond() {
        return mSpeedAccuracyMetersPerSecond;
    }

    /**
     * Set the estimated speed accuracy of this location, meters per second.
     *
     * <p>See {@link #getSpeedAccuracyMetersPerSecond} for the definition of speed accuracy.
     *
     * <p>Following this call {@link #hasSpeedAccuracy} will return true.
     */
    public void setSpeedAccuracyMetersPerSecond(float speedAccuracyMeterPerSecond) {
        mSpeedAccuracyMetersPerSecond = speedAccuracyMeterPerSecond;
        mFieldsMask |= HAS_SPEED_ACCURACY_MASK;
    }

    /**
     * Remove the speed accuracy from this location.
     *
     * <p>Following this call {@link #hasSpeedAccuracy} will return false, and
     * {@link #getSpeedAccuracyMetersPerSecond} will return 0.0.
     *
     * @removed
     * @deprecated use a new Location object for location updates.
     */
    @Deprecated
    public void removeSpeedAccuracy() {
        mSpeedAccuracyMetersPerSecond = 0.0f;
        mFieldsMask &= ~HAS_SPEED_ACCURACY_MASK;
    }

    /**
     * True if this location has a heading accuracy.
     */
    public boolean hasHeadingAccuracy() {
        return (mFieldsMask & HAS_HEADING_ACCURACY_MASK) != 0;
    }

    /**
     * Get the estimated bearing accuracy of this location, in degrees.
     *
     * <p>We define bearing accuracy at 68% confidence.  Specifically, as 1-side of the
     * 2-sided range on each side of the estimated bearing reported by {@link #getHeading()},
     * within which there is a 68% probability of finding the true bearing.
     *
     * <p>In the case where the underlying distribution is assumed Gaussian normal, this would be
     * considered 1 standard deviation.
     *
     * <p>For example, if {@link #getHeading()} returns 60, and
     * {@link #getHeadingAccuracyDegrees()} returns 10, then there is a 68% probability of the
     * true bearing being between 50 and 70 degrees.
     *
     * <p>If this location does not have a bearing accuracy, then 0.0 is returned.
     */
    public float getHeadingAccuracyDegrees() {
        return mHeadingAccuracyDegrees;
    }

    /**
     * Set the estimated heading accuracy of this location, degrees.
     *
     * <p>See {@link #getHeadingAccuracyDegrees} for the definition of heading accuracy.
     *
     * <p>Following this call {@link #hasHeadingAccuracy} will return true.
     */
    public void setBearingAccuracyDegrees(float headingAccuracyDegrees) {
        mHeadingAccuracyDegrees = headingAccuracyDegrees;
        mFieldsMask |= HAS_HEADING_ACCURACY_MASK;
    }

    /**
     * Remove the heading accuracy from this location.
     *
     * <p>Following this call {@link #hasHeadingAccuracy} will return false, and
     * {@link #getHeadingAccuracyDegrees} will return 0.0.
     *
     * @removed
     * @deprecated use a new Location object for location updates.
     */
    @Deprecated
    public void removeHeadingAccuracy() {
        mHeadingAccuracyDegrees = 0.0f;
        mFieldsMask &= ~HAS_HEADING_ACCURACY_MASK;
    }

    public int getSatellites() {
        return mSatellites;
    }

    public void setSatellites(int count) {
        mSatellites = count;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    public float getDiffAge() {
        return mDiffAge;
    }

    public void setDiffAge(float age) {
        mDiffAge = age;
    }
}
