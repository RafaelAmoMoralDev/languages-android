package learning.rafaamo.languages.data.datasource.remote.util

sealed class Resource<Data, Error> {
  data class Load<Data, Error>(val data: Data?): Resource<Data, Error>()
  data class Success<Data, Error>(val data: Data): Resource<Data, Error>()
  data class Error<Data, Error>(val error: Error?): Resource<Data, Error>()
}