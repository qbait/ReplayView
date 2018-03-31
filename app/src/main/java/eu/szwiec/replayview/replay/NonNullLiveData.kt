package eu.szwiec.replayview.replay

import android.arch.lifecycle.MutableLiveData

class NonNullLiveData<T>(private val defaultValue: T) : MutableLiveData<T>() {

    override fun getValue(): T {
        return super.getValue() ?: defaultValue
    }

}