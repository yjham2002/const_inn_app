package kr.co.picklecode.const_inn;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bases.BaseActivity;
import bases.Configs;
import bases.callbacks.SimpleCallback;
import comm.SimpleCall;
import comm.model.UserModel;

public class IntroActivity extends BaseActivity {

    private Handler introProcessHandler = new Handler();
    private Runnable introProcess = new Runnable() {
        @Override
        public void run() {
            Map<String, Object> params = new HashMap<>();

            SimpleCall.getHttpJson(Configs.BASE_URL + "/web/introprocess", params, new SimpleCall.CallBack() {
                @Override
                public void handle(JSONObject jsonObject) {
                    Intent i = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, new SimpleCallback() {
                @Override
                public void callback() {
                    // on Failure
                    showToast("네트워크에 연결할 수 없어 앱을 종료합니다.");
                    new Handler().postDelayed(exitRunnable, 3000);
                }
            });
        }
    };
    private Runnable exitRunnable = new Runnable() {
        public void run() {
            System.exit(0);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACTION_PERMISSION_ASKING:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        showToast("앱 이용에 필요한 권한을 얻을 수 없어 앱을 종료합니다.");
                        new Handler().postDelayed(exitRunnable, 3000);
                        return;
                    }
                }
                // all permissions were granted
                introProcessHandler.post(introProcess);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if(UserModel.getFromPreference() != null) Log.e("userInfo", UserModel.getFromPreference().toString());

        checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.introProcessHandler.removeCallbacks(introProcess);
        this.introProcessHandler.postDelayed(exitRunnable, 2000);
    }

}
