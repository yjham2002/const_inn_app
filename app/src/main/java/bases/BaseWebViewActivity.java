package bases;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import bases.callbacks.SimpleWebViewCallback;

public abstract class BaseWebViewActivity extends BaseActivity {

    protected WebView webView;
    protected View loader;
    protected String baseUrl;
    private boolean isLoadAnimationEnabled = false;

    protected void setWebView(WebView webView){
        this.webView = webView;
    }

    protected void setLoaderView(View loader){
        this.loader = loader;
    }

    protected void loadUrl(String url){
        Log.e(this.getClass().getSimpleName(), "loadUrl : " + url);
        this.webView.loadUrl(url);
    }

    protected void setLoadAnimation(boolean enabled){
        this.isLoadAnimationEnabled = enabled;
    }

    protected void setHybridOption(
            final String scheme,
            final SimpleWebViewCallback overrideAction,
            final SimpleWebViewCallback finishAction
            ) {
        if(webView == null){
            throw new NullPointerException("BaseWebViewActivity.setWebView() not called or is set as null.");
        }else{
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if(url.startsWith(scheme)){
                        if(overrideAction != null) overrideAction.callback(view, url.replaceFirst(scheme, ""));
                        return true;
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    if(isLoadAnimationEnabled && loader != null) loader.setVisibility(View.VISIBLE);
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(isLoadAnimationEnabled && loader != null) loader.setVisibility(View.INVISIBLE);
                    if(finishAction != null) finishAction.callback(view, url);
                    super.onPageFinished(view, url);
                }
            });

            webView.getSettings().setJavaScriptEnabled(true);
//            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setWebChromeClient(new WebChromeClient());
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.setHorizontalScrollBarEnabled(true);
            webView.setVerticalScrollBarEnabled(true);
            webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
            webView.setBackgroundColor(0);
        }
    }

    protected void setBaseUrl(String baseUrl){
        this.baseUrl = baseUrl;
    }

    protected void moveWithinBase(String additional){
        if(webView != null){
            String toMove = this.baseUrl;
            if(this.baseUrl.endsWith("/") && additional.startsWith("/")){
                toMove = this.baseUrl + additional.substring(1);
            }else if((this.baseUrl.endsWith("/") && !additional.startsWith("/")) || !this.baseUrl.endsWith("/") && additional.startsWith("/")){
                toMove = this.baseUrl + additional;
            }else{
                toMove = this.baseUrl + "/" + additional;
            }

            webView.loadUrl(toMove);
        }else{
            throw new NullPointerException("BaseWebViewActivity.setWebView() not called or is set as null.");
        }
    }

    protected boolean isAfterHistoryBase(){
        final String currentUrl = this.webView.getUrl();
        if(currentUrl.equals(this.baseUrl) || currentUrl.equals(this.baseUrl + "/") || currentUrl.equals(this.baseUrl + "#") || currentUrl.equals(this.baseUrl + "/#")){
            return false;
        }
        return true;
    }

    private boolean mFlag = false;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 0){
                mFlag=false;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)){
            if(this.isAfterHistoryBase()){
                this.webView.goBack();
                return false;
            }else{
                if(!mFlag) {
                    Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                    mFlag = true;
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                    return false;
                } else {
                    finish();
                    System.exit(0);
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
