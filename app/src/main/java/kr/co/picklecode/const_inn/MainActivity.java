package kr.co.picklecode.const_inn;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import bases.BaseActivity;
import bases.BaseWebViewActivity;
import bases.Configs;
import bases.Constants;
import bases.callbacks.SimpleWebViewCallback;
import bases.utils.PreferenceUtil;
import comm.model.UserModel;

public class MainActivity extends BaseWebViewActivity {

    private static final String TAG = "BaseWebViewActivity";

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("MainActivity", "onReceive : " + intent);
            if(intent.getExtras() == null && !intent.getExtras().containsKey("action")) return;
            Log.e("MainActivity", "onReceiveExtra : " + intent.getExtras());
            final String action = intent.getExtras().getString("action");
            switch (action){
                case "gotoRecruit" :  {
                    if(UserModel.isSatisfied()){
                        UserModel userModel = UserModel.getFromPreference();
                        MainActivity.this.moveWithinBase("/pages/mypage/applyInfo.php?byPush=1&userId=" + userModel.getUserNo());
                    }
                }
                break;
                default: break;
            }
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_NOTIFICATION.REP_FILTER));
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

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
                        if(UserModel.isSatisfied()){
                            nativeCall_sendPushKey(UserModel.getFromPreference().getMessageToken());
                        }else{
                            nativeCall_sendPushKey(PreferenceUtil.getString("pKeyPickle"));
                        }
                        break;
                    }
                    case "cropImage" : {
                        nativeCall_cropImage();
                        break;
                    }
                    case "logout" : {
                        nativeCall_logout();
                        break;
                    }
                    default: {
                        if(string.contains("?")) {
                            String paramCall = string.substring(0, string.indexOf("?"));
                            String params = string.substring(string.indexOf("?") + 1, string.length());
                            Log.e(TAG, "overrode url(param) : [" + paramCall + "] [" + params + "]");

                            switch (paramCall){
                                case "loginProcess" : {
                                    try{
                                        int userNo = Integer.parseInt(params.substring(params.indexOf("id") + 3, params.length()));
                                        nativeCall_loginProcess(params.contains("auto=true"), userNo);
                                    }catch (NumberFormatException e){
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                                default: break;
                            }
                        }
                        break;
                    }
                }
            }
        }, new SimpleWebViewCallback() {
            @Override
            public void callback(WebView webView, String string) {
                if(string.contains("mypageMain.php")){
                    if(window != null) {
                        MainActivity.this.webView.removeView(window);
                        MainActivity.this.window.destroy();
                        MainActivity.this.window = null;
                    }
                }
                Log.e(TAG, "onPageFinished : " + string);
            }
        });

        this.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                Log.e("onCreateWindow", "SOWED");
                WebView newWebView = new WebView(view.getContext());
                newWebView.getSettings().setJavaScriptEnabled(true);
                newWebView.getSettings().setSupportZoom(true);
                newWebView.getSettings().setBuiltInZoomControls(true);
                newWebView.getSettings().setSupportMultipleWindows(true);
                newWebView.setWebChromeClient(new WebChromeClient(){
                    @Override
                    public void onCloseWindow(WebView window) {
                        super.onCloseWindow(window);
                        webView.removeView(window);
                        Log.e("onCreateWindow", "CLOSED CALLED");
                    }
                });
                view.addView(newWebView);
                MainActivity.this.window = newWebView;
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                return true;
            }
            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
                webView.removeView(window);
                Log.e("onCreateWindow", "CLOSED CALLED");
            }
        });

        UserModel userModel = UserModel.getFromPreference();

        if(UserModel.isSatisfied()){
            if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("isRedirect") && getIntent().getExtras().getBoolean("isRedirect")){
                this.moveWithinBase("/pages/mypage/applyInfo.php?byPush=1&userId=" + userModel.getUserNo());
            }else{
                final boolean autoLogin = userModel.isAutoLogin();
                this.moveWithinBase(String.format("?auto=%s&id=%d", Boolean.toString(autoLogin), userModel.getUserNo()));
            }
        }else{
            this.moveWithinBase("");
        }
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
        if(pushKey == null) {
            pushKey = "";
        }
        try {
            String encoded = URLEncoder.encode(pushKey, "UTF-8");
            this.loadUrl("javascript:getPushKeyCallBack(\'" + encoded + "\')");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    private void nativeCall_cropImage(){
        takeAlbumAndUpload(Configs.BASE_URL + "/imgUpload", new Handler(){
            @Override
            public void handleMessage(Message msg){
                Log.e("ResponseCall", msg.getData().toString());
                String jsonString = msg.getData().getString("jsonString");
                try {
                    if(jsonString == null || jsonString.equals("")) throw new IOException();
                    JSONObject json_obj = new JSONObject(jsonString);
                    final String imgPath = json_obj.getString("data");
                    nativeCall_sendImageMeta(imgPath);
                    Log.e("returned", imgPath);
                    showToast("이미지가 성공적으로 업로드되었습니다.");
                }catch (Exception e){
                    showToast("이미지를 업로드하는 중 오류가 발생하였습니다.");
                    e.printStackTrace();
                }
            }
        });
    }

    private void nativeCall_loginProcess(boolean isAutoLogin, int userNo){
        UserModel userModel = UserModel.getFromPreference();
        if(!UserModel.isSatisfied()){
            userModel = new UserModel();
        }
        userModel.setAutoLogin(isAutoLogin);
        userModel.setUserNo(userNo);
        userModel.saveAsPreference();

        this.moveWithinBase("pages/search/searchMain.php");
    }

    private void nativeCall_logout(){
        String pKey = "";
        if(UserModel.isSatisfied()){
            pKey = UserModel.getFromPreference().getMessageToken();
        }
        UserModel userModel = new UserModel();
        userModel.setAutoLogin(false);
        userModel.setMessageToken(pKey);
        userModel.saveAsPreference();

        this.moveWithinBase("");
    }

    private void nativeCall_sendImageMeta(String path){
        try {
            String encoded = URLEncoder.encode(path, "UTF-8");
            this.loadUrl("javascript:recvImageMeta(\'" + encoded + "\')");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

}
