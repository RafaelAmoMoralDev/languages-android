package learning.rafaamo.languages.domain.datasource.remote.util

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


class CallAdapterFactory: CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        val responseType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = getRawType(responseType)
        if (rawObservableType != ApiResponse::class.java) {
            throw IllegalArgumentException("type must be a resource")
        }
        if (responseType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be a parametizer")
        }

        val bodyType = getParameterUpperBound(0, responseType)
        return CallAdapter<Any>(bodyType)
    }

}