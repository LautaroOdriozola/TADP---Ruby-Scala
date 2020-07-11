trait Participante {
  def puedeCargar() : Double
  def calcularDanio() : Double
  def calcularVelocidad() : Double
  def mostrarBarbarosidad() : Int
  def mostrarHambre() : Int
  def estaEquipado () : Boolean
  def tieneItem(itemDeseado : Item) : Boolean
  def esMejorQue(otroParticipante: Participante) (posta: Posta) : Boolean ={
    posta(this, otroParticipante)
  }
  def incrementarHambre(cuanto : Int) : Participante
  def mejorMontura (posta : Posta, dragones : List[Dragon]): Participante
  def monturaActual() : Option[Dragon]
  def desmontarse () : Participante

  def puedeParticiparPorHambre(posta: Posta) : Boolean ={
    val nuevoParticipante = this.incrementarHambre(posta.incrementoDeHambre())
    nuevoParticipante.mostrarHambre() <= 100
  }

}

case class Vikingo(statsBase: StatsBase, nivel : Int = 1, hambre : Int = 0, item : Option[Item] = None) extends Participante {

  def montar (dragonAMontar: Dragon) : Jinete = {
    if (dragonAMontar.puedeMontar(this)) {
      Jinete(vikingo = this, dragon = dragonAMontar)
    }else throw new RuntimeException ("No se pudo montar al dragon")
  }

  def calcularDanio() : Double = item match {
    case arma : Some[Arma] => arma.get.danioExtra + statsBase.danioBase
    case _ => statsBase.danioBase
  }

  def puedeCargar() : Double = statsBase.peso / 2 + statsBase.barbarosidad * 2

  def calcularVelocidad() : Double = statsBase.velocidad

  override def mostrarBarbarosidad(): Int = statsBase.barbarosidad

  override def estaEquipado(): Boolean = item.isDefined

  override def tieneItem(itemDeseado: Item): Boolean = item.contains(itemDeseado)

  override def incrementarHambre(cuanto: Int): Vikingo = this.copy(hambre = this.hambre + cuanto)

  override def mostrarHambre(): Int = this.hambre

  def mejorMontura (posta : Posta, dragones : List[Dragon]): Participante = {
    val posiblesDragones = dragones.filter(dragon => dragon.puedeMontar(this))
    if(posiblesDragones.isEmpty) return this
    val mejorDragon = posiblesDragones.maxBy(dragon => posta.puntuacion(this.montar(dragon)))
    if (posta.puntuacion(this.montar(mejorDragon)) > posta.puntuacion(this)) this.montar(mejorDragon) else this
  }

  override def desmontarse(): Participante = this

  override def monturaActual(): Option[Dragon] = None

}

case class StatsBase (peso : Double, velocidad : Double, barbarosidad : Int, danioBase : Double)

case class Jinete (vikingo: Vikingo, dragon: Dragon) extends Participante {

  def calcularDanio () : Double = vikingo.calcularDanio() + dragon.calcularDanio()

  def calcularVelocidad () : Double = dragon.calcularVelocidad() - vikingo.statsBase.peso

  def puedeCargar () : Double = dragon.puedeCargar() - vikingo.statsBase.peso

  override def mostrarBarbarosidad(): Int = vikingo.mostrarBarbarosidad()

  override def estaEquipado(): Boolean = vikingo.estaEquipado()

  override def tieneItem(itemDeseado: Item): Boolean = vikingo.tieneItem(itemDeseado)

  override def incrementarHambre(cuanto: Int): Jinete = this.copy(vikingo = this.vikingo.incrementarHambre(5))

  override def mostrarHambre(): Int = this.vikingo.mostrarHambre()

  override def mejorMontura(posta: Posta, dragones: List[Dragon]): Participante = this

  override def desmontarse(): Participante = this.vikingo

  override def monturaActual(): Option[Dragon] = Some(dragon)

}

// Creacion de vikingos predeterminados

object hipo extends Vikingo(statsBase = StatsBase(peso = 40, velocidad = 30, barbarosidad = 10, danioBase = 5), nivel = 1, hambre = 0, item = Some(SistemaDeVuelo)){}

object astrid extends Vikingo(statsBase = StatsBase(peso = 40, velocidad = 30, barbarosidad = 10, danioBase = 5), nivel = 1, hambre = 0, item = Some(Arma (danioExtra = 30))){}

object patan extends Vikingo(statsBase = StatsBase(peso = 40, velocidad = 30, barbarosidad = 10, danioBase = 5), nivel = 1, hambre = 0, item = Some(Arma (danioExtra = 100))){}

object patapez extends Vikingo(statsBase = StatsBase(peso = 40, velocidad = 30, barbarosidad = 10, danioBase = 5), nivel = 1, hambre = 0, item = Some(ItemComestible (40)) ){
  override def incrementarHambre(cuanto: Int): Vikingo ={
    var porcentaje = 0
    item match {
      case itemComes : Some[ItemComestible] => porcentaje = itemComes.get.porcentajeComida
      case _ => porcentaje = 0
    }

    var hambreIncrementada = (this.hambre + cuanto*2) - porcentaje
    if(hambreIncrementada <= 0) { hambreIncrementada = 0 }
    this.copy(hambre = hambreIncrementada )
  }

  override def puedeParticiparPorHambre(posta: Posta): Boolean = this.mostrarHambre() <= 50
}