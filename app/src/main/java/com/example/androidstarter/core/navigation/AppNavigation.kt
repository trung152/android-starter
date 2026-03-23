package com.example.androidstarter.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidstarter.feature.hello.presentation.HelloScreen

/**
 * Định nghĩa tất cả routes của app.
 * Khi thêm feature mới, chỉ cần thêm object vào đây và composable() vào NavHost.
 */
sealed class Screen(val route: String) {
    data object Hello : Screen("hello")
    // Thêm màn hình mới tại đây:
    // data object Profile : Screen("profile")
}

/**
 * AppNavigation - NavHost trung tâm của toàn bộ app.
 *
 * Mỗi feature chỉ expose composable Screen của mình,
 * việc điều hướng được tập trung tại đây.
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Hello.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Hello.route) {
            HelloScreen()
        }
        // Thêm destinations mới tại đây:
        // composable(Screen.Profile.route) { ProfileScreen(navController) }
    }
}
