package com.example.dynamicwebview

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.SearchView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.util.stream.Collectors

class MainActivity : AppCompatActivity() {
    private val DEFAULT_URL = "https://music.youtube.com"

    private lateinit var searchView: SearchView
    private lateinit var webView: WebView
    private lateinit var webViewSwipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.initXMLComponents()
        this.initWebViewSwipeRefreshLayout()
        this.initSearchView()
        this.initWebView()
    }

    override fun onBackPressed() {
        if (this.webView.canGoBack())
            this.webView.goBack()
        else
            super.onBackPressed()
    }

    private fun initXMLComponents() {
        this.webView = this.findViewById(R.id.webView)
        this.webViewSwipeRefreshLayout = this.findViewById(R.id.webViewSwipeRefreshLayout)
        this.searchView = this.findViewById(R.id.searchView)
    }

    private fun initSearchView() {
        this.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if(URLUtil.isValidUrl(it))
                        webView.loadUrl(it)
                    else {
                        val sanitizedIt: String = it.split(Regex("\\s+")).joinToString("+")
                        webView.loadUrl("$DEFAULT_URL/search?q=$sanitizedIt")
                    }
                }
                return false
            }
        })
    }

    private fun initWebView() {
        this.webView.webChromeClient = object : WebChromeClient() {}
        this.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // next code is implemented to use WebView for another urls not related with YouTube Music
                //searchView.setQuery(url, false)
                webViewSwipeRefreshLayout.isRefreshing = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webViewSwipeRefreshLayout.isRefreshing = false
            }
        }
        this.webView.settings.javaScriptEnabled = true
        this.webView.loadUrl(DEFAULT_URL)
    }

    private fun initWebViewSwipeRefreshLayout() {
        this.webViewSwipeRefreshLayout.setOnRefreshListener {
            this.webView.reload()
        }
    }
}