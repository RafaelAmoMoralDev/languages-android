package learning.rafaamo.languages.domain.entity.user

interface IUser {

  val id: Long
  val name: String
  val email: String
  val phone: String?
  val description: String?
  val image: String?
  val likes: Int

}