package learning.rafaamo.languages.domain.datasource.remote.util

import learning.rafaamo.languages.domain.entity.AppError


sealed class ApiResponse<T> {
  data class Success<T>(val body: T) : ApiResponse<T>()
  class Empty<T> : ApiResponse<T>()
  data class Error<T>(val errorsResponse: AppError?) : ApiResponse<T>()
  class NetworkError<T> : ApiResponse<T>()
}