package com.loviagin.rollic.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.loviagin.rollic.R
import com.loviagin.rollic.databinding.ActivityCustomWebviewBinding
import com.loviagin.rollic.presentation.confirmation.ConfirmationActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecureWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomWebviewBinding

    private var repeating: Boolean = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.close.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        val url = intent.getStringExtra("URL")

        if (url == null) {
            finish()
            return
        }

        binding.webview.apply {
            settings.javaScriptEnabled = true
            webViewClient = CustomWebClient()
            loadUrl(url)
        }
    }

    private inner class CustomWebClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {

            Log.d("Tag","openConfirmation: webview ${request.url.toString()}")

            return if (request.url.toString() == getString(R.string.redirectUrl)) {

                val id = intent.getStringExtra("ID")
                Log.d("TAG", "openConfirmation: from web view")

                if (repeating) {
                    return true
                }

                startActivity(Intent(applicationContext, ConfirmationActivity::class.java).apply {
                    putExtra("ID", id)
                    putExtra("user", intent.getStringExtra("user"))
                    putExtra("cud", intent.getStringExtra("cud"))
                })
                finish()

                repeating = true

                true
            } else {
                false
            }
        }
    }
}