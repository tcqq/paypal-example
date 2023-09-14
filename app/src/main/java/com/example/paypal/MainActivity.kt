package com.example.paypal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.paypal.databinding.ActivityMainBinding
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CreateOrderActions
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.CaptureOrderResult

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    private val clientId = "Aai8pSvuCfxseUf0q5WyRqZjl6hiwP_ppL1Tvy8lnBWWYH_hYqSOk8FIvv1xWnSuQco7VlCkE3jN1e9u"
    private val redirectUri = "com.example.paypal://paypalpay"
    private val authUrl =
        "${getPayPalBaseUrl(PayPalEnvironment.SANDBOX)}/signin/authorize?client_id=$clientId&redirect_uri=$redirectUri&response_type=code&scope=openid profile"

    private val paypalSdk: PayPalCheckout
        get() = PayPalCheckout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        paypalSdk.registerCallbacks(
            onApprove = OnApprove { approval ->
                approval.orderActions.capture { result ->
                    when (result) {
                        is CaptureOrderResult.Success -> {
                            showToast("Payment successful")
                            Log.d(tag, "Payment successful: $result)")
                            val orderId = result.orderResponse?.id.orEmpty()
                            val paymentId =
                                result.orderResponse?.purchaseUnits?.firstOrNull()?.payments?.captures?.firstOrNull()?.id.orEmpty()
                        }

                        is CaptureOrderResult.Error -> {
                            showToast("Payment failed")
                            Log.d(tag, "payment-failed: $result")
                        }
                    }
                }
            },
            onCancel = OnCancel {
                showToast("Payment canceled")
                Log.d(tag, "Payment canceled")
            },
            onError = OnError { errorInfo ->
                showToast("Payment failed")
                Log.d(tag, "Payment failed: $errorInfo")
            }
        )

        binding.payment.setOnClickListener {
            paypalSdk.startCheckout(object : CreateOrder {
                override fun create(createOrderActions: CreateOrderActions) {
                    val orderId = binding.orderId.text.toString()
                    if (orderId.isNotEmpty()) {
                        createOrderActions.set(binding.orderId.text.toString())
                    } else {
                        showToast("Please enter paypal order id")
                    }
                }
            })
        }


        binding.authorization.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
            startActivity(browserIntent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri = intent.data
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            val authorizationCode = uri.getQueryParameter("code")
            Log.d("MainActivity", "authorizationCode: $authorizationCode")
            binding.authCode.setText(authorizationCode)
        }
    }

    private fun getPayPalBaseUrl(environment: PayPalEnvironment): String {
        return when (environment) {
            PayPalEnvironment.LIVE -> "https://www.paypal.com"
            PayPalEnvironment.SANDBOX -> "https://www.sandbox.paypal.com"
        }
    }

    enum class PayPalEnvironment {
        LIVE, SANDBOX
    }


    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}