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
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MyWebViewActivity extends AppCompatActivity {

    private static final String HOME_URL = "https://catsmoker.github.io/";
    private WebView webView;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        initializeViews();
        configureWebView();
        handleBackButton();
        loadWebViewContent();
    }

    private void initializeViews() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView() {
        // Enable hardware acceleration for better performance
        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);

        // WebView settings
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);

        // Security settings
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(false);
        webView.getSettings().setSafeBrowsingEnabled(true);

        // Set custom clients
        webView.setWebViewClient(new CustomWebViewClient());
        webView.setWebChromeClient(new CustomWebChromeClient());

        // Handle downloads
        webView.setDownloadListener(this::handleDownload);
    }

    private void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    // If the WebView can go back, navigate back
                    webView.goBack();
                } else if (!HOME_URL.equals(webView.getUrl())) {
                    // If the current URL is not the home URL, load the home URL
                    webView.loadUrl(HOME_URL);
                } else {
                    // If the current URL is the home URL, navigate to MainActivity
                    Intent intent = new Intent(MyWebViewActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close the current activity
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
                url.contains("catsmoker-lab.blogspot.com");
    }

    private void openExternalUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void handleDownload(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if (!isValidUrl(url)) {
            Toast.makeText(this, "Invalid download URL", Toast.LENGTH_SHORT).show();
            return;
        }

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
        } else {
            Toast.makeText(this, "DownloadManager is unavailable. Cannot download the file.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
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