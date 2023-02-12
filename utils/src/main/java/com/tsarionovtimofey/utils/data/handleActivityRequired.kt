package com.tsarionovtimofey.utils.data

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

fun ComponentActivity.handleActivityRequired(activityRequiredSet: Set<ActivityRequired>) {
    val activity = this
    activityRequiredSet.forEach {
        it.onActivityCreated(activity)
    }
    val observer = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            activityRequiredSet.forEach {
                it.onActivityCreated(activity)
            }
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            activityRequiredSet.forEach {
                it.onActivityStarted()
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            activityRequiredSet.forEach {
                it.onActivityStopped()
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            activityRequiredSet.forEach {
                it.onActivityDestroyed(isFinishing)
            }
        }
    }
    lifecycle.addObserver(observer)
}