package bases;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;

import comm.Comm;

public class BaseApp extends Application {

    private boolean mBounded;

    public static final String ADMOB_AD_ID = "";
    private static Context context;

    static{
        Comm.call();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("BaseApp", "onCreate");

        this.context = this.getApplicationContext();
//        MobileAds.initialize(this, ADMOB_AD_ID);
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
    }

}
