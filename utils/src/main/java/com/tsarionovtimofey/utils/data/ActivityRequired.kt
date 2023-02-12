package com.tsarionovtimofey.utils.data

import androidx.activity.ComponentActivity

abstract class ActivityRequired {

    internal abstract fun onActivityCreated(activity: ComponentActivity)

    internal abstract fun onActivityStarted()

    internal abstract fun onActivityStopped()

    internal abstract fun onActivityDestroyed(isFinishing: Boolean)
}