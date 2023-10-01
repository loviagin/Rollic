package com.loviagin.rollic.presentation.confirmation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.loviagin.rollic.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.loviagin.rollic.activities.pro.UserPaidActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmationActivity : AppCompatActivity(R.layout.activity_confirmation) {

    private val viewModel: ConfirmationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra("ID")

        val text = findViewById<TextView>(R.id.url)
        val progress = findViewById<CircularProgressIndicator>(R.id.progress)

        viewModel.state.observe(this) {
            if (it.isError) {
                text.text = it.errorMessage
                progress.visibility = View.GONE
            }
            if (it.isLoading) {
                progress.visibility = View.VISIBLE
            }
            if (it.isSuccess) {
                if (it.payment?.paid == true) {
                    val intent = Intent(this, UserPaidActivity::class.java).apply {
                        putExtra("user", intent.getStringExtra("user"))
                        putExtra("cud", intent.getStringExtra("cud"))
                    }
                    startActivity(intent)
                } else {
                    text.text = "Payment canceled. Check your card"
                }
                progress.visibility = View.GONE
            }
        }

        id?.let(viewModel::checkPayment)
    }
}