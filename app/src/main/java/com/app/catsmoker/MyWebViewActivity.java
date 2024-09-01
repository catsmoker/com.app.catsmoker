package com.app.catsmoker;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MyWebViewActivity extends AppCompatActivity {

    private static final String HOME_URL = "https://catsmoker.github.io/";
    private WebView webView;
    private FirebaseAnalytics firebaseAnalytics;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        initializeFirebaseAnalytics();
        initializeViews();
        configureWebView();
        handleBackButton();
        loadWebViewContent();
    }

    private void initializeFirebaseAnalytics() {
        try {
            firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to initialize Firebase Analytics", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView() {
        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new CustomWebViewClient());
        webView.setWebChromeClient(new CustomWebChromeClient());
        webView.setDownloadListener(this::handleDownload);
    }

    private void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else if (!HOME_URL.equals(webView.getUrl())) {
                    webView.loadUrl(HOME_URL);
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void loadWebViewContent() {
        if (isConnectedToInternet()) {
            webView.loadUrl(HOME_URL);
        } else {
            loadLocalErrorPage();
        }
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }

    private void loadLocalErrorPage() {
        try (InputStream inputStream = getAssets().open("no_internet.html")) {
            byte[] buffer = new byte[inputStream.available()];
            int bytesRead = inputStream.read(buffer);
            if (bytesRead != -1) {
                String htmlContent = new String(buffer, StandardCharsets.UTF_8);
                webView.loadDataWithBaseURL(null, htmlContent, "text/html", StandardCharsets.UTF_8.name(), null);
            }
        } catch (IOException e) {
            Toast.makeText(this, "Failed to load local error page", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isInternalUrl(String url) {
        return url.contains("catsmoker.github.io") ||
                url.contains("github.com") ||
                url.contains("objects.githubusercontent.com") ||
                url.contains("foxsmoker.blogspot.com");
    }

    private void openExternalUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void handleDownload(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);

        if (!fileName.contains(".")) {
            fileName += "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimetype);
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType(mimetype);
        request.addRequestHeader("User-Agent", userAgent);
        request.addRequestHeader("Accept-Encoding", "identity");
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show();
            logFirebaseEvent("download_file", fileName, mimetype);
        } else {
            Toast.makeText(this, "DownloadManager is unavailable. Cannot download the file.", Toast.LENGTH_SHORT).show();
        }
    }

    private void logFirebaseEvent(String itemId, String itemName, String contentType) {
        if (firebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            logFirebaseEvent("web_view_page", url, "web_page");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (isInternalUrl(url)) {
                return false;
            } else {
                openExternalUrl(url);
                return true;
            }
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
        }
    }
}
