package learning.rafaamo.languages.domain.datasource.remote.util

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type


class CallAdapter<R>(private val responseType: Type): CallAdapter<R, Call<ApiResponse<R>>> {

    override fun adapt(call: Call<R>): Call<ApiResponse<R>> = NetworkResponseCall(call)

    override fun responseType(): Type = responseType

}