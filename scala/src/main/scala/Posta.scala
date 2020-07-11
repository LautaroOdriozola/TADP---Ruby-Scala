abstract class Posta(incrementoDeHambre : Int) extends ((Participante, Participante) => Boolean){

  override def apply(participante : Participante, otroParticipante : Participante): Boolean = puntuacion(participante) > puntuacion(otroParticipante)

  def iniciarPosta (listaParticipantes : List[Participante]): List[Participante] ={

    val participantesAptos = listaParticipantes.filter(puedeParticipar)

    competir(participantesAptos.map(participante => participante.incrementarHambre(incrementoDeHambre)))

  }

  def competir (particpantesAptos : List[Participante]) : List[Participante] ={
    val resultados = particpantesAptos.sortWith((participante1 : Participante, participante2 : Participante)=> participante1.esMejorQue(participante2) (this))
    resultados
  }

  def puedeParticiparPorHambre(participante : Participante) : Boolean ={
    val nuevoParticipante = participante.incrementarHambre(incrementoDeHambre)
    nuevoParticipante.mostrarHambre() <= 100
  }

  def puedeParticipar (participante : Participante) : Boolean = {
    participante.puedeParticiparPorHambre(this)
  }

  def puntuacion (participante: Participante) : Double

  def incrementoDeHambre() : Int = incrementoDeHambre

}

case class combate (requisitoBarbarosidad : Double = 50) extends Posta (incrementoDeHambre = 10) {

  override def puedeParticipar(participante: Participante): Boolean = {
    super.puedeParticipar(participante) && (participante.mostrarBarbarosidad() >= requisitoBarbarosidad || participante.estaEquipado())

  }

  override def puntuacion(participante: Participante): Double = participante.calcularDanio()
}
case class pesca(pesoMinimo : Option[Double] = None) extends Posta (incrementoDeHambre = 5){

  override def puedeParticipar(participante: Participante): Boolean = {
    super.puedeParticipar(participante) && (if (pesoMinimo.isDefined) participante.puedeCargar() >= pesoMinimo.get else true)
  }

  override def puntuacion(participante: Participante): Double = participante.puedeCargar()
}

case class carrera(distanciaCarrera : Double, requiereMontura : Boolean = false) extends Posta (incrementoDeHambre = distanciaCarrera.toInt) {

  override def puedeParticipar(participante: Participante): Boolean = {
    super.puedeParticipar(participante) && (
      if (requiereMontura) participante match {
        case Vikingo(_,_,_,_) => false
        case Jinete(_,_) => true
      }else true
    )
  }

  override def puntuacion(participante: Participante): Double = participante.calcularVelocidad()
}
