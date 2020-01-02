package com.tenke.lib_common.view;

/*
Copyright [2016] [Doğan Kılıç]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.databinding.BindingAdapter;

public class CustomedWebView extends WebView {

    public CustomedWebView(Context context) {
        super(context);
    }

    public CustomedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeWebSettings();
        setWebChromeClient(new WebChromeClient());
        setBackgroundColor(Color.TRANSPARENT);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebSettings() {
        WebSettings webSetting = getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
    }

    @BindingAdapter("app:loadurl")
    public static void loadUrl(CustomedWebView customedWebView, String url) {
        customedWebView.loadUrl(url);
    }

}
