package me.paladin.wifi.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import me.paladin.wifi.Locator
import me.paladin.wifi.models.AppTheme
import me.paladin.wifi.ui.main.viewmodels.ThemeViewModel
import me.paladin.wifi.ui.theme.WifiPasswordTheme
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<ThemeViewModel>(factoryProducer = { Locator.themeViewModelFactory })
    private lateinit var consentInformation: ConsentInformation
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val theme by viewModel.theme.collectAsState()
            val monet by viewModel.monet.collectAsState()

            val darkTheme = when (theme) {
                AppTheme.DAY -> false
                AppTheme.NIGHT -> true
                else -> isSystemInDarkTheme()
            }

            WifiPasswordTheme(darkTheme, monet) {
                MainNavGraph()
            }
        }
        val params = ConsentRequestParameters
            .Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(
            this,
            params, {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    this@MainActivity
                ) { loadAndShowError ->
                    // Consent gathering failed.
                    if (loadAndShowError != null) {
                        Log.w(
                            "AdMob", String.format(
                                "%s: %s",
                                loadAndShowError.errorCode,
                                loadAndShowError.message
                            )
                        )
                    }
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAdsSdk()
                    }
                }
            }, { requestConsentError ->
                // Consent gathering failed.
                Log.w("AdMob", String.format(
                    "%s: %s",
                    requestConsentError.errorCode,
                    requestConsentError.message))
            })
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.get()) {
            return
        }
        isMobileAdsInitializeCalled.set(true)

        // Initialize the Google Mobile Ads SDK.
        MobileAds.initialize(this)
    }
}