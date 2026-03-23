package com.example.androidstarter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.androidstarter.core.designsystem.theme.AppTheme
import com.example.androidstarter.core.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity - Single Activity của app (Single Activity Architecture).
 *
 * @AndroidEntryPoint: Kích hoạt Hilt injection cho Activity này.
 * Toàn bộ navigation được uỷ quyền cho AppNavigation (NavHost).
 * Activity này KHÔNG biết gì về các màn hình cụ thể.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppNavigation()
            }
        }
    }
}