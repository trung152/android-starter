package com.example.androidstarter

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * BaseApplication - Entry point của toàn bộ ứng dụng.
 * @HiltAndroidApp kích hoạt code generation của Hilt,
 * tạo ra AppComponent làm gốc cho toàn bộ dependency graph.
 */
@HiltAndroidApp
class BaseApplication : Application()
