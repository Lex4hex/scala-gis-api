package entities

import persistence.entities._
import spray.json.DefaultJsonProtocol

object JsonProtocol extends DefaultJsonProtocol {
  implicit val buildingFormat       = jsonFormat4(Building)
  implicit val simpleBuildingFormat = jsonFormat3(SimpleBuilding)
  implicit val companyFormat        = jsonFormat4(Company)
  implicit val simpleCompanyFormat  = jsonFormat3(SimpleCompany)
}