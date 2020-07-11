trait CriterioDeMontura extends ((Dragon, Vikingo) => Boolean)

object criterioDanio extends CriterioDeMontura {
  override def apply(dragon : Dragon, vikingo : Vikingo): Boolean = dragon.calcularDanio() > vikingo.calcularDanio()
}

case class criterioItem(item : Item) extends CriterioDeMontura{
  override def apply(dragon: Dragon, vikingo: Vikingo) : Boolean = vikingo.item.contains(item)
}

case object criterioFacilDeMontar extends CriterioDeMontura{
  override def apply(dragon: Dragon, vikingo: Vikingo): Boolean = true
}

case class Dragon (velocidadBase : Double = 60, peso : Double, raza : Raza){

  def calcularDanio() : Double = raza.mostrarDanio(this)

  def puedeMontar(vikingo: Vikingo) : Boolean = raza.validarCriterios(this, vikingo)

  def calcularVelocidad (): Double = velocidadBase - peso

  def puedeCargar() : Double = peso * 0.2
}


abstract class Raza(criteriosDeMontura : List[CriterioDeMontura]){
  def mostrarDanio(dragon: Dragon) : Double
  def validarCriterios(dragon: Dragon, vikingo: Vikingo) : Boolean = criteriosDeMontura.forall((criterioDeMontura : CriterioDeMontura) => criterioDeMontura(dragon, vikingo))
}

case class FuriaNocturna(danio : Double, criteriosDeMontura : List[CriterioDeMontura] ) extends Raza (criteriosDeMontura){
  def mostrarDanio(dragon: Dragon): Double = danio
}

case class NadderMortifero (criteriosDeMontura : List[CriterioDeMontura]) extends Raza (criteriosDeMontura){
  def mostrarDanio(dragon: Dragon) : Double = 150

  override def validarCriterios(dragon: Dragon, vikingo: Vikingo): Boolean = {
    super.validarCriterios(dragon, vikingo) && criterioDanio(dragon, vikingo)
  }
}

case class Gronckle (criteriosDeMontura : List[CriterioDeMontura], pesoMaximoSoportado : Double = 60) extends Raza (criteriosDeMontura){
  def mostrarDanio (dragon: Dragon) : Double = dragon.peso * 5

  override def validarCriterios(dragon: Dragon, vikingo: Vikingo): Boolean = {
    super.validarCriterios(dragon, vikingo) && (vikingo.statsBase.peso < this.pesoMaximoSoportado)
  }
}

