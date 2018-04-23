package bases;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import bases.callbacks.SimpleWebViewCallback;

public abstract class BaseWebViewActivity extends BaseActivity {

    protected WebView webView;
    protected String baseUrl;

    protected abstract void setWebView(WebView webView);

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
                        if(overrideAction != null) overrideAction.callback(view, url);
                        return true;
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(finishAction != null) finishAction.callback(view, url);

                    super.onPageFinished(view, url);
                }
            });

            webView.getSettings().setJavaScriptEnabled(true);
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

}
