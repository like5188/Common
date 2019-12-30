package com.like.common.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val IO_EXECUTOR: ExecutorService = Executors.newSingleThreadExecutor()
val NETWORK_EXECUTOR: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors().coerceAtLeast(2))!!// 至少为 2 最大与 CPU 数相同
val MAIN_EXECUTOR = MainThreadExecutor()

fun ioThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}

fun networkThread(f: () -> Unit) {
    NETWORK_EXECUTOR.execute(f)
}

fun mainThread(f: () -> Unit) {
    if (Looper.getMainLooper() === Looper.myLooper()) f() else MAIN_EXECUTOR.execute(f)
}

class MainThreadExecutor : Executor {
    private val mainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    override fun execute(command: Runnable) {
        mainThreadHandler.post(command)
    }

}