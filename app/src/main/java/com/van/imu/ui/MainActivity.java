package com.van.imu.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.van.comm.ImuVal;
import com.van.comm.Location;
import com.van.comm.VanPos;
import com.van.imu.R;
import com.van.imu.core.VanCore;
import com.van.imu.core.VanWork;
import com.van.imu.location.GnssManager;
import com.van.utils.Projection;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 2013; // 请求权限的唯一标识符
    private VanHandler handler; // 处理消息的Handler
    private TextView tvGnss; // 显示GNSS信息的TextView
    private TextView tvImu; // 显示IMU信息的TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // 设置系统栏的边距?
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();

        if (checkPermissions()) {
            VanCore.instance().start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onPause() {
        handler.removeMessages(0);
        super.onPause();
    }

    // 检查应用是否拥有必要的权限
    private boolean checkPermissions() {
        String[] items = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE // 需要写外部存储的权限
        };

        ArrayList<String> list = new ArrayList<>();

        // 检查每个权限是否已被授予
        for (String item : items) {
            if (checkSelfPermission(item) != PackageManager.PERMISSION_GRANTED) {
                list.add(item); // 如果未被授予，将其加入到列表中
            }
        }

        if (list.isEmpty()) {
            return true; // 如果所有权限已被授予，返回true
        }

        // 请求未被授予的权限
        items = list.toArray(new String[0]);
        requestPermissions(items, REQUEST_PERMISSION);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (REQUEST_PERMISSION == requestCode) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    // 如果有权限未被授予，提示用户并关闭应用
                    Toast.makeText(this, "no permission", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }

            VanCore.instance().start();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initView() {
        handler = new VanHandler(this);
        tvGnss = findViewById(R.id.gnss);
        tvImu = findViewById(R.id.imu);
    }

    // 内部静态类，处理定时消息以更新UI
    static class VanHandler extends Handler {
        private final WeakReference<MainActivity> mActivity; // 弱引用，防止内存泄漏

        public VanHandler(MainActivity activity) {
            super(activity.getMainLooper());

            mActivity = new WeakReference<>(activity);
        }

        public void handleMessage(@NonNull android.os.Message msg) {
            final MainActivity activity = mActivity.get();

            if (null != activity) {
                activity.updateImu(); // 更新IMU数据
                activity.updateGnss(); // 更新GNSS数据
            }

            sendEmptyMessageDelayed(0, 200); // 每200毫秒发送一次空消息，循环更新数据
        }
    }

    // 更新GNSS数据
    private void updateGnss() {
        Location location = GnssManager.instance().getLocation(); // 获取当前定位信息

        if (null == location) {
            tvGnss.setText("未定位"); // 如果无法获取定位信息，显示“未定位”
            return;
        }

        // 创建定位对象并将其转换为平面坐标
        VanPos pos = new VanPos(location.getLongitude(), location.getLatitude());
        Projection.toPlane(pos);

        // 格式化显示状态、方向、速度和坐标信息
        String str = String.format(Locale.US, "状态: %d, 方向: %.1f, 速度: %.1fkm/h \nX:%.3f, Y:%.3f, Z:%.3f",
                location.getState(), location.getDirection(), location.getSpeed(),
                pos.x(), pos.y(), location.getAltitude());
        // 更新显示
        Log.d("TAG", "updateGnss: " + str);
        tvGnss.setText(str);
    }

    // 更新IMU数据
    private void updateImu() {
        ImuVal val = new ImuVal(); // 创建IMU数据对象

        if (!VanWork.instance().getImu(val)) {
            tvGnss.setText("N/A"); // 如果无法获取IMU数据，显示“N/A”
            return;
        }

        // 格式化并显示角速度和加速度数据
        String str = String.format(Locale.US,
                "角速度 X: %.3f, Y: %.3f, Z: %.3f \n加速度 X: %.3f, Y: %.3f, Z: %.3f",
                val.gyroX, val.gyroY, val.gyroZ, val.accelX, val.accelY, val.accelZ);
        // 更新显示
        Log.d("TAG", "updateImu: " + str);
        tvImu.setText(str);
    }
}
