package learning.rafaamo.languages.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import learning.rafaamo.languages.common.AppAuthentication
import learning.rafaamo.languages.domain.entity.AppError
import learning.rafaamo.languages.domain.entity.ErrorResponse
import learning.rafaamo.languages.domain.entity.user.IUser
import learning.rafaamo.languages.domain.datasource.local.UserDAO
import learning.rafaamo.languages.domain.datasource.local.dto.UserDTO
import learning.rafaamo.languages.domain.datasource.remote.API
import learning.rafaamo.languages.domain.datasource.remote.UserAPI
import learning.rafaamo.languages.domain.datasource.remote.util.*
import learning.rafaamo.languages.domain.entity.user.AppUser
import learning.rafaamo.languages.domain.entity.user.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
  api: API,
  private val appUserAuthentication: AppAuthentication,
  private val userDAO: UserDAO
) {

  private val api: UserAPI = api


  class SignInError(override val code: Long, override val message: String, override val errors: List<ErrorResponse>): AppError(code, message, errors) {
    enum class Code(val code: Long) { USER_NOT_FOUND(1L), INVALID_PASSWORD(2L) }
  }

  suspend fun signIn(email: String, password: String): Resource<Unit, SignInError> {
    return when (val response = api.signIn(email, password)) {
      is ApiResponse.Success -> {
        appUserAuthentication.storeUserToken(response.body.authToken)
        Resource.Success(Unit)
      }
      is ApiResponse.Empty -> {
        Resource.Error(null)
      }
      is ApiResponse.Error -> {
        val appError = response.errorsResponse

        if (appError == null) {
          Resource.Error(null)
        } else {
          Resource.Error(SignInError(appError.code, appError.message, appError.errors))
        }
      }
      is ApiResponse.NetworkError -> {
        Resource.Error(null)
      }
    }
  }

  suspend fun signUp(email: String, name: String, password: String): Resource<Unit, AppError> {
    return when (val response = api.signUp(email, name, password)) {
      is ApiResponse.Success -> {
        appUserAuthentication.storeUserToken(response.body.authToken)
        Resource.Success(Unit)
      }
      is ApiResponse.Empty -> Resource.Error(null)
      is ApiResponse.Error -> {
        Resource.Error(response.errorsResponse)
      }
      is ApiResponse.NetworkError ->  Resource.Error(null)
    }
  }

  suspend fun me(): Flow<Resource<AppUser, AppError>> {
    return flow {
      emit(Resource.Load(null))

      when (val response = api.me()) {
        is ApiResponse.Success -> {
          val user = response.body
          emit(Resource.Success(AppUser(user.id, user.name, user.email, user.phone, user.description, user.image, user.likes)))
        }
        is ApiResponse.Empty -> emit(Resource.Error(null))
        is ApiResponse.Error -> {
          emit(Resource.Error(response.errorsResponse))
        }
        is ApiResponse.NetworkError -> emit(Resource.Error(null))
      }
    }
  }

  suspend fun get(id: Long): Flow<Resource<User, AppError>> {
    return flow {
      emit(Resource.Load(null))

      when (val response = api.get(id)) {
        is ApiResponse.Success -> {
          val user = response.body
          userDAO.insert(UserDTO(user.id, user.name, user.email, user.phone, user.description, user.image, user.likes, user.liked))
          userDAO.get(user.id).distinctUntilChanged().collect { userDTO ->
            emit(Resource.Success(User(userDTO.id, userDTO.name, userDTO.email, userDTO.phone, userDTO.description, userDTO.image, userDTO.likes, userDTO.liked)))
          }
        }
        is ApiResponse.Empty -> emit(Resource.Error(null))
        is ApiResponse.Error -> {
          emit(Resource.Error(response.errorsResponse))
        }
        is ApiResponse.NetworkError -> emit(Resource.Error(null))
      }
    }
  }

  suspend fun like(user: User): Resource<Unit, AppError> {
    val likes = if (user.liked) {
      user.likes - 1
    } else {
      user.likes + 1
    }

    userDAO.like(user.id, !user.liked, likes)

    return when (val response = api.like(user.id)) {
      is ApiResponse.Success -> {
        Resource.Success(Unit)
      }
      is ApiResponse.Empty -> Resource.Error(null)
      is ApiResponse.Error -> {
        Resource.Error(response.errorsResponse)
      }
      is ApiResponse.NetworkError ->  Resource.Error(null)
    }
  }

}