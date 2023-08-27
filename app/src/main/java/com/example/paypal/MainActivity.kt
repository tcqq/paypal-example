package com.example.paypal

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
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}