package learning.rafaamo.languages.domain.datasource.remote.util

import com.google.gson.Gson
import learning.rafaamo.languages.domain.entity.AppError
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkResponseCall<S>(private val delegate: Call<S>) : Call<ApiResponse<S>> {

    override fun enqueue(callback: Callback<ApiResponse<S>>) {
        return delegate.enqueue(object : Callback<S> {

            override fun onResponse(call: Call<S>, response: Response<S>) {
                val formattedResponse: ApiResponse<S> = if (response.isSuccessful) {
                    if (response.body() == null || response.code() == 204) {
                        ApiResponse.Empty()
                    } else {
                        ApiResponse.Success(response.body()!!)
                    }
                } else {
                    if (response.code() == 500) {
                        ApiResponse.Error(null)
                    } else {
                        ApiResponse.Error(Gson().fromJson(response.errorBody()!!.charStream(), AppError::class.java))
                    }
                }
                callback.onResponse(this@NetworkResponseCall, Response.success(formattedResponse))
            }

            override fun onFailure(call: Call<S>, throwable: Throwable) {
                throwable.printStackTrace()
                callback.onResponse(this@NetworkResponseCall, Response.success(ApiResponse.NetworkError()))
            }
        })
    }

    override fun clone(): Call<ApiResponse<S>> {
        return NetworkResponseCall(delegate.clone())
    }

    override fun execute(): Response<ApiResponse<S>> {
        throw UnsupportedOperationException("NetworkResponseCall doesn't support execute")
    }

    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun cancel() {
        return delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }
    
}