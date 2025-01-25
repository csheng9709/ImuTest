package com.van.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private final static char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String getHexString(byte[] data, int length) {
        if (null == data) {
            return "";
        } else if (data.length < length) {
            length = data.length;
        }

        if (0 == length) {
            return "";
        }

        StringBuilder sb = new StringBuilder(data.length*3);

        for (int i = 0; i < length; i++) {
            int value = data[i] & 0xff;
            sb.append(HEX[value/16]).append(HEX[value%16]).append(' ');
        }

        return sb.substring(0, sb.length()-1);
    }

    public static void showHex(byte[] data, int length) {
        System.out.println("--- " + getHexString(data, length));
    }

    public static long uptime() {
        return SystemClock.elapsedRealtime();
    }

    public static int dp2px(int dp) {
        float ds = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * ds + 0.5f);
    }

    public static String getSdCardPath1() {
        String path = "";

        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.contains("/storage/")) {
                    continue;
                }

                if (line.contains("emulated")) {
                    continue;
                }

                if (line.contains("fuse")) {
                    String[] columns = line.split(" ");
                    if (columns!=null && columns.length>1) {
                        if (columns[1].startsWith("/")) {
                            path = columns[1];
                        } else {
                            path = columns[2];
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }

    public static List<String> getSdCardPath(Context context) {
        List<String> dirs = new ArrayList<>();
        StorageManager manager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        List<StorageVolume> list = manager.getStorageVolumes();
        if (null==list || 0==list.size()) {
            return dirs;
        }

        String path = "";
        Class<?> storageVolumeClazz = null;

        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");

            for (StorageVolume volume : list) {
                path = (String) getPath.invoke(volume);
                boolean removable = (Boolean) isRemovable.invoke(volume);
                if (removable) {
                    dirs.add(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dirs;
    }

    public static boolean copyFile(String oldPathName, String newPathName) {
        try {
            File oldFile = new File(oldPathName);
            if (!oldFile.exists()) {
                Log.e("copyFile", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("copyFile", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("copyFile", "copyFile:  oldFile cannot read.");
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(oldPathName);
            FileOutputStream fileOutputStream = new FileOutputStream(newPathName);
            byte[] buffer = new byte[4096];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
