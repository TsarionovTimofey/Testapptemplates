package com.tsarionovtimofey.utils.data

import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.reflect.KProperty

class ActivityRequiredDelegate(
    private val activity: ComponentActivity,
    private val activityRequiredSet: Set<ActivityRequired>
) : DefaultLifecycleObserver {

    init {
        activity.lifecycle.addObserver(this)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = activityRequiredSet

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
            it.onActivityDestroyed(activity.isFinishing)
        }
    }
}