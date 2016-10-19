package persistence.entities

trait BaseEntity {
  val id: Int

  def isValid: Boolean = true
}