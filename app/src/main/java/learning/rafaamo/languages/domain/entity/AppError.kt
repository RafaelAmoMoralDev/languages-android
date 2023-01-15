package learning.rafaamo.languages.domain.entity

open class AppError(open val code: Long, open val message: String, open val errors: List<ErrorResponse>) {
  enum class Code(val code: Long) { GENERIC(0L) }
}

data class ErrorResponse(
  val type: String,
  val causes: List<String>
)