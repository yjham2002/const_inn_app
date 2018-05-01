package kr.co.picklecode.const_inn;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONObject;

import java.io.IOException;
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
                    case "cropImage" : {
                        nativeCall_cropImage();
                        break;
                    }
                    case "setAutoLoginTrue" : {
                        // TODO
                        break;
                    }
                    case "setAutoLoginFalse" : {
                        // TODO
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

    private void nativeCall_sendImageMeta(String path){
        try {
            String encoded = URLEncoder.encode(path, "UTF-8");
            this.loadUrl("javascript:recvImageMeta(\'" + encoded + "\')");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

}
