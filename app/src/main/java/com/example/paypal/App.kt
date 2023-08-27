package com.example.paypal

import android.app.Application
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.UserAction

/**
 * @author Perry Lance
 * @since 2023-08-27 Created
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        PayPalCheckout.setConfig(
            checkoutConfig = CheckoutConfig(
                application = this,
                clientId =
                PAYPAL_CLIENT_ID_SANDBOX,
                environment = Environment.SANDBOX,
                currencyCode = CurrencyCode.USD,
                userAction = UserAction.PAY_NOW,
                settingsConfig = SettingsConfig(
                    loggingEnabled = true,
                    showWebCheckout = false
                ),
                returnUrl = "com.example.paypal://paypalpay"
            )
        )
    }

    companion object {
        /**
         * You can get a Client ID by signing into your PayPal developer account or by signing up for one if
         * you haven't already.
         *
         * https://developer.paypal.com/developer/accounts/
         */
        const val PAYPAL_CLIENT_ID_SANDBOX =
            "Aai8pSvuCfxseUf0q5WyRqZjl6hiwP_ppL1Tvy8lnBWWYH_hYqSOk8FIvv1xWnSuQco7VlCkE3jN1e9u"
    }
}