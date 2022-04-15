package com.example.eyescareapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.eyescareapp.service.EyeCareService;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private static Context mContext;
    private static final String TAG="MainActivity";
    private Button mOpenButton;
    private Button mCloseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mOpenButton = findViewById(R.id.open_eyes_care);
        mCloseButton = findViewById(R.id.close_eyes_care);
        mOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEyeCareMode();
            }
        });
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeEyeCareMode();
            }
        });
    }


    /**
     * 打开护眼模式
     *
     * @return
     */
    public static void openEyeCareMode() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(mContext)) { //有悬浮窗权限开启服务绑定 绑定权限
                Intent intent = new Intent(mContext, EyeCareService.class);
                mContext.startService(intent);
            } else { //没有悬浮窗权限,去开启悬浮窗权限
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    ((MainActivity) mContext).startActivityForResult(intent, 1234);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Intent intent = new Intent(mContext, EyeCareService.class);
            mContext.startService(intent);
        }
    }


    /**
     * 关闭护眼模式
     *
     * @return
     */
    public static void closeEyeCareMode() {
        Log.i(TAG, "closeEyeCareMode: ");
        Intent intent = new Intent(mContext, EyeCareService.class);
        mContext.stopService(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "openEyeCareMode onActivityResult: requestCode :" + requestCode);
        if (requestCode == 1234) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Log.i(TAG, "openEyeCareMode: 失败");
                    //权限授予失败，无法开启悬浮窗
                    return;
                } else {
                    Log.i(TAG, "openEyeCareMode: 成功");
                    //权限授予成功
                }//有悬浮窗权限开启服务绑定 绑定权限
            }
            Intent intent = new Intent(this, EyeCareService.class);
            startService(intent);
        } else if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(mContext, "not granted", Toast.LENGTH_SHORT);
                }
            }
        }
    }
}