package learning.rafaamo.languages.domain.datasource.remote.entity.user

interface IUserResponse {

  val id: Long
  val name: String
  val email: String
  val phone: String?
  val description: String?
  val image: String?
  val likes: Int

}