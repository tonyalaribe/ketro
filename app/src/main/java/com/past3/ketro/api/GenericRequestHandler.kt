package com.past3.ketro.api

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.past3.ketro.model.Wrapper
import retrofit2.Call

abstract class GenericRequestHandler<R> {
    /**
     * Perform API request
     *
     * @return Retrofit call
     */
    protected abstract fun makeRequest(): Call<R>

    /*
    Override to give custom implementation of Retrofit enque function
     */
    protected fun doRequestInternal(liveData: MutableLiveData<Wrapper<R>>) {
        val wrapper: Wrapper<R> = Wrapper()
        makeRequest().enqueue(object : ApiCallback<R>(getErrorHandler()) {
            override fun handleResponseData(data: R) {
                wrapper.data = data
                liveData.value = wrapper
            }

            override fun handleException(t: Exception) {
                wrapper.exception = t
                liveData.value = wrapper
            }
        })
    }

    fun doRequest(): LiveData<Wrapper<R>> {
        val liveData = MutableLiveData<Wrapper<R>>()
        executeRequest(liveData)
        return liveData
    }

    fun executeRequest(liveData: MutableLiveData<Wrapper<R>>) {
        doRequestInternal(liveData)
    }

    /*
    Override getErrorHandler() to return custom ApiErrorHandler object
    where error/exception creation mapping resides.
     */
    fun getErrorHandler():ApiErrorHandler?{
        return ApiErrorHandler()
    }
}