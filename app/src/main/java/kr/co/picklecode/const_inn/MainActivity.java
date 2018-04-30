package kr.co.picklecode.const_inn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import bases.BaseActivity;
import bases.BaseWebViewActivity;
import bases.Configs;
import bases.callbacks.SimpleWebViewCallback;
import comm.model.UserModel;

public class MainActivity extends BaseWebViewActivity {

    private static final String TAG = "BaseWebViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        setWebView((WebView)findViewById(R.id.webView));
        setLoaderView(findViewById(R.id.loader));
        setBaseUrl(Configs.BASE_WEB_URL);
        setLoadAnimation(true);
        setHybridOption("pickle://", new SimpleWebViewCallback() {
            @Override
            public void callback(WebView webView, String string) {
                Log.e(TAG, "overrode url : " + string);
                switch (string){
                    case "getPushKey" : {
                        nativeCall_sendPushKey(UserModel.getFromPreference().getMessageToken());
                        break;
                    }
                    default: break;
                }
            }
        }, new SimpleWebViewCallback() {
            @Override
            public void callback(WebView webView, String string) {
                Log.e(TAG, "onPageFinished : " + string);
            }
        });

        this.moveWithinBase("");
    }

    @Override
    protected void setHybridOption(String scheme, SimpleWebViewCallback overrideAction, SimpleWebViewCallback finishAction) {
        super.setHybridOption(scheme, overrideAction, finishAction);
    }

    @Override
    protected void setBaseUrl(String baseUrl) {
        super.setBaseUrl(baseUrl);
    }

    private void nativeCall_sendPushKey(String pushKey){
        try {
            String encoded = URLEncoder.encode(pushKey, "UTF-8");
            this.loadUrl("javascript:getPushKeyCallBack(" + encoded + ")");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

}
