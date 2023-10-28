package me.paladin.wifi.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    settingsAction: () -> Unit
) {
    Scaffold(
        bottomBar = {
            Column {
                AdmobBanner()
                MainNavBar(navController)
            }
        },

        contentWindowInsets = WindowInsets.navigationBars
    ) { paddingValues ->
        BottomBarNavGraph(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            settingsAction = settingsAction
        )
    }
}

@Composable
fun AdmobBanner() {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}