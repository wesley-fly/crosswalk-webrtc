package com.internal.voip;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.Toast;


import org.xwalk.core.ClientCertRequest;
import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkActivity;
import org.xwalk.core.XWalkHttpAuthHandler;
import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkWebResourceRequest;
import org.xwalk.core.XWalkWebResourceResponse;

public class MainActivity extends XWalkActivity {
    private final String TAG = "XWalkActivity-Test";
    private XWalkView xWalkWebView;
    private XWalkSettings xWVSettings;

    //以下两个url需要vpn
//    private String mUrl = "https://appr.tc/r/18629586538?vrc=H264&vsc=H264";
    private String mUrl = "https://appr.tc/r/18629586538?vrc=H264&debug=loopback&vsc=H264";

    public class JsInterface {
        @JavascriptInterface
        public void greet(String name) {
            System.out.println("Hello, " + name);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xWalkWebView = findViewById(R.id.xWalkView);
    }

    @Override
    public void onXWalkReady() {
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
//        XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, true);

        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
        XWalkPreferences.setValue(XWalkPreferences.ALLOW_UNIVERSAL_ACCESS_FROM_FILE, true);
        XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
//        XWalkPreferences.setValue(XWalkPreferences.PROFILE_NAME, true);
        XWalkPreferences.setValue(XWalkPreferences.SPATIAL_NAVIGATION, true);
//        XWalkPreferences.setValue(XWalkPreferences.ENABLE_THEME_COLOR, true);
        XWalkPreferences.setValue(XWalkPreferences.ENABLE_JAVASCRIPT, true);
        XWalkPreferences.setValue(XWalkPreferences.ENABLE_EXTENSIONS, true);

        xWalkWebView.addJavascriptInterface(new JsInterface(),"NativeInterface");

        //获取setting
        xWVSettings = xWalkWebView.getSettings();
        xWVSettings.setSupportZoom(true);//支持缩放
        xWVSettings.setBuiltInZoomControls(true);//可以任意缩放
        xWVSettings.setLoadWithOverviewMode(true);
        xWVSettings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        xWVSettings.setLoadsImagesAutomatically(true);
        //调用JS方法.安卓版本大于17,加上注解@JavascriptInterface
        xWVSettings.setJavaScriptEnabled(true);//支持JS
        xWVSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        xWVSettings.setSupportMultipleWindows(false);

        xWVSettings.setAllowFileAccess(true);
        xWVSettings.setDomStorageEnabled(true);
        xWVSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        xWVSettings.setAllowUniversalAccessFromFileURLs(true);
//        xWVSettings.setMediaPlaybackRequiresUserGesture(true);

        xWalkWebView.setUIClient(new XWalkUIClient(xWalkWebView) {
            @Override
            public void onPageLoadStarted(XWalkView view, String url) {
                super.onPageLoadStarted(view, url);
            }

            @Override
            public boolean onJsAlert(XWalkView view, String url, String message, XWalkJavascriptResult result) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onScaleChanged(XWalkView view, float oldScale, float newScale) {
                if (view != null) {
                    view.invalidate();
                }
                super.onScaleChanged(view, oldScale, newScale);
            }

            @Override
            public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
                super.onPageLoadStopped(view, url, status);
            }

            @Override
            public boolean onCreateWindowRequested(XWalkView view, InitiateBy initiator, final ValueCallback<XWalkView> callback) {
                // Create a temporary XWalkView instance and set a custom XWalkUIClient
                // to it with the setUIClient method. The url is passed as a parameter to the
                // XWalkUIClient.onPageLoadStarted method.
                Log.e(TAG, "onCreateWindowRequested　地址：" + view.getUrl());

//                return super.onCreateWindowRequested(view, initiator, callback);
                XWalkView tempView = new XWalkView(MainActivity.this);
                tempView.setUIClient(new XWalkUIClient(tempView) {
                    @Override
                    public void onPageLoadStarted(XWalkView view, String url) {
                        Log.e(TAG, "onCreateWindowRequested -> onPageLoadStarted url : " + url);
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                        startActivity(intent);
//                        view.loadUrl(url);
                    }
                });

                // Return the temporary XWalkView instance with the callback to start the page
                // load process in tempView.
                callback.onReceiveValue(tempView);
                return true;
            }
        });

        xWalkWebView.setResourceClient(new XWalkResourceClient(xWalkWebView) {
            @Override
            public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
//                Log.e(TAG, "shouldOverrideUrlLoading url : " + url);
                view.loadUrl(url);
                return true;
            }
            @Override
            public XWalkWebResourceResponse shouldInterceptLoadRequest(XWalkView view, XWalkWebResourceRequest request) {
//                Log.e(TAG, "shouldInterceptLoadRequest url : " + equest.getUrl().toString());
                return super.shouldInterceptLoadRequest(view, request);
            }

            @Override
            public void onReceivedSslError(XWalkView view, ValueCallback<Boolean> callback, SslError error) {
                //super.onReceivedSslError(view, callback, error);
                Log.e(TAG, "onReceivedSslError");
                Toast.makeText(MainActivity.this, "证书不合法", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);
            }

            @Override
            public void onLoadStarted(XWalkView view, String url) {
                super.onLoadStarted(view, url);
            }

            @Override
            public void onProgressChanged(XWalkView view, int progressInPercent) {
                super.onProgressChanged(view, progressInPercent);
            }

            @Override
            public void onReceivedClientCertRequest(XWalkView view, ClientCertRequest handler) {
                super.onReceivedClientCertRequest(view, handler);
            }

            @Override
            public void onDocumentLoadedInFrame(XWalkView view, long frameId) {
                super.onDocumentLoadedInFrame(view, frameId);
            }

            @Override
            public void onReceivedHttpAuthRequest(XWalkView view, XWalkHttpAuthHandler handler, String host, String realm) {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }

            @Override
            public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {
                super.onReceivedLoadError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedResponseHeaders(XWalkView view, XWalkWebResourceRequest request, XWalkWebResourceResponse response) {
                super.onReceivedResponseHeaders(view, request, response);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        3332);
            } else {
                xWalkWebView.loadUrl(mUrl);
            }
        } else {
            xWalkWebView.loadUrl(mUrl);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 3332) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请授予录音，拍照等权限" + permissions[i], Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        xWalkWebView.loadUrl(mUrl);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (xWalkWebView.getNavigationHistory().canGoBack()) {
                xWalkWebView.getNavigationHistory().navigate(XWalkNavigationHistory.Direction.BACKWARD, 1);
            } else {
                //finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        if (xWalkWebView != null) {
            xWalkWebView.onDestroy();
            xWalkWebView = null;
            System.gc();
        }

        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (xWalkWebView != null) {
            xWalkWebView.onNewIntent(intent);
        }
    }
}
