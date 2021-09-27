package com.rikoriko.rctst.utils;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.rikoriko.rctst.R;

public class WebViewActivity extends AppCompatActivity {

    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;

    private View globView;
    private WebView actionView;
    private FrameLayout frameLayout;
    public ValueCallback<Uri[]> uploadMessage;
    private ValueCallback<Uri> mUploadMessage;
    private Webbbbb webbbbb;
    private WebChromeClient.CustomViewCallback viewCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        frameLayout = (FrameLayout) findViewById(R.id.frame);
        actionView = findViewById(R.id.webView);

        WebSettings settings = actionView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setUseWideViewPort(true);
        settings.setDisplayZoomControls(false);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLoadWithOverviewMode(false);
        actionView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
                String message = "SSL Certificate error. Sorry. \nContinue?";

                builder.setTitle("SSL Certificate Error");
                builder.setMessage(message);
                builder.setPositiveButton("Ok", (dialog, which) -> handler.proceed());
                builder.setNegativeButton("Cancel", (dialog, which) -> handler.cancel());
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        };

        webbbbb = new Webbbbb();
        actionView.setWebChromeClient(webbbbb);
        actionView.setWebViewClient(webViewClient);
        //actionView.loadUrl(Linker.shared().urlWithParams(this));
        actionView.loadUrl(getIntent().getExtras().getString("gitUrl"));

        //Log.d("MyLog", "web " + getIntent().getExtras().getString("gitUrl"));

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.acceptCookie();
        cookieManager.setAcceptFileSchemeCookies(true);
        cookieManager.getInstance().setAcceptCookie(true);
        cookieManager.getCookie(getIntent().getExtras().getString("gitUrl"));
    }

    public boolean inCustomView() {
        return (globView != null);
    }

    public void hideCustomView() {
        webbbbb.onHideCustomView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(this, "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        actionView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        actionView.restoreState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (inCustomView()) {
            hideCustomView();
            return;
        }
        if ((globView == null) && actionView.canGoBack()) {
            actionView.goBack();
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        actionView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (inCustomView()) {
            hideCustomView();
        }
    }

    class Webbbbb extends WebChromeClient {
        private View mVideoProgressView;

        @Override
        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }
            uploadMessage = filePathCallback;
            Intent intent = new Intent("android.intent.action.GET_CONTENT").addCategory(Intent.CATEGORY_OPENABLE).setType("image/*");
            //Intent intent = fileChooserParams.createIntent();
            try {
                startActivityForResult(intent, REQUEST_SELECT_FILE);
            } catch (ActivityNotFoundException e) {
                uploadMessage = null;
                Toast.makeText(WebViewActivity.this, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (globView != null) {
                callback.onCustomViewHidden();
                return;
            }
            globView = view;
            actionView.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            frameLayout.addView(view);
            viewCallback = callback;
        }

        @Override
        public View getVideoLoadingProgressView() {
            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(WebViewActivity.this);
                mVideoProgressView = inflater.inflate(R.layout.load_state, null);
            }
            return mVideoProgressView;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (globView == null)
                return;

            actionView.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);

            globView.setVisibility(View.GONE);

            frameLayout.removeView(globView);
            viewCallback.onCustomViewHidden();

            globView = null;
        }
    }
}

