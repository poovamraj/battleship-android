package com.battleship.android.app.common

import android.os.Handler
import android.os.Looper

fun runOnMainThread(runnable: Runnable){
    Handler(Looper.getMainLooper()).post(runnable)
}